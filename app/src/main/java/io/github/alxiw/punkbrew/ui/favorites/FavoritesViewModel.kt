package io.github.alxiw.punkbrew.ui.favorites

import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.BeersRepository
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.ui.base.UiState
import io.github.alxiw.punkbrew.ui.list.BeersViewModel
import kotlinx.coroutines.flow.*

class FavoritesViewModel(
    repository: BeersRepository
) : BeersViewModel(repository) {

    override val beers: StateFlow<PagedList<BeerEntity>?> = repository.favorites()
        .onEach {
            _uiState.value = if (it.isEmpty()) UiState.Empty else UiState.Content
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
}
