package io.github.alxiw.punkbrew.presentation.list

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.domain.Interactor
import io.github.alxiw.punkbrew.domain.model.Beer
import io.github.alxiw.punkbrew.presentation.base.BaseViewModel
import io.github.alxiw.punkbrew.presentation.base.UiEvent
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BeersViewModel(
    private val interactor: Interactor
) : BaseViewModel()  {

    abstract val beers: StateFlow<PagedList<Beer>?>

    fun toggleFavorite(beer: Beer) {
        viewModelScope.launch {
            runCatching {
                val id = beer.id
                interactor.toggleFavorite(id)
                interactor.getBeer(id)
            }.onSuccess { updatedBeer ->
                Log.d("HELLO", "[BVM] Beer #${updatedBeer.id} favorite toggled")
                _events.emit(UiEvent.FavoriteToggled(updatedBeer.id, updatedBeer.favorite))
            }.onFailure { e ->
                Log.e("HELLO", "[BVM] Error toggling favorite: ${e.message}")
            }
        }
    }
}
