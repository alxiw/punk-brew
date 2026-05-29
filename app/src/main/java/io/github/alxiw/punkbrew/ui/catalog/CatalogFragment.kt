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
        // если будет обработка клика, то тогда сделать true
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

    override fun initView(view: View) {
        super.initView(view)

        binding.beersSearch.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val oldQuery = viewModel.currentQuery
                Log.d("HELLO", "On query text submit, old query is <$oldQuery>, new query is <$query>")
                // val oldQuery = viewModel.state.value.query
                // viewModel.accept(UiAction.Search(query = query.trim()))
                binding.beersSearch.clearFocus()
                searchByName(query)

                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                val oldQuery = viewModel.currentQuery
                Log.d("HELLO", "On query text change, old query is <$oldQuery>, new query is <$query>")
                // val oldQuery = viewModel.state.value.query
                // viewModel.accept(UiAction.Search(query = query.trim())) // query is empty
                searchByName(query)

                return true
            }

            override fun onQueryTextCleared(): Boolean {
                val oldQuery = viewModel.currentQuery
                Log.d("HELLO", "On query text cleared, old query is <$oldQuery>")
                // val oldQuery = viewModel.state.value.query
                // viewModel.accept(UiAction.Search(query = ""))
                binding.beersSearch.clearFocus()
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

        /*viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state
                .map { it.query }
                .distinctUntilChanged()
                .collect { beersSearch.setQuery(it, true) }
        }*/

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
        binding.beersSearch.setQuery("", false)
        searchByName("")
        binding.beersRecyclerView.scrollToPosition(0)
    }

    private fun searchByName(query: String) {
        if (viewModel.searchBeers(getFormattedBeerName(query))) {
            binding.beersRecyclerView.scrollToPosition(0)
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
