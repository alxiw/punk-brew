package io.github.alxiw.punkbrew.data.source

import androidx.paging.DataSource
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.data.db.PunkDao
import io.reactivex.Single
import timber.log.Timber
import java.util.concurrent.Executor

class PunkLocalSource (
    private val punkDao: PunkDao,
    private val ioExecutor: Executor
) {

    fun insert(beer: BeerEntity, insertFinished: () -> Unit) {
        Timber.d("Insert beer into cache")
        ioExecutor.execute {
            punkDao.insert(beer)
            insertFinished()
        }
    }

    fun insertAll(beers: List<BeerEntity>, insertFinished: () -> Unit) {
        Timber.d("Insert beers into cache")
        ioExecutor.execute {
            punkDao.insertAll(beers)
            insertFinished()
        }
    }

    fun getBeer(id: Int): Single<BeerEntity> {
        Timber.d("Request beer from cache")
        return punkDao.beer(id)
    }

    fun getBeers(query: String?): DataSource.Factory<Int, BeerEntity> {
        Timber.d("Request data source of all beers from cache")
        if (query.isNullOrEmpty()) {
            return punkDao.beers()
        }
        val number = query.toIntOrNull()
        if (number != null && number > 0) {
            return punkDao.beersById(number)
        }
        val name = "%${query.replace(' ', '%')}%"
        return punkDao.beersByName(name)
    }

    fun getFavorites(): DataSource.Factory<Int, BeerEntity> {
        Timber.d("Request data source of favorite beers from cache")
        return punkDao.favorites()
    }
}
