package io.github.alxiw.punkbrew.data.mediator

import android.util.Log
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.local.BeersLocalSource
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.data.remote.BeersRemoteSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BeersBoundaryCallback(
    private val query: String?,
    private val remoteSource: BeersRemoteSource,
    private val localSource: BeersLocalSource
): PagedList.BoundaryCallback<BeerEntity>() {

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = 1

    // StateFlow of network errors.
    private val _networkErrors = MutableStateFlow<String?>(null)

    // StateFlow of network errors.
    val networkErrors: StateFlow<String?>
        get() = _networkErrors.asStateFlow()

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
                    _networkErrors.value = null
                    lastRequestedPage++
                    isRequestInProgress = false
                }
            },
            { error ->
                Log.d("HELLO", "No beers inserted into cache")
                _networkErrors.value = error
                isRequestInProgress = false
            }
        )
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 30
    }
}