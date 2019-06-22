package io.github.alxiw.punkbrew.ui.home

import io.github.alxiw.punkbrew.data.BrewRepository
import io.github.alxiw.punkbrew.model.Beer
import kotlinx.coroutines.*
import javax.inject.Inject

class HomePresenter @Inject constructor(private val repository: BrewRepository) {

    private lateinit var view: HomeView

    fun setView(homeView: HomeView) {
        this.view = homeView
    }

    fun loadBeerPage(currentQuery: String?, currentPage: Int, pageSize: Int) {
        GlobalScope.async (Dispatchers.IO) {
            val list = repository.getBeers(currentQuery, currentPage, pageSize)
            if (list.isNotEmpty()) {
                view.updateList(list)
            }
        }
    }

    fun checkIsBeerExistsInFavourites(beer: Beer): Boolean {
        return runBlocking {
            repository.isInFavourites(beer)
        }
    }

    fun deleteFavourite(beer: Beer) {
        GlobalScope.async (Dispatchers.IO) {
            repository.deleteFavourite(beer)
        }
    }

    fun saveFavourite(beer: Beer) {
        GlobalScope.async (Dispatchers.IO) {
            repository.saveFavourite(beer)
        }
    }

}