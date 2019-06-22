package io.github.alxiw.punkbrew.ui.details

import io.github.alxiw.punkbrew.data.BrewRepository
import io.github.alxiw.punkbrew.model.Beer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DetailsPresenter @Inject constructor(private val repository: BrewRepository) {

    private lateinit var view: DetailsView

    fun setView(detailsView: DetailsView) {
        this.view = detailsView
    }

    suspend fun checkIsBeerExistsInFavourites(beer: Beer): Boolean {
        return runBlocking {
            repository.isInFavourites(beer)
        }
    }

    suspend fun deleteFavourite(beer: Beer) {
        GlobalScope.async (Dispatchers.IO) {
            repository.deleteFavourite(beer)
        }
    }

    suspend fun saveFavourite(beer: Beer) {
        GlobalScope.async (Dispatchers.IO) {
            repository.saveFavourite(beer)
        }
    }
}