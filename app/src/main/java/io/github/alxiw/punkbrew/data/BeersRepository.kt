package io.github.alxiw.punkbrew.data

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.data.source.BeersLocalSource
import io.github.alxiw.punkbrew.data.source.BeersRemoteSource
import io.reactivex.Single
import timber.log.Timber

class BeersRepository(
    private val remoteSource: BeersRemoteSource,
    private val localSource: BeersLocalSource
) {

    private val pageConfig = PagedList.Config.Builder()
        .setPageSize(DATABASE_PAGE_SIZE)
        .build()

    fun search(query: String?): SearchResult {
        Timber.d("Search beers by query: $query")
        val dataSourceFactory = localSource.getBeers(query)
        val boundaryCallback = BeersBoundaryCallback(query, remoteSource, localSource)
        val networkErrors = boundaryCallback.networkErrors
        val data = LivePagedListBuilder(dataSourceFactory, pageConfig)
            .setBoundaryCallback(boundaryCallback)
            .build()
        return SearchResult(data, networkErrors)
    }

    fun favorites(): LiveData<PagedList<BeerEntity>> {
        val dataSourceFactory = localSource.getFavorites()
        return LivePagedListBuilder(dataSourceFactory, pageConfig).build()
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
