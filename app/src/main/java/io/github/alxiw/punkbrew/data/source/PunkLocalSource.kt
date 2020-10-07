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

    fun insertAll(beers: List<BeerEntity>, insertFinished: () -> Unit) {
        Timber.d("Insert beers into cache")
        ioExecutor.execute {
            for (beer in beers) {
                punkDao.insert(
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
            punkDao.update(beer.id, beer.favorite)
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
