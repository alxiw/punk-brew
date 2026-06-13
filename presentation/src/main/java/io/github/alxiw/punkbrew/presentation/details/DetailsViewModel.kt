package io.github.alxiw.punkbrew.presentation.details

import android.util.Log
import androidx.lifecycle.viewModelScope
import io.github.alxiw.punkbrew.domain.Interactor
import io.github.alxiw.punkbrew.domain.model.BeerDetails
import io.github.alxiw.punkbrew.presentation.base.BaseViewModel
import io.github.alxiw.punkbrew.presentation.base.UiEvent
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

    fun findBeer() {
        val id = beerId ?: run {
            _uiState.value = UiState.Error("Beer ID is null")
            return
        }

        _uiState.value = UiState.Loading
        viewModelScope.launch {
            runCatching { interactor.getBeer(id) }
                .onSuccess { beer ->
                    _beer.value = beer
                    _uiState.value = UiState.Content
                }
                .onFailure { e ->
                    Log.d("HELLO", "[DVM] Error finding beer #$id: ${e.message}")
                    _uiState.value = UiState.Empty
                }
        }
    }

    fun toggleFavorite() {
        val id = beerId ?: return
        viewModelScope.launch {
            runCatching {
                interactor.toggleFavorite(id)
                interactor.getBeer(id)
            }.onSuccess { updatedBeer ->
                _beer.value = updatedBeer
                Log.d("HELLO", "[DVM] Beer #$id favorite toggled")
                _events.emit(UiEvent.FavoriteToggled(updatedBeer.id, updatedBeer.favorite))
            }.onFailure { e ->
                Log.e("HELLO", "[DVM] Error toggling favorite #$id: ${e.message}")
            }
        }
    }
}
