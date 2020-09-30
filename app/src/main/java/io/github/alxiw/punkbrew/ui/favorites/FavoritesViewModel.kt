package io.github.alxiw.punkbrew.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.PunkRepository
import io.github.alxiw.punkbrew.data.db.BeerEntity
import timber.log.Timber

class FavoritesViewModel(private val repository: PunkRepository) : ViewModel() {

    val favorites : LiveData<PagedList<BeerEntity>> = repository.favorites()

    fun updateBeer(beer: BeerEntity, updateFinished: () -> Unit) {
        repository.update(beer) {
            Timber.d("Beer update from favorites fragment")
            updateFinished()
        }
    }
}
