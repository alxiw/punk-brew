package io.github.alxiw.punkbrew.data

import android.util.Log
import androidx.lifecycle.asFlow
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.local.BeersLocalSource
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.data.mediator.BeersBoundaryCallback
import io.github.alxiw.punkbrew.data.model.SearchResult
import io.github.alxiw.punkbrew.data.remote.BeersRemoteSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class BeersRepository(
    private val remoteSource: BeersRemoteSource,
    private val localSource: BeersLocalSource,
    private val scope: CoroutineScope
) {

    private val pageConfig = PagedList.Config.Builder()
        .setPageSize(DATABASE_PAGE_SIZE)
        .build()

    fun search(query: String?): SearchResult {
        Log.d("HELLO", "[REPOSITORY] Search beers by query: <${query ?: "NULL"}>")
        val dataSourceFactory = localSource.getBeers(query)
        val boundaryCallback = BeersBoundaryCallback(query, remoteSource, localSource, scope)
        val networkErrors = boundaryCallback.networkErrors
        val data = LivePagedListBuilder(dataSourceFactory, pageConfig)
            .setBoundaryCallback(boundaryCallback)
            .build()
            .asFlow()
        return SearchResult(data, networkErrors)
    }

    fun favorites(): Flow<PagedList<BeerEntity>> {
        Log.d("HELLO", "[REPOSITORY] Get favourite beers")
        val dataSourceFactory = localSource.getFavorites()
        return LivePagedListBuilder(dataSourceFactory, pageConfig)
            .build()
            .asFlow()
    }

    suspend fun beer(id: Int): BeerEntity {
        Log.d("HELLO", "[REPOSITORY] Get beer #$id")
        return localSource.getBeer(id)
    }

    suspend fun update(beer: BeerEntity) {
        Log.d("HELLO", "[REPOSITORY] Update beer #${beer.id}")
        localSource.update(beer)
    }

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }
}
