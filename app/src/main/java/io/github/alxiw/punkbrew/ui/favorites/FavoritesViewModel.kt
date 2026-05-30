package io.github.alxiw.punkbrew.ui.favorites

import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.domain.Interactor
import io.github.alxiw.punkbrew.domain.model.Beer
import io.github.alxiw.punkbrew.ui.base.UiState
import io.github.alxiw.punkbrew.ui.list.BeersViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(
    interactor: Interactor
) : BeersViewModel(interactor) {

    override val beers: StateFlow<PagedList<Beer>?> = interactor.favorites()
        .onEach {
            _uiState.value = if (it.isEmpty()) UiState.Empty else UiState.Content
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
}
