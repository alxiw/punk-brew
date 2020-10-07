package io.github.alxiw.punkbrew.data

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.data.source.PunkLocalSource
import io.github.alxiw.punkbrew.data.source.PunkRemoteSource
import io.reactivex.Single
import timber.log.Timber

class PunkRepository(
    private val remoteSource: PunkRemoteSource,
    private val localSource: PunkLocalSource
) {

    private val pageConfig = PagedList.Config.Builder()
        .setPageSize(DATABASE_PAGE_SIZE)
        .setInitialLoadSizeHint(DATABASE_PAGE_INITIAL_SIZE)
        .build()

    fun search(query: String?): SearchResult {
        Timber.d("Search beers by query: $query")
        val dataSourceFactory = localSource.getBeers(query)
        val boundaryCallback = PunkBoundaryCallback(query, remoteSource, localSource)
        val networkErrors = boundaryCallback.networkErrors
        val data = LivePagedListBuilder(dataSourceFactory, pageConfig)
            .setBoundaryCallback(boundaryCallback)
            .build()
        return SearchResult(data, networkErrors)
    }

    fun favorites(): LiveData<PagedList<BeerEntity>> {
        val dataSourceFactory = localSource.getFavorites()
        dataSourceFactory.create().invalidate()
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
        private const val DATABASE_PAGE_INITIAL_SIZE = 60
        private const val DATABASE_PAGE_SIZE = 20
    }
}
