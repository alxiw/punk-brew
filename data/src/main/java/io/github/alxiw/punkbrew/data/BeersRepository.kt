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
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

class BeersRepository(
    private val remoteSource: BeersRemoteSource,
    private val localSource: BeersLocalSource
) {

    private val pageConfig = PagedList.Config.Builder()
        .setPageSize(DATABASE_PAGE_SIZE)
        .build()

    fun search(query: String?): SearchResult {
        Log.d("HELLO", "Search beers by query: $query")
        val dataSourceFactory = localSource.getBeers(query)
        val boundaryCallback = BeersBoundaryCallback(query, remoteSource, localSource)
        val networkErrors = boundaryCallback.networkErrors
        val data = LivePagedListBuilder(dataSourceFactory, pageConfig)
            .setBoundaryCallback(boundaryCallback)
            .build()
            .asFlow()
        return SearchResult(data, networkErrors)
    }

    fun favorites(): Flow<PagedList<BeerEntity>> {
        val dataSourceFactory = localSource.getFavorites()
        return LivePagedListBuilder(dataSourceFactory, pageConfig)
            .build()
            .asFlow()
    }

    fun beer(id: Int): Single<BeerEntity> {
        return localSource.getBeer(id)
    }

    fun update(beer: BeerEntity, insertFinished: () -> Unit) {
        localSource.update(beer) {
            insertFinished()
        }
    }

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }
}
