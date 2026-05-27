package io.github.alxiw.punkbrew.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.data.source.BeersLocalSource
import io.github.alxiw.punkbrew.data.source.BeersRemoteSource

class BeersBoundaryCallback(
    private val query: String?,
    private val remoteSource: BeersRemoteSource,
    private val localSource: BeersLocalSource
): PagedList.BoundaryCallback<BeerEntity>() {

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = 1

    // LiveData of network errors.
    private val _networkErrors = MutableLiveData<String>()

    // LiveData of network errors.
    val networkErrors: LiveData<String>
        get() = _networkErrors

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    override fun onZeroItemsLoaded() {
        requestAndSaveData(query)
    }

    override fun onItemAtFrontLoaded(itemAtFront: BeerEntity) {
        requestAndSaveData(query)
    }

    override fun onItemAtEndLoaded(itemAtEnd: BeerEntity) {
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String?) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        Log.d("HELLO", "Request new page of beers")
        remoteSource.searchBeers(
            query,
            lastRequestedPage,
            NETWORK_PAGE_SIZE,
            { beers ->
                localSource.insertAll(beers) {
                    Log.d("HELLO", "Insert ${beers.size} beers into cache")
                    _networkErrors.postValue(null)
                    lastRequestedPage++
                    isRequestInProgress = false
                }
            },
            { error ->
                Log.d("HELLO", "No beers inserted into cache")
                _networkErrors.postValue(error)
                isRequestInProgress = false
            }
        )
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 30
    }
}
