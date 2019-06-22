package io.github.alxiw.punkbrew.ui.favourites

import io.github.alxiw.punkbrew.data.BrewRepository
import io.github.alxiw.punkbrew.model.Beer
import kotlinx.coroutines.*
import javax.inject.Inject

class FavouritesPresenter @Inject constructor(private val repository: BrewRepository) {

    private lateinit var view: FavouritesView

    fun setView(favouritesView: FavouritesView) {
        this.view = favouritesView
    }

    fun loadFavouritesPage(currentPage: Int, pageSize: Int) {
        GlobalScope.async {
            val list = repository.getFavourites(currentPage, pageSize)
            view.updateList(list)
        }
    }

    fun deleteBeer(beer: Beer) {
        GlobalScope.async {
            repository.deleteFavourite(beer)
        }
    }

}