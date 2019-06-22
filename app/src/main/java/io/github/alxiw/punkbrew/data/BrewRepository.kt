package io.github.alxiw.punkbrew.data

import android.content.Context
import android.net.ConnectivityManager
import io.github.alxiw.punkbrew.api.BeerResponse
import io.github.alxiw.punkbrew.api.PunkService
import io.github.alxiw.punkbrew.db.BrewCache
import io.github.alxiw.punkbrew.model.Beer
import kotlinx.coroutines.*
import retrofit2.Call
import javax.inject.Inject

class BrewRepository @Inject constructor(
        private val context: Context,
        private val cache: BrewCache,
        private val service: PunkService
) {
    private val isOnline: Boolean
        get() {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }

    suspend fun getFavourites(currentPage: Int, pageSize: Int) : List<Beer> {
        return CoroutineScope(Dispatchers.IO).async {
            cache.getFavouritesPage(currentPage, pageSize)
        }.await()
    }

    suspend fun getBeers(currentQuery: String?, currentPage: Int, pageSize: Int) : List<Beer> {
        return if (isOnline) {
                getFromService(currentQuery, currentPage, pageSize)
            } else {
                getFromDatabase(currentQuery, currentPage, pageSize)
            }
    }

    suspend fun isInFavourites(beer: Beer) : Boolean {
        return CoroutineScope(Dispatchers.IO).async {
            cache.isBeerExists(beer) && cache.isBeerFavourite(beer)
        }.await()
    }

    suspend fun deleteFavourite(beer: Beer) {
        CoroutineScope(Dispatchers.IO).async {
            cache.updateBeer(beer, false)
        }.await()
    }

    suspend fun saveFavourite(beer: Beer) {
        CoroutineScope(Dispatchers.IO).async {
            cache.updateBeer(beer, true)
        }.await()
    }

    private suspend fun getFromService(currentQuery: String?, currentPage: Int, pageSize: Int) : List<Beer> {
        val responseList = CoroutineScope(Dispatchers.IO).async {
            // just for test async
            delay(1000)
            val call: Call<List<BeerResponse>> = if (currentQuery != null && currentQuery.isNotEmpty()) {
                service.getBeersByName(currentPage, pageSize, currentQuery)
            } else {
                service.getBeers(currentPage, pageSize)
            }
            val response = call.execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                ArrayList<BeerResponse>()
            }
        }.await()

        val list: ArrayList<Beer> = ArrayList()

        CoroutineScope(Dispatchers.IO).launch {
            responseList?.forEach {
                val entity = Beer(
                        it.id,
                        it.name,
                        it.tagline,
                        it.firstBrewed,
                        it.description,
                        it.imageUrl,
                        it.abv,
                        it.ibu,
                        it.targetFg,
                        it.targetOg,
                        it.ebc,
                        it.srm,
                        it.ph,
                        it.attenuationLevel,
                        it.brewersTips,
                        it.contributedBy
                )
                list.add(entity)
            }
            list.forEach {
                if (cache.isBeerExists(it)) {
                    if (cache.isBeerFavourite(it)) {
                        it.isFavourite = true
                    }
                    if (it == cache.getBeer(it.id)) {
                        cache.deleteBeer(it)
                        cache.saveBeer(it)
                    }
                } else {
                    cache.saveBeer(it)
                }
            }
        }.join()

        return list
    }

    private suspend fun getFromDatabase(currentQuery: String?, currentPage: Int, pageSize: Int) : List<Beer> {
        return CoroutineScope(Dispatchers.IO).async {
            if (currentQuery != null && currentQuery.isNotEmpty()) {
                cache.getQueriedBeersPage(currentQuery, currentPage, pageSize)
            } else {
                cache.getBeersPage(currentPage, pageSize)
            }
        }.await()
    }

}