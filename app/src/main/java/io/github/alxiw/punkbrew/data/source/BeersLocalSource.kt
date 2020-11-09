package io.github.alxiw.punkbrew.data.source

import androidx.paging.DataSource
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.data.db.BeersDao
import io.reactivex.Single
import timber.log.Timber
import java.util.concurrent.Executor

class BeersLocalSource (
    private val beersDao: BeersDao,
    private val ioExecutor: Executor
) {

    fun insertAll(beers: List<BeerEntity>, insertFinished: () -> Unit) {
        Timber.d("Insert beers into cache")
        ioExecutor.execute {
            for (beer in beers) {
                beersDao.insert(
                    beer.id,
                    beer.name,
                    beer.tagline,
                    beer.firstBrewed,
                    beer.description,
                    beer.imageUrl,
                    beer.abv,
                    beer.ibu,
                    beer.targetFg,
                    beer.targetOg,
                    beer.ebc,
                    beer.srm,
                    beer.ph,
                    beer.attenuationLevel,
                    beer.volumeJson,
                    beer.boilVolumeJson,
                    beer.methodJson,
                    beer.ingredientsJson,
                    beer.foodPairingJson,
                    beer.brewersTips,
                    beer.contributedBy
                )
            }
            insertFinished()
        }
    }

    fun update(beer: BeerEntity, insertFinished: () -> Unit) {
        Timber.d("Update beer in cache")
        ioExecutor.execute {
            beersDao.update(beer.id, beer.favorite)
            insertFinished()
        }
    }

    fun getBeer(id: Int): Single<BeerEntity> {
        Timber.d("Request beer from cache")
        return beersDao.beer(id)
    }

    fun getBeers(query: String?): DataSource.Factory<Int, BeerEntity> {
        Timber.d("Request data source of all beers from cache")
        if (query.isNullOrEmpty()) {
            return beersDao.beers()
        }
        val number = query.toIntOrNull()
        if (number != null && number > 0) {
            return beersDao.beersById(number)
        }
        val name = "%${query.replace(' ', '%')}%"
        return beersDao.beersByName(name)
    }

    fun getFavorites(): DataSource.Factory<Int, BeerEntity> {
        Timber.d("Request data source of favorite beers from cache")
        return beersDao.favorites()
    }
}
