package io.github.alxiw.punkbrew.data.local

import android.util.Log
import androidx.paging.DataSource
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.data.local.db.BeersDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BeersLocalSource(
    private val beersDao: BeersDao
) {

    suspend fun insertAll(beers: List<BeerEntity>) = withContext(Dispatchers.IO) {
        Log.d("HELLO", "Insert beers into cache")
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

    suspend fun update(beer: BeerEntity) = withContext(Dispatchers.IO) {
        Log.d("HELLO", "Update beer in cache")
        beersDao.update(beer.id, beer.favorite)
    }

    suspend fun getBeer(id: Int): BeerEntity {
        Log.d("HELLO", "Request beer from cache")
        return beersDao.beer(id)
    }

    fun getBeers(query: String?): DataSource.Factory<Int, BeerEntity> {
        Log.d("HELLO", "Request data source of all beers from cache")
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
        Log.d("HELLO", "Request data source of favorite beers from cache")
        return beersDao.favorites()
    }
}
