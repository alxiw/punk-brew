package io.github.alxiw.punkbrew.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.data.source.PunkLocalSource
import io.github.alxiw.punkbrew.data.source.PunkRemoteSource
import timber.log.Timber

class PunkBoundaryCallback(
    private val query: String?,
    private val remoteSource: PunkRemoteSource,
    private val localSource: PunkLocalSource
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

    override fun onItemAtEndLoaded(itemAtEnd: BeerEntity) {
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String?) {
        Timber.d("BOUNDARY CALLBACK")
        if (isRequestInProgress) return

        isRequestInProgress = true
        remoteSource.searchBeers(query, lastRequestedPage, NETWORK_PAGE_SIZE, { beers ->
            localSource.insertAll(beers) {
                Timber.d("INSERT CACHE")
                lastRequestedPage++
                isRequestInProgress = false
            }
        }, { error ->
            _networkErrors.postValue(error)
            isRequestInProgress = false
        })
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }
}
