package io.github.alxiw.punkbrew.models

import android.content.Context
import io.github.alxiw.punkbrew.dao.AppDatabase
import io.github.alxiw.punkbrew.data.BeerItem
import io.github.alxiw.punkbrew.entities.BeerEntity
import io.github.alxiw.punkbrew.entities.FavouriteEntity
import io.github.alxiw.punkbrew.listeners.LoadListListener
import java.util.ArrayList

class DatabaseModel(context: Context, private val loadListener: LoadListListener) {

    private val room = AppDatabase.getAppDatabase(context)

    fun getBeers(currentQuery: String?, page: Int, pageSize: Int) {
        if (currentQuery != null && currentQuery.isNotEmpty()) {
            val list = getQueriedBeers(currentQuery)
            if (list != null) {
                handleList(page, pageSize, list)
            }
        } else {
            val list = getBeers()
            if (list != null) {
                handleList(page, pageSize, list)
            }
        }
    }

    private fun handleList(page: Int, pageSize: Int, list: List<BeerEntity>) {
        val startPosition = 1 + (page - 1) * pageSize
        val endPosition = startPosition + (pageSize - 1)
        var currentPosition = 1
        val beers = ArrayList<BeerItem>()

        for (item in list) {
            if (currentPosition in startPosition..endPosition) {
                val beerItem = BeerItem(
                    item.id,
                    item.name,
                    item.tagline,
                    item.description,
                    item.abv,
                    item.ibu,
                    item.ebc,
                    item.srm,
                    item.date,
                    item.image
                )
                beerItem.isFavorite = item.isFavorite

                beers.add(beerItem)
            }
            currentPosition++
        }
        loadListener.updateRecyclerView(beers)
    }

    fun getFavouriteBeers() {
        val list = getFavourites()

        var entities = ArrayList<BeerEntity>()

        if (list != null && list.isNotEmpty()) {
            for (f in list) {
                val ent = room?.getBeerDao()?.getBeerById(f.id)
                if (ent != null) {
                    entities.add(ent)
                }
            }

            val beers = ArrayList<BeerItem>()

            for (item in entities) {
                val beerItem = BeerItem(
                    item.id,
                    item.name,
                    item.tagline,
                    item.description,
                    item.abv,
                    item.ibu,
                    item.ebc,
                    item.srm,
                    item.date,
                    item.image
                )
                beerItem.isFavorite = item.isFavorite

                beers.add(beerItem)
            }
            loadListener.updateRecyclerView(beers)
        }
    }

    fun getBeerById(beerEntity: BeerEntity): BeerEntity? {
        return room?.getBeerDao()?.getBeerById(beerEntity.id.toInt())
    }

    fun getQueriedBeers(query: String): List<BeerEntity>? {
        return room?.getBeerDao()?.getQueriedBeers(query)
    }

    fun getBeers(): List<BeerEntity>? {
        return room?.getBeerDao()?.getBeers()
    }

    fun insertBeer(beer: BeerEntity) {
        room?.getBeerDao()?.insert(beer)
    }

    fun cleanAllBeers() {
        room?.getBeerDao()?.cleanAllBeers()
    }

    fun closeDatabase() {
        AppDatabase.closeDatabase()
    }

    fun saveBeer(id: Int) {
        if (room?.getFavouritesDao()?.getFavouriteById(id) == null) {
            room?.getFavouritesDao()?.insertBeer(FavouriteEntity(id))
        }
    }

    fun removeBeer(id: Int) {
        room?.getFavouritesDao()?.remove(id)
    }

    fun containsBeer(id: Int): Int? {
        val favouriteEntity = room?.getFavouritesDao()?.getFavouriteById(id)
        return favouriteEntity?.id
    }

    fun getFavourites(): List<FavouriteEntity>? {
        return room?.getFavouritesDao()?.getFavourites()
    }

}