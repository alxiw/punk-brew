package io.github.alxiw.punkbrew.data.remote

import io.github.alxiw.punkbrew.data.local.model.BeerEntity

interface RemoteDataSource {

    suspend fun searchBeers(
        query: String?,
        page: Int,
        perPage: Int
    ): List<BeerEntity>
}
