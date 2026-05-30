package io.github.alxiw.punkbrew.domain

import androidx.lifecycle.asFlow
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.local.LocalDataSource
import io.github.alxiw.punkbrew.data.remote.RemoteDataSource
import io.github.alxiw.punkbrew.domain.mapper.BeerMapper
import io.github.alxiw.punkbrew.domain.mediator.BeersBoundaryCallback
import io.github.alxiw.punkbrew.domain.model.Beer
import io.github.alxiw.punkbrew.domain.model.BeerDetails
import io.github.alxiw.punkbrew.domain.model.SearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class BeersInteractor(
    private val remoteSource: RemoteDataSource,
    private val localSource: LocalDataSource,
    private val mapper: BeerMapper,
    private val scope: CoroutineScope
): Interactor {

    private val pageConfig = PagedList.Config.Builder()
        .setPageSize(DATABASE_PAGE_SIZE)
        .build()

    override fun search(query: String?): SearchResult {
        val dataSourceFactory = localSource.getBeers(query).map { mapper.mapToBeer(it) }
        val boundaryCallback = BeersBoundaryCallback(query, remoteSource, localSource, scope)
        val data = LivePagedListBuilder(dataSourceFactory, pageConfig)
            .setBoundaryCallback(boundaryCallback)
            .build()
            .asFlow()
        return SearchResult(data, boundaryCallback.networkErrors)
    }

    override fun favorites(): Flow<PagedList<Beer>> {
        val dataSourceFactory = localSource.getFavorites().map { mapper.mapToBeer(it) }
        return LivePagedListBuilder(dataSourceFactory, pageConfig)
            .build()
            .asFlow()
    }

    override suspend fun getBeer(id: Int): BeerDetails = withContext(Dispatchers.IO) {
        localSource.getBeer(id)
    }.let { mapper.mapToDetails(it) }

    override suspend fun toggleFavorite(id: Int) = withContext(Dispatchers.IO) {
        val entity = localSource.getBeer(id)
        val updated = entity.copy(favorite = !entity.favorite)
        localSource.update(updated)
    }

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }
}
