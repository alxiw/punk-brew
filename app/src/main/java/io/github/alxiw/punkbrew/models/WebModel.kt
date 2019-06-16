package io.github.alxiw.punkbrew.models

import android.util.Log
import io.github.alxiw.punkbrew.data.BeerItem
import io.github.alxiw.punkbrew.entities.BeerEntity
import io.github.alxiw.punkbrew.listeners.LoadListListener
import io.github.alxiw.punkbrew.service.RetrofitWrapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WebModel(val loadListener: LoadListListener, private val databaseModel: DatabaseModel) {

    fun getBeers(currentQuery: String?, currentPage: Int, pageSize: Int) {
        val call: Call<List<BeerItem>> = if (currentQuery != null && currentQuery.isNotEmpty())
            RetrofitWrapper().getBeerService().list(currentPage, pageSize, currentQuery)
        else
            RetrofitWrapper().getBeerService().list(currentPage, pageSize)

        call.enqueue(object : Callback<List<BeerItem>> {
            override fun onResponse(call: Call<List<BeerItem>>, response: Response<List<BeerItem>>) {
                val beers = response.body()
                for (beer in beers!!) {
                    if (databaseModel.containsBeer(beer.id.toInt()) != null) {
                        beer.isFavorite = true
                    }
                }

                if (beers != null && beers.isNotEmpty()) {
                    GlobalScope.launch {
                        addAllBeersToDatabase(beers)
                    }
                }
                loadListener.updateRecyclerView(beers)
            }

            override fun onFailure(call: Call<List<BeerItem>>, t: Throwable) {
                Log.e("onFailure", "Requisition failure. msg: " + t.message)
            }
        })
    }

    fun getFavouriteBeers() {

        val favourites = databaseModel.getFavourites()

        if (favourites != null && favourites.isNotEmpty()) {

            var ids : MutableList<Int> = ArrayList()

            var line = ""

            for (f in favourites) {
                ids.add(f.id)
                line += "${f.id}|"
            }

            val call = RetrofitWrapper().getBeerService().list(line)

            call.enqueue(object : Callback<List<BeerItem>> {
                override fun onResponse(call: Call<List<BeerItem>>, response: Response<List<BeerItem>>) {
                    val beers = response.body()
                    for (beer in beers!!) {
                        if (databaseModel.containsBeer(beer.id.toInt()) != null) {
                            beer.isFavorite = true
                        }
                    }

                    if (beers != null && beers.isNotEmpty()) {
                        GlobalScope.launch {
                            addAllBeersToDatabase(beers)
                        }
                    }

                    loadListener.clearWhenFavourites()
                    loadListener.updateRecyclerView(beers)
                }

                override fun onFailure(call: Call<List<BeerItem>>, t: Throwable) {
                    Log.e("onFailure", "Requisition failure. msg: " + t.message)
                }
            })

        }
    }

    suspend fun addAllBeersToDatabase(beers: List<BeerItem>) {
        delay(500)
        for (beer in beers) {
            val beerEntity = BeerEntity(
                beer.id,
                beer.name,
                beer.tagline,
                beer.description,
                beer.abv,
                beer.ibu,
                beer.ebc,
                beer.srm,
                beer.date,
                beer.image
            )
            beerEntity.isFavorite = beer.isFavorite

            val check = databaseModel.getBeerById(beerEntity)

            if (check == null) {
                databaseModel.insertBeer(beerEntity)
            }
        }
    }
}