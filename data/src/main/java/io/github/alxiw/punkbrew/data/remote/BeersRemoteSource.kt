package io.github.alxiw.punkbrew.data.remote

import android.util.Log
import com.google.gson.Gson
import io.github.alxiw.punkbrew.data.remote.api.PunkService
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.data.mapper.BeerMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BeersRemoteSource(
    private val service: PunkService,
    private val gson: Gson,
    private val scope: CoroutineScope
) {

    fun searchBeers(
        query: String?,
        page: Int,
        perPage: Int,
        onSuccess: (beers: List<BeerEntity>) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val number = query?.toIntOrNull()
        Log.d("HELLO", "Request page of beers from server")
        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    if (number != null && number > 0) {
                        service.getBeersById(page, perPage, number)
                    } else {
                        service.getBeers(page, perPage, query)
                    }
                }
                Log.d("HELLO", "Received beers from server")

                onSuccess(BeerMapper.fromResponse(response, gson))
            } catch (e: Exception) {
                Log.d("HELLO", "Error occurred while requesting beers from server: ${e.message}")

                onError(e.message ?: "Unknown error")
            }
        }
    }
}
