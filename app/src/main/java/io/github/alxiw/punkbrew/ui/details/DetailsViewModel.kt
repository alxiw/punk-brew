package io.github.alxiw.punkbrew.ui.details

import android.util.Log
import androidx.lifecycle.viewModelScope
import io.github.alxiw.punkbrew.data.BeersRepository
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.ui.base.BaseViewModel
import io.github.alxiw.punkbrew.ui.base.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsViewModel(
    private val repository: BeersRepository
) : BaseViewModel() {

    private val _beer = MutableStateFlow<BeerEntity?>(null)
    val beer: StateFlow<BeerEntity?> = _beer.asStateFlow()

    var beerId: Int? = null

    var currentBeer: BeerEntity? = null

    fun findBeer() {
        beerId?.let { id ->
            _uiState.value = UiState.Loading
            viewModelScope.launch {
                try {
                    val beer = withContext(Dispatchers.IO) {
                        repository.beer(id)
                    }
                    currentBeer = beer
                    _beer.value = beer
                    _uiState.value = UiState.Content
                } catch (e: Exception) {
                    Log.d("HELLO", "Error finding beer: ${e.message}")
                    _uiState.value = UiState.Empty
                }
            }
        } ?: run {
            _uiState.value = UiState.Error("Beer ID is null")
        }
    }

    fun updateBeer(beer: BeerEntity, updateFinished: () -> Unit) {
        viewModelScope.launch {
            repository.update(beer)
            Log.d("HELLO", "Beer #${beer.id} updated from ${javaClass.name}")
            updateFinished()
        }
    }
}
