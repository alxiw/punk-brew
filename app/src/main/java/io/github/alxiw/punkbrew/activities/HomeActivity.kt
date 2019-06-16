package io.github.alxiw.punkbrew.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import io.github.alxiw.punkbrew.data.BeerItem
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.adapters.BeerListAdapter
import io.github.alxiw.punkbrew.listeners.LoadListListener
import io.github.alxiw.punkbrew.listeners.OnItemClickListener
import io.github.alxiw.punkbrew.listeners.PaginationScrollListener
import io.github.alxiw.punkbrew.providers.BeerProvider
import io.github.alxiw.punkbrew.service.ConnectivityReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(), LoadListListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var progressBar: ProgressBar

    private lateinit var beerListRecyclerView: RecyclerView
    private lateinit var adapter: BeerListAdapter

    private lateinit var beerProvider: BeerProvider

    private lateinit var searchView: SearchView

    private var currentQuery: String? = null

    private var mSnackBar: Snackbar? = null

    private val startPage = 1
    private val pageSize = 15

    private var isLoading = false
    private var isLastPage = false
    private var currentPage = startPage

    private var isFavourites = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        progressBar = findViewById(R.id.home_progress_bar)
        beerListRecyclerView = findViewById(R.id.home_beer_list_recycler_view)
        adapter = BeerListAdapter(this)
        beerListRecyclerView.adapter = adapter
        beerProvider = BeerProvider(this, this@HomeActivity)

        loadBeers(currentQuery, currentPage, pageSize)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        beerListRecyclerView.layoutManager = linearLayoutManager

        beerListRecyclerView.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override val isLoading: Boolean
                get() = this@HomeActivity.isLoading
            override val isLastPage: Boolean
                get() = this@HomeActivity.isLastPage

            override fun loadMoreItems() {
                this@HomeActivity.isLoading = true
                currentPage += 1

                loadBeers(currentQuery, currentPage, pageSize)
            }
        })

        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(beer: BeerItem) {
                sendItemToBeerActivity(beer)
            }

            override fun onFavoritesButtonClick(beer: BeerItem) {
                onFavoriteItemButtonClick(beer)
            }
        })
    }

    private fun onFavoriteItemButtonClick(beer: BeerItem) {
        if (beerProvider.contains(beer)) {
            beerProvider.removeBeer(beer)
        } else {
            beerProvider.saveBeer(beer)
        }
    }

    private fun sendItemToBeerActivity(beer: BeerItem) {
        val toDetails = Intent(this, BeerDetailActivity::class.java)
        toDetails.putExtra("BEER", beer)
        startActivity(toDetails)
    }

    fun loadBeers(currentQuery: String?, currentPage: Int, pageSize: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            beerProvider.getBeers(currentQuery, currentPage, pageSize)
        }
    }

    fun loadFavouriteBeers() {
        GlobalScope.launch(Dispatchers.IO) {
            beerProvider.getFavouriteBeers()
        }
    }

    override fun onResume() {
        super.onResume()

        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.home_menu_search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    searchView.clearFocus()
                    currentQuery = query
                    clearBeerList()
                    loadBeers(currentQuery, currentPage, pageSize)
                    searchView.onActionViewCollapsed()
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    currentQuery = newText
                    clearBeerList()
                    loadBeers(currentQuery, currentPage, pageSize)
                }
                return false
            }
        })

        val favouritesIconMenu = menu.findItem(R.id.home_menu_favorites)
        favouritesIconMenu.setOnMenuItemClickListener {
            loadFavouriteBeers()
            isFavourites = true
            false
        }
        return true
    }

    override fun updateRecyclerView(beers: List<BeerItem>?) {
        if (beers != null) {
            if (beers.isNotEmpty()) {
                progressBar.visibility = View.GONE

                adapter.addAll(beers)
                isLoading = false

            } else {
                isLastPage = true
            }
        } else {
            isLastPage = true
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            val messageToUser = "OFFLINE"

            mSnackBar = Snackbar.make(findViewById(R.id.home_beer_list_recycler_view), messageToUser, Snackbar.LENGTH_LONG)
            mSnackBar?.duration = Snackbar.LENGTH_INDEFINITE
            mSnackBar?.show()
        } else {
            mSnackBar?.dismiss()
        }
    }

    override fun onBackPressed() {
        if (currentQuery == null && !isFavourites) {
            super.onBackPressed()
        } else if (isFavourites) {
            isFavourites = false
            clearBeerList()
            loadBeers(currentQuery, currentPage, pageSize)
        } else {
            currentQuery = null
            clearBeerList()
            loadBeers(currentQuery, currentPage, pageSize)
        }
    }

    override fun clearWhenFavourites() {
        adapter.removeAll()

        currentPage = startPage
        isLastPage = true
    }

    fun clearBeerList() {
        adapter.removeAll()

        currentPage = startPage
        isLastPage = false
    }

    override fun onDestroy() {
        super.onDestroy()

        beerProvider.close()
    }
}
