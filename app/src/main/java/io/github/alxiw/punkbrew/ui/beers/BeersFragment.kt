package io.github.alxiw.punkbrew.ui.beers

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_DETAILS_TAG
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_FAVORITES_TAG
import io.github.alxiw.punkbrew.ui.RefreshBeerListener
import io.github.alxiw.punkbrew.ui.details.DetailsFragment
import io.github.alxiw.punkbrew.ui.favorites.FavoritesFragment
import io.github.alxiw.punkbrew.util.getFormattedBeerName
import io.github.alxiw.punkbrew.util.hide
import io.github.alxiw.punkbrew.util.show
import kotlinx.android.synthetic.main.fragment_beers.*
import kotlinx.android.synthetic.main.item_beer.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class BeersFragment : Fragment(), RefreshBeerListener {

    private val viewModel: BeersViewModel by viewModel()

    private var searchView: SearchView? = null
    private var adapter: BeersAdapter = BeersAdapter()

    private var hasSearchFocus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        restoreSavedInstanceState(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.searchBeers(viewModel.currentQuery)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.beers_menu, menu)

        val favoritesItem = menu.findItem(R.id.beers_menu_favorites)
        favoritesItem?.setOnMenuItemClickListener {
            onFavoritesClicked()
            true
        }

        val searchItem = menu.findItem(R.id.beers_menu_search)
        searchItem?.let {
            val searchManager
                    = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView = searchItem.actionView as SearchView
            searchView?.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            searchView?.isIconified = true
            viewModel.currentQuery?.let {
                searchItem.expandActionView()
                searchView?.setQuery(it, false)
                if (!hasSearchFocus) {
                    searchView?.clearFocus()
                }
            }
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(s: String): Boolean {
                    searchView?.clearFocus()
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
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beers, container, false)

        adapter.setOnItemClickListener(object : BeersAdapter.OnItemClickListener {
            override fun onItemClick(beer: BeerEntity) {
                onBeerClicked(beer)
            }

            override fun onItemFavoriteBadgeClick(beer: BeerEntity, itemView: View) {
                onBeerFavoriteBadgeClicked(beer, itemView)
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        beers_recycler_view.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }

        viewModel.beers.observe(this, Observer {
            Timber.d("Received list of beers with size of: ${it.size}")
            if (it.size > 0) {
                showRecyclerView()
            } else {
                showEmptyList()
            }
            adapter.submitList(it)
        })

        viewModel.networkErrors.observe(this, Observer {
            Timber.d("Network error: $it")
            showNetworkError(it)
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SEARCH_FOCUS_KEY, searchView?.hasFocus() ?: false)
    }

    override fun onDestroyView() {
        searchView = null
        beers_recycler_view.adapter = null
        super.onDestroyView()
    }

    private fun setupToolbar() {
        beers_toolbar.also {
            it.setLogo(R.mipmap.ic_launcher)
            (activity as AppCompatActivity).setSupportActionBar(it)
        }
    }

    private fun restoreSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            hasSearchFocus = savedInstanceState.getBoolean(SEARCH_FOCUS_KEY, false)
        }
    }

    private fun searchByName(query: String) {
        beers_recycler_view.scrollToPosition(0)
        viewModel.searchBeers(getFormattedBeerName(query))
        adapter.submitList(null)
    }

    private fun showProgressBar() {
        beers_progress_bar.show()
        beers_recycler_view.hide()
        beers_empty_list.hide()
    }

    private fun showEmptyList() {
        beers_empty_list.show()
        beers_recycler_view.hide()
        beers_progress_bar.hide()
    }

    private fun showRecyclerView() {
        beers_recycler_view.show()
        beers_progress_bar.hide()
        beers_empty_list.hide()
    }

    private fun showNetworkError(text: String) {
        Toast.makeText(activity, "⚠️ $text", Toast.LENGTH_LONG).show()
    }

    private fun onFavoritesClicked() {
        requireFragmentManager().beginTransaction()
            .replace(
                R.id.root_container,
                FavoritesFragment.newInstance(),
                BACK_STACK_FAVORITES_TAG
            )
            .addToBackStack(null)
            .commit()
    }

    private fun onBeerClicked(beer: BeerEntity) {
        requireFragmentManager().beginTransaction()
            .replace(
                R.id.root_container,
                DetailsFragment.newInstance(beer.id),
                BACK_STACK_DETAILS_TAG
            )
            .addToBackStack(null)
            .commit()
    }

    private fun onBeerFavoriteBadgeClicked(beer: BeerEntity, itemView: View) {
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

    override fun refreshBeersAdapter() {
        viewModel.beers.value?.dataSource?.addInvalidatedCallback {
            adapter.notifyDataSetChanged()
        }
        viewModel.beers.value?.dataSource?.invalidate()
    }

    companion object {
        private const val SEARCH_FOCUS_KEY = "search_focus"

        fun newInstance(): BeersFragment {
            return BeersFragment()
        }
    }
}
