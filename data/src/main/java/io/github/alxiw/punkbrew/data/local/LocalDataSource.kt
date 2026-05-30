package io.github.alxiw.punkbrew.data.local

import androidx.annotation.WorkerThread
import androidx.paging.DataSource
import io.github.alxiw.punkbrew.data.local.model.BeerEntity

interface LocalDataSource {

    @WorkerThread
    fun insertAll(beers: List<BeerEntity>)

    @WorkerThread
    fun update(beer: BeerEntity)

    suspend fun getBeer(id: Int): BeerEntity

    fun getBeers(query: String?): DataSource.Factory<Int, BeerEntity>

    fun getFavorites(): DataSource.Factory<Int, BeerEntity>

    suspend fun getBeersCount(query: String?): Int
}
