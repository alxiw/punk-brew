package io.github.alxiw.punkbrew.ui.catalog

import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_DETAILS_TAG
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_FAVORITES_TAG
import io.github.alxiw.punkbrew.ui.details.DetailsFragment
import io.github.alxiw.punkbrew.ui.favorites.FavoritesFragment
import io.github.alxiw.punkbrew.ui.list.BeersFragment
import io.github.alxiw.punkbrew.util.getFormattedBeerName
import io.github.alxiw.simplesearchview.SimpleSearchView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CatalogFragment : BeersFragment(), MenuProvider {

    override val viewModel: CatalogViewModel by viewModel()

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu, menu)
        val item = menu.findItem(R.id.action_search)
        binding.beersSearch.setMenuItem(item)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun setupToolbar() {
        binding.toolbar.also {
            (activity as AppCompatActivity).setSupportActionBar(it)
            it.setNavigationIcon(R.drawable.ic_favorites)
            it.setNavigationOnClickListener { onFavoritesClicked() }
        }

        val menuHost = requireActivity()
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED)
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_catalog, menu)

        val filterItem = menu.findItem(R.id.catalog_menu_filter)
        filterItem.let {
            if (viewModel.filterEnabled) {
                it.setIcon(R.drawable.ic_menu_filter_true)
            }
        }
        filterItem.setOnMenuItemClickListener {
            viewModel.filterEnabled = !viewModel.filterEnabled
            if (viewModel.filterEnabled) {
                it.setTitle(R.string.menu_filter_true_title)
                it.setIcon(R.drawable.ic_menu_filter_true)
                Toast.makeText(context, context?.getString(R.string.toast_filter_enabled), Toast.LENGTH_SHORT).show()
            } else {
                it.setTitle(R.string.menu_filter_false_title)
                it.setIcon(R.drawable.ic_menu_filter_false)
                Toast.makeText(context, context?.getString(R.string.toast_filter_disabled), Toast.LENGTH_SHORT).show()
            }
            true
        }
        filterItem.isVisible = false

        val searchItem = menu.findItem(R.id.catalog_menu_search)
        searchItem.let { item ->
            val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView = item.actionView as SearchView
            searchView?.let { view ->
                view.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
                view.isIconified = true
                viewModel.currentQuery?.let { query ->
                    item.expandActionView()
                    view.setQuery(query, false)
                    if (!hasSearchFocus) {
                        view.clearFocus()
                    }
                }
                view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(s: String): Boolean {
                            view.clearFocus()
                            hasSearchFocus = false
                            searchByName(s)
                            return true
                        }

                        override fun onQueryTextChange(s: String): Boolean {
                            hasSearchFocus = true
                            searchByName(s)
                            return true
                        }
                    })
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }*/

    override fun initView(view: View) {
        super.initView(view)

        binding.beersSearch.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                /*val oldQuery = uiState.value.query
                Log.d("HELLO", "On query text submit, old query is <$oldQuery>, new query is <$query>")

                if (oldQuery != query) {
                    Log.d("HELLO", "Query changing from <$oldQuery> to <$query> submitted")
                    binding.beersSearch.clearFocus()
                    onQueryChanged(UiAction.Search(query = query.trim()))
                }*/

                view.clearFocus()
                searchByName(query)

                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                /*val oldQuery = uiState.value.query
                Log.d("HELLO", "On query text change, old query is <$oldQuery>, new query is <$query>")

                if (query.isEmpty() && oldQuery != query) {
                    Log.d("HELLO", "Query text changing from <$oldQuery> to <$query> applied")
                    job?.cancel()
                    job = CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        binding.beersSearch.clearFocus()
                        onQueryChanged(UiAction.Search(query = query.trim())) // query is empty
                    }
                } else {
                    job?.cancel()
                }*/
                searchByName(query)

                return true
            }

            override fun onQueryTextCleared(): Boolean {
                /*val oldQuery = uiState.value.query
                Log.d("HELLO", "On query text cleared, old query is <$oldQuery>")

                if (oldQuery.isNotEmpty()) {
                    Log.d("HELLO", "Query text changing from <$oldQuery> to <> applied")
                    binding.beersSearch.clearFocus()
                    onQueryChanged(UiAction.Search(query = ""))
                }*/
                view.clearFocus()
                searchByName("")

                return true
            }
        })

        binding.beersSearch.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewClosed() = onBackAction()
            override fun onSearchViewShown() = Unit
            override fun onSearchViewShownAnimation() = Unit
            override fun onSearchViewClosedAnimation() = Unit
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.beers.collect {
                        it?.let { list ->
                            Log.d("HELLO", "Received list of beers with size of: ${list.size}")
                            adapter.submitList(list)
                        }
                    }
                }

                launch {
                    viewModel.networkErrors.collect { error ->
                        Log.d("HELLO", "Network error: ${error ?: "..."}")
                        error?.let { text -> showNetworkError(text) }
                    }
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.beersSearch.onBackPressed()) {
                        onBackAction()
                    } else {
                        // Если поиск не открыт, отключаем callback и пробрасываем событие дальше (в Activity)
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        )

        viewModel.searchBeers(viewModel.currentQuery)
    }

    private fun onBackAction() {
        searchByName("")
        binding.beersRecyclerView.scrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun searchByName(query: String) {
        if (viewModel.searchBeers(getFormattedBeerName(query))) {
            binding.beersRecyclerView.scrollToPosition(0)
            adapter.submitList(null)
        }
    }

    private fun showNetworkError(text: String?) {
        val message = requireContext().applicationContext.getString(R.string.format_error, text)
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun onBeerClicked(beer: BeerEntity) {
        viewModel.hideKeyboard(requireContext().applicationContext, null)
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.root_container,
                DetailsFragment.newInstance(beer.id),
                BACK_STACK_DETAILS_TAG
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onFavoriteBadgeClicked(beer: BeerEntity, itemView: View) {
        val updatedBeer = beer.copy(favorite = !beer.favorite)
        viewModel.updateBeer(updatedBeer) {
            itemView.findViewById<ImageView>(R.id.item_favorite).setImageResource(
                if (updatedBeer.favorite) {
                    R.drawable.badge_favorite_true
                } else {
                    R.drawable.badge_favorite_false
                }
            )
        }
    }

    private fun onFavoritesClicked() {
        viewModel.hideKeyboard(requireContext().applicationContext, null)
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.root_container,
                FavoritesFragment.newInstance(),
                BACK_STACK_FAVORITES_TAG
            )
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance(): CatalogFragment {
            return CatalogFragment()
        }
    }
}
