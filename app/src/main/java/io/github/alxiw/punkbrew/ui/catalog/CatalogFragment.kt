package io.github.alxiw.punkbrew.ui.catalog

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_DETAILS_TAG
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_FAVORITES_TAG
import io.github.alxiw.punkbrew.ui.details.DetailsFragment
import io.github.alxiw.punkbrew.ui.favorites.FavoritesFragment
import io.github.alxiw.punkbrew.ui.list.BeersFragment
import kotlinx.android.synthetic.main.fragment_beers.*
import kotlinx.android.synthetic.main.item_beer.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class CatalogFragment : BeersFragment() {

    override val viewModel: CatalogViewModel by viewModel()

    private var hasSearchFocus = false
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        restoreSavedInstanceState(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.searchBeers(viewModel.currentQuery)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
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

        val searchItem = menu.findItem(R.id.catalog_menu_search)
        searchItem.let { item ->
            val searchManager
                    = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
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
    }

    override fun setupToolbar() {
        beers_toolbar.also {
            (activity as AppCompatActivity).setSupportActionBar(it)
            it.setNavigationIcon(R.drawable.ic_favorites)
            it.setNavigationOnClickListener { onFavoritesClicked() }
        }
    }

    override fun initView() {
        super.initView()
        viewModel.beers.observe(this, Observer {
            Timber.d("Received list of beers with size of: ${it.size}")
            if (it.size > 0) {
                onContentReceived()
            } else {
                onEmptyContent()
            }
            adapter.submitList(it)
        })

        viewModel.networkErrors.observe(this, Observer {
            Timber.d("Network error: %s", it ?: "...")
            it?.let {
                showNetworkError(it)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SEARCH_FOCUS_KEY, searchView?.hasFocus() ?: false)
    }

    override fun onDestroyView() {
        searchView = null
        super.onDestroyView()
    }

    private fun restoreSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            hasSearchFocus = savedInstanceState.getBoolean(SEARCH_FOCUS_KEY, false)
        }
    }

    private fun searchByName(query: String) {
        beers_recycler_view.scrollToPosition(0)
        viewModel.searchBeers(query)
        adapter.submitList(null)
    }

    private fun showNetworkError(text: String?) {
        val message = requireContext().applicationContext.getString(R.string.format_error, text)
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun onBeerClicked(beer: BeerEntity) {
        viewModel.hideKeyboard(requireContext().applicationContext, searchView)
        searchView?.clearFocus()
        requireFragmentManager().beginTransaction()
            .replace(
                R.id.root_container,
                DetailsFragment.newInstance(beer.id),
                BACK_STACK_DETAILS_TAG
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onFavoriteBadgeClicked(beer: BeerEntity, itemView: View) {
        beer.favorite = !beer.favorite
        viewModel.updateBeer(beer) {
            val mainHandler = Handler(requireContext().mainLooper)
            val runnable = Runnable {
                itemView.item_favorite.setImageResource(
                    if (beer.favorite) {
                        R.drawable.badge_favorite_true
                    } else {
                        R.drawable.badge_favorite_false
                    }
                )
            }
            mainHandler.post(runnable)
        }
    }

    private fun onFavoritesClicked() {
        viewModel.hideKeyboard(requireContext().applicationContext, searchView)
        searchView?.clearFocus()
        requireFragmentManager().beginTransaction()
            .replace(
                R.id.root_container,
                FavoritesFragment.newInstance(),
                BACK_STACK_FAVORITES_TAG
            )
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private const val SEARCH_FOCUS_KEY = "search_focus"

        fun newInstance(): CatalogFragment {
            return CatalogFragment()
        }
    }
}
