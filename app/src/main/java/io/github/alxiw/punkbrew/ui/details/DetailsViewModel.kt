package io.github.alxiw.punkbrew.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.github.alxiw.punkbrew.data.PunkRepository
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.reactivex.Single
import timber.log.Timber

class DetailsViewModel(private val repository: PunkRepository) : ViewModel() {

    private var currentBeerId: Int = -1

    private val idLiveData = MutableLiveData<Int>()

    val beer : LiveData<Single<BeerEntity>> = Transformations.map(idLiveData) {
        repository.beer(it)
    }

    fun findBeer(beerId: Int) {
        currentBeerId = beerId
        idLiveData.postValue(beerId)
    }

    fun updateBeer(beer: BeerEntity, updateFinished: () -> Unit) {
        repository.update(beer) {
            Timber.d("Beer updated from details fragment")
            updateFinished()
        }
    }
}
