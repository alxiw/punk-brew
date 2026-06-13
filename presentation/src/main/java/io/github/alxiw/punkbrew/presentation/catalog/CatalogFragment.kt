package io.github.alxiw.punkbrew.presentation.catalog

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.google.android.material.appbar.AppBarLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.alxiw.punkbrew.presentation.R
import io.github.alxiw.punkbrew.presentation.list.BeersFragment
import io.github.alxiw.simplesearchview.SimpleSearchView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CatalogFragment : BeersFragment(), MenuProvider {

    override val viewModel: CatalogViewModel by viewModel()

    private var shouldScrollToTop = false

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu, menu)
        val item = menu.findItem(R.id.action_search)
        binding.beersSearch.setMenuItem(item)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        // set to true if click handling is needed
        return false
    }

    override fun setupToolbar() {
        binding.toolbar.apply {
            (activity as AppCompatActivity).setSupportActionBar(this)
            setNavigationIcon(R.drawable.ic_favorites)
            setNavigationOnClickListener { onFavoritesClicked() }
        }

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun initView(view: View) {
        super.initView(view)

        viewModel.searchBeers(viewModel.currentQuery)

        binding.beersSearch.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val oldQuery = viewModel.currentQuery
                Log.d("HELLO", "[CF] On query text submit, old query is <${oldQuery ?: "NULL"}>, new query is <${query}>")
                binding.beersSearch.clearFocus()
                searchByName(query)

                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                val oldQuery = viewModel.currentQuery
                Log.d("HELLO", "[CF] On query text change, old query is <${oldQuery ?: "NULL"}>, new query is <${query}>")
                searchByName(query)

                return true
            }

            override fun onQueryTextCleared(): Boolean {
                val oldQuery = viewModel.currentQuery
                Log.d("HELLO", "[CF] On query text cleared, old query is <${oldQuery ?: "NULL"}>")
                searchByName("")

                return true
            }
        })

        binding.beersSearch.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                with(binding.toolbarContainer) {
                    val params = layoutParams as AppBarLayout.LayoutParams
                    params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                            AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or
                            AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
                    layoutParams = params
                }
                onBackAction()
            }

            override fun onSearchViewShown() {
                with(binding) {
                    val params = toolbarContainer.layoutParams as AppBarLayout.LayoutParams
                    params.scrollFlags = 0 // disable scroll
                    toolbarContainer.layoutParams = params
                    beersAppBarLayout.setExpanded(true, true)
                }
            }

            override fun onSearchViewShownAnimation() = Unit
            override fun onSearchViewClosedAnimation() = Unit
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.beers.collect { list ->
                        list?.let {
                            adapter.submitList(it) {
                                if (shouldScrollToTop) {
                                    binding.beersRecyclerView.scrollToPosition(0)
                                    shouldScrollToTop = false
                                }
                            }
                        }
                    }
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.beersSearch.onBackPressed()) {
                        onBackAction()
                    } else {
                        // if search is not open, disable the callback
                        // and pass the event up to Activity
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        )
    }

    override fun onBeerUpdated() {
        Log.d("HELLO", "[CF] OnBeerUpdated: dataSource=${viewModel.beers.value?.dataSource}, isInvalid=${viewModel.beers.value?.dataSource?.isInvalid}")
        viewModel.searchBeers(viewModel.currentQuery, force = true)
    }

    private fun onBackAction() {
        binding.beersSearch.setQuery("", false)
        searchByName("")
    }

    private fun searchByName(query: String) {
        if (viewModel.searchBeers(query.trim())) {
            if (isResumed) {
                shouldScrollToTop = true
            }
        }
    }

    private fun onFavoritesClicked() {
        navigator.openFavorites()
    }

    companion object {
        const val BACK_STACK_CATALOG_TAG = "BACK_STACK_CATALOG_TAG"

        fun newInstance(): CatalogFragment {
            return CatalogFragment()
        }
    }
}
