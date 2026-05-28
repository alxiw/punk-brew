package io.github.alxiw.punkbrew.ui.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.alxiw.punkbrew.data.BeersRepository
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.base.BaseViewModel
import io.github.alxiw.punkbrew.ui.base.UiState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DetailsViewModel(
    private val repository: BeersRepository
) : BaseViewModel() {

    private val disposables = CompositeDisposable()

    private val _beer = MutableLiveData<BeerEntity>()
    val beer: LiveData<BeerEntity> = _beer

    var beerId: Int? = null

    var currentBeer: BeerEntity? = null

    fun findBeer() {
        beerId?.let {
            _uiState.value = UiState.Loading
            disposables.add(
                repository.beer(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { beer ->
                            currentBeer = beer
                            _beer.value = beer
                            _uiState.value = UiState.Content
                        },
                        { e ->
                            Log.d("HELLO", "Error finding beer: ${e.message}")
                            _uiState.value = UiState.Empty
                        }
                    )
            )
        } ?: run {
            _uiState.value = UiState.Error("Beer ID is null")
        }
    }

    fun updateBeer(beer: BeerEntity, updateFinished: () -> Unit) {
        repository.update(beer) {
            Log.d("HELLO", "Beer #${beer.id} updated from ${javaClass.name}")
            updateFinished()
        }
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
