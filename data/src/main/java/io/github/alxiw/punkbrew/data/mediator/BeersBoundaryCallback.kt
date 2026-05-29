package io.github.alxiw.punkbrew.data.mediator

import android.util.Log
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.local.BeersLocalSource
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.data.remote.BeersRemoteSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

class BeersBoundaryCallback(
    private val query: String?,
    private val remoteSource: BeersRemoteSource,
    private val localSource: BeersLocalSource,
    private val scope: CoroutineScope
) : PagedList.BoundaryCallback<BeerEntity>() {

    private val _networkErrors = MutableStateFlow<String?>(null)
    val networkErrors: StateFlow<String?>
        get() = _networkErrors.asStateFlow()

    private var isRequestInProgress = false
    private var isEndReached = false

    override fun onZeroItemsLoaded() {
        Log.d("HELLO", "[BOUNDARY CALLBACK] On zero items loaded")
        requestAndSaveData(query)
    }

    override fun onItemAtEndLoaded(itemAtEnd: BeerEntity) {
        Log.d("HELLO", "[BOUNDARY CALLBACK] On item at end loaded")
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String?) {
        Log.d("HELLO", "[BOUNDARY CALLBACK] Request and save data: <${query ?: "NULL"}>")
        if (isRequestInProgress || isEndReached) return
        isRequestInProgress = true
        Log.d("HELLO", "[BOUNDARY CALLBACK] Request new page of beers for query: <${query ?: "NULL"}>")

        flow {
            val count = localSource.getBeersCount(query)
            val page = (count / NETWORK_PAGE_SIZE) + 1
            val beers = remoteSource.searchBeers(query, page, NETWORK_PAGE_SIZE)
            emit(beers)
        }
            .onEach { beers ->
                if (beers.isEmpty() || beers.size < NETWORK_PAGE_SIZE) {
                    isEndReached = true
                }
                localSource.insertAll(beers)
                Log.d("HELLO", "[BOUNDARY CALLBACK] Insert ${beers.size} beers into cache")
                _networkErrors.value = null
            }
            .catch { e ->
                Log.d("HELLO", "[BOUNDARY CALLBACK] Error during request: ${e.message}")
                _networkErrors.value = e.message
            }
            .onCompletion {
                isRequestInProgress = false
            }
            .launchIn(scope)
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 30
    }
}
