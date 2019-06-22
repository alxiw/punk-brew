package io.github.alxiw.punkbrew.ui.base

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.model.Beer
import io.github.alxiw.punkbrew.network.ConnectivityReceiver
import io.github.alxiw.punkbrew.ui.details.DetailsActivity

abstract class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    protected var mSnackBar: Snackbar? = null

    protected lateinit var mTextEmpty: View
    protected lateinit var mCircleWait: ProgressBar

    protected lateinit var mRecyclerView: RecyclerView
    protected val mAdapter : BaseAdapter = BaseAdapter()

    private lateinit var receiver: ConnectivityReceiver

    protected val startPage = 1
    protected val pageSize = 25

    protected var currentPage = startPage
    protected var isLastPage = false
    protected var isLoading = false

    protected var currentQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        receiver = ConnectivityReceiver()

        mRecyclerView = findViewById(R.id.recycler_list_view)
        mTextEmpty = findViewById(R.id.home_search_fail)
        mCircleWait = findViewById(R.id.progress_list_bar)

        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        mRecyclerView.addItemDecoration(decoration)

        mAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(beer: Beer) {
                openBeerDetails(beer)
            }

            override fun onFavoritesButtonClick(beer: Beer) {
                onFavoriteItemButtonClick(beer)
            }
        })

        mRecyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(applicationContext)
            layoutManager = linearLayoutManager
            adapter = mAdapter
            itemAnimator = DefaultItemAnimator()
            addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
                override val isLoading: Boolean
                    get() = this@BaseActivity.isLoading
                override val isLastPage: Boolean
                    get() = this@BaseActivity.isLastPage

                override fun loadMoreItems() {
                    this@BaseActivity.isLoading = true
                    currentPage++
                    loadBeerPage()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onPause() {
        super.onPause()
        ConnectivityReceiver.connectivityReceiverListener = null
        unregisterReceiver(receiver)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.overridePendingTransition(R.anim.animate_fade_enter, R.anim.animate_fade_exit)
    }

    // VIEW METHODS

    fun updateRecyclerView(beers: List<Beer>?) {
        if (beers != null) {
            if (beers.isEmpty()) {
                mCircleWait.visibility = View.GONE
                if (currentPage == 1) {
                    mTextEmpty.visibility = View.VISIBLE
                } else {
                    isLastPage = true
                    mAdapter.removeLoadingFooter()
                }
            } else {
                mTextEmpty.visibility = View.GONE

                if (currentPage == startPage) {
                    mCircleWait.visibility = View.GONE
                } else {
                    mAdapter.removeLoadingFooter()
                    isLoading = false
                }

                mAdapter.addAll(beers)
                if (beers.size < pageSize) {
                    isLastPage = true
                } else {
                    mAdapter.addLoadingFooter()
                }
            }
        } else {
            isLastPage = true
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            val messageToUser = getString(R.string.no_internet_connection)
            mSnackBar = Snackbar.make(findViewById(R.id.recycler_list_view), messageToUser, Snackbar.LENGTH_LONG)
            mSnackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            mSnackBar?.show()
        } else {
            mSnackBar?.dismiss()
            isLastPage = false
            isLoading = false
        }
    }

    protected abstract fun loadBeerPage()
    protected abstract fun onFavoriteItemButtonClick(beer: Beer)

    fun openBeerDetails(beer: Beer) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("beer", beer)
        startActivity(intent)
        this.overridePendingTransition(R.anim.animate_slide_left_enter, R.anim.animate_slide_left_exit)
    }

}