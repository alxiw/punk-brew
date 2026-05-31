package io.github.alxiw.punkbrew.presentation.details

import android.util.Log
import androidx.lifecycle.viewModelScope
import io.github.alxiw.punkbrew.domain.Interactor
import io.github.alxiw.punkbrew.domain.model.BeerDetails
import io.github.alxiw.punkbrew.presentation.base.BaseViewModel
import io.github.alxiw.punkbrew.presentation.base.UiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val interactor: Interactor
) : BaseViewModel() {

    sealed interface Event {
        data object FavoriteToggled : Event
    }

    private val _beer = MutableStateFlow<BeerDetails?>(null)
    val beer: StateFlow<BeerDetails?> = _beer.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

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
                    Log.d("HELLO", "Error finding beer: ${e.message}")
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
                Log.d("HELLO", "Beer #$id favorite toggled")
                _events.emit(Event.FavoriteToggled)
            }.onFailure { e ->
                Log.e("HELLO", "Error toggling favorite: ${e.message}")
            }
        }
    }
}
