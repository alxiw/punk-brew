package io.github.alxiw.punkbrew.db

import io.github.alxiw.punkbrew.model.Beer
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import kotlin.concurrent.withLock

class BrewCache @Inject constructor(private val dao: BrewDao) {

    private val lock = ReentrantLock()

    fun isBeerExists(beer: Beer): Boolean {
        lock.withLock {
            return dao.getBeerById(beer.id) != null
        }
    }

    fun saveBeer(beer: Beer) {
        if (!isBeerExists(beer)) {
            lock.withLock {
                dao.insertBeer(beer)
            }
        }
    }

    fun updateBeer(beer: Beer, isFavourite: Boolean): Boolean {
        lock.withLock {
            return dao.updateBeer(beer.id, isFavourite) == 1
        }
    }

    fun deleteBeer(beer: Beer) {
        if (isBeerExists(beer)) {
            lock.withLock {
                dao.delete(beer.id)
            }
        }
    }

    fun getBeer(id: Int) : Beer? {
        lock.withLock {
            return dao.getBeerById(id)
        }
    }

    fun getAllBeers(): List<Beer> {
        lock.withLock {
            return dao.getAllBeers()
        }
    }

    fun deleteAllBeers() {
        lock.withLock {
            dao.deleteAllBeers()
        }
    }

    fun getBeersByName(query: String): List<Beer> {
        lock.withLock {
            return dao.getBeersByName("%$query%")
        }
    }

    fun getBeersPage(currentPage: Int, pageSize: Int): List<Beer> {
        val scope = getAllBeers()
        val startPosition = 1 + (currentPage - 1) * pageSize
        val endPosition = startPosition + (pageSize - 1)
        var currentPosition = 1
        val beers = ArrayList<Beer>()

        scope.forEach {
            if (currentPosition in startPosition..endPosition) {
                beers.add(it)
            }
            currentPosition++
        }
        return beers
    }

    fun getQueriedBeersPage(query: String, currentPage: Int, pageSize: Int): List<Beer> {
        val scope = getBeersByName(query)
        val startPosition = 1 + (currentPage - 1) * pageSize
        val endPosition = startPosition + (pageSize - 1)
        var currentPosition = 1
        val beers = ArrayList<Beer>()

        scope.forEach {
            if (currentPosition in startPosition..endPosition) {
                beers.add(it)
            }
            currentPosition++
        }
        return beers
    }

    fun getFavourites(): List<Beer> {
        lock.withLock {
            return dao.getFavourites()
        }
    }

    fun getFavouritesPage(currentPage: Int, pageSize: Int): List<Beer> {
        val scope = getFavourites()
        val startPosition = 1 + (currentPage - 1) * pageSize
        val endPosition = startPosition + (pageSize - 1)
        var currentPosition = 1
        val beers = ArrayList<Beer>()

        scope.forEach {
            if (currentPosition in startPosition..endPosition) {
                beers.add(it)
            }
            currentPosition++
        }
        return beers
    }

    fun isBeerFavourite(beer: Beer): Boolean {
        if (isBeerExists(beer)) {
            val entity = getBeer(beer.id) ?: return false
            entity.let {
                return entity.isFavourite
            }
        } else {
            return false
        }
    }

}