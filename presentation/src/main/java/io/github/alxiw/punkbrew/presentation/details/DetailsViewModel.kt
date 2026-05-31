package io.github.alxiw.punkbrew.presentation.details

import android.util.Log
import androidx.lifecycle.viewModelScope
import io.github.alxiw.punkbrew.domain.Interactor
import io.github.alxiw.punkbrew.domain.model.BeerDetails
import io.github.alxiw.punkbrew.presentation.base.BaseViewModel
import io.github.alxiw.punkbrew.presentation.base.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val interactor: Interactor
) : BaseViewModel() {

    private val _beer = MutableStateFlow<BeerDetails?>(null)
    val beer: StateFlow<BeerDetails?> = _beer.asStateFlow()

    var beerId: Int? = null

    var isLoaded = false

    fun findBeer() {
        beerId?.let { id ->
            _uiState.value = UiState.Loading
            viewModelScope.launch {
                try {
                    val beer = interactor.getBeer(id)
                    isLoaded = true
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

    fun toggleFavorite(updateFinished: () -> Unit) {
        val beerId = beerId ?: return
        viewModelScope.launch {
            try {
                interactor.toggleFavorite(beerId)
                val updatedBeer = interactor.getBeer(beerId)
                isLoaded = true
                _beer.value = updatedBeer
                Log.d("HELLO", "Beer #${beerId} favorite toggled from ${javaClass.name}")
                updateFinished()
            } catch (e: Exception) {
                Log.e("HELLO", "Error toggling favorite: ${e.message}")
            }
        }
    }
}
