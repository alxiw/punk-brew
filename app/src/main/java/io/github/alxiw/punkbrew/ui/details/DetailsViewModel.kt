package io.github.alxiw.punkbrew.ui.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import io.github.alxiw.punkbrew.data.BeersRepository
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.base.BaseViewModel
import io.reactivex.Single

class DetailsViewModel(
    private val repository: BeersRepository
) : BaseViewModel() {

    internal var currentBeer: BeerEntity? = null

    private val idLiveData = MutableLiveData<Int>()

    val beer : LiveData<Single<BeerEntity>> = idLiveData.map {
        repository.beer(it)
    }

    fun findBeer(beerId: Int) {
        idLiveData.postValue(beerId)
    }

    fun updateBeer(beer: BeerEntity, updateFinished: () -> Unit) {
        repository.update(beer) {
            Log.d("HELLO", "Beer #${beer.id} updated from ${javaClass.name}")
            updateFinished()
        }
    }
}
