package io.github.alxiw.punkbrew.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.data.source.BeersLocalSource
import io.github.alxiw.punkbrew.data.source.BeersRemoteSource
import timber.log.Timber

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
        Timber.d("Request new page of beers")
        remoteSource.searchBeers(
            query,
            lastRequestedPage,
            NETWORK_PAGE_SIZE,
            { beers ->
                localSource.insertAll(beers) {
                    Timber.d("Insert %d beers into cache", beers.size)
                    _networkErrors.postValue(null)
                    lastRequestedPage++
                    isRequestInProgress = false
                }
            },
            { error ->
                Timber.d("No beers inserted into cache")
                _networkErrors.postValue(error)
                isRequestInProgress = false
            }
        )
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 30
    }
}
