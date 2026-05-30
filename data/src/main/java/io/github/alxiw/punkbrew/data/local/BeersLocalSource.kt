package io.github.alxiw.punkbrew.data.local

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.paging.DataSource
import io.github.alxiw.punkbrew.data.local.db.BeersDao
import io.github.alxiw.punkbrew.data.local.model.BeerEntity

internal class BeersLocalSource(
    private val beersDao: BeersDao
): LocalDataSource {

    @WorkerThread
    override fun insertAll(beers: List<BeerEntity>) {
        Log.d("HELLO", "[LOCAL SOURCE] Insert beers into cache")
        for (beer in beers) {
            beersDao.insert(
                beer.id,
                beer.name,
                beer.tagline,
                beer.firstBrewed,
                beer.description,
                beer.image,
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
    }

    @WorkerThread
    override fun update(beer: BeerEntity) {
        Log.d("HELLO", "[LOCAL SOURCE] Update beer in cache")
        beersDao.update(beer.id, beer.favorite)
    }

    override suspend fun getBeer(id: Int): BeerEntity {
        Log.d("HELLO", "[LOCAL SOURCE] Request beer from cache")
        return beersDao.beer(id)
    }

    override fun getBeers(query: String?): DataSource.Factory<Int, BeerEntity> {
        Log.d("HELLO", "[LOCAL SOURCE] Request data source of all beers from cache, query: <${query ?: "NULL"}>")
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

    override fun getFavorites(): DataSource.Factory<Int, BeerEntity> {
        Log.d("HELLO", "[LOCAL SOURCE] Request data source of favorite beers from cache")
        return beersDao.favorites()
    }

    override suspend fun getBeersCount(query: String?): Int {
        Log.d("HELLO", "[LOCAL SOURCE] Request beers count from cache for query: <${query ?: "NULL"}>")
        return if (query.isNullOrEmpty()) {
            beersDao.getBeersCount()
        } else {
            val name = "%${query.replace(' ', '%')}%"
            beersDao.getBeersCountByName(name)
        }
    }
}
