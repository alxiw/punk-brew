package io.github.alxiw.punkbrew.data.remote

import android.util.Log
import com.google.gson.Gson
import io.github.alxiw.punkbrew.data.local.model.BeerEntity
import io.github.alxiw.punkbrew.data.mapper.BeerMapper
import io.github.alxiw.punkbrew.data.remote.api.PunkService

internal class BeersRemoteSource(
    private val service: PunkService,
    private val gson: Gson
): RemoteDataSource {

    override suspend fun searchBeers(
        query: String?,
        page: Int,
        perPage: Int
    ): List<BeerEntity> {
        Log.d("HELLO", "[BRS] Request page of beers from server for query: <${query ?: "NULL"}>, page: $page, per page: $perPage")
        val number = query?.toIntOrNull()

        val response = if (number != null && number > 0) {
            service.getBeersById(page, perPage, number)
        } else {
            service.getBeers(page, perPage, query)
        }

        Log.d("HELLO", "[BRS] Received ${response.size} beers from server")
        return BeerMapper.fromResponse(response, gson)
    }
}
