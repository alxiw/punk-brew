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
        Timber.d("Get beer from cache")
        return punkDao.beer(id)
    }

    fun getBeers(name: String?): DataSource.Factory<Int, BeerEntity> {
        Timber.d("Get beers from cache")
        if (name.isNullOrEmpty()) {
            return punkDao.beers()
        }
        val number = name.toIntOrNull()
        if (number != null && number > 0) {
            return punkDao.beersById(number)
        }
        val query = "%${name.replace(' ', '%')}%"
        return punkDao.beersByName(query)
    }

    fun getFavorites(): DataSource.Factory<Int, BeerEntity> {
        Timber.d("Get favorites from cache")
        return punkDao.favorites()
    }
}
