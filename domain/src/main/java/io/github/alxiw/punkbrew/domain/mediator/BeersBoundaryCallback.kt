package io.github.alxiw.punkbrew.domain.mediator

import android.util.Log
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.local.LocalDataSource
import io.github.alxiw.punkbrew.data.remote.RemoteDataSource
import io.github.alxiw.punkbrew.domain.model.Beer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class BeersBoundaryCallback(
    private val query: String?,
    private val remoteSource: RemoteDataSource,
    private val localSource: LocalDataSource,
    private val scope: CoroutineScope
) : PagedList.BoundaryCallback<Beer>() {

    private val _networkErrors = MutableStateFlow<String?>(null)
    val networkErrors: StateFlow<String?>
        get() = _networkErrors.asStateFlow()

    private var isRequestInProgress = false
    private var isEndReached = false

    override fun onZeroItemsLoaded() {
        Log.d("HELLO", "[BC] On zero items loaded")
        requestAndSaveData(query)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Beer) {
        Log.d("HELLO", "[BC] On item at end loaded")
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String?) {
        Log.d("HELLO", "[BC] Request and save data: <${query ?: "NULL"}>")
        if (isRequestInProgress || isEndReached) return

        scope.launch(Dispatchers.IO) {
            try {
                isRequestInProgress = true
                Log.d("HELLO", "[BC] Request new page of beers for query: <${query ?: "NULL"}>")

                val count = localSource.getBeersCount(query)
                val page = (count / NETWORK_PAGE_SIZE) + 1
                val beers = remoteSource.searchBeers(query, page, NETWORK_PAGE_SIZE)

                if (beers.isEmpty() || beers.size < NETWORK_PAGE_SIZE) {
                    isEndReached = true
                }
                localSource.insertAll(beers)
                Log.d("HELLO", "[BC] Insert ${beers.size} beers into cache")
                _networkErrors.value = null
            } catch (e: Exception) {
                Log.d("HELLO", "[BC] Error during request: ${e.message}")
                _networkErrors.value = e.message
            } finally {
                isRequestInProgress = false
            }
        }
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 30
    }
}
