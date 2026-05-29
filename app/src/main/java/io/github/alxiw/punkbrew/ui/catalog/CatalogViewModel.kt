package io.github.alxiw.punkbrew.ui.catalog

import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.BeersRepository
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.ui.base.UiState
import io.github.alxiw.punkbrew.ui.list.BeersViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class CatalogViewModel(
    private val repository: BeersRepository
) : BeersViewModel(repository) {

    var currentQuery: String? = null
        private set

    private var isLaunched = false

    private val queryFlow = MutableStateFlow<String?>(null)

    private val beersResult = queryFlow
        .debounce(300)
        .map { repository.search(it) }
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    override val beers: StateFlow<PagedList<BeerEntity>?> = beersResult
        .flatMapLatest { it.data }
        .onEach { list ->
            _uiState.value = if (list.isEmpty()) UiState.Empty else UiState.Content
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val networkErrors: StateFlow<String?> = beersResult
        .flatMapLatest { it.networkErrors }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun searchBeers(queryString: String?, force: Boolean = false): Boolean {
        if (!force && isLaunched && currentQuery == queryString) return false
        isLaunched = true
        currentQuery = queryString
        _uiState.value = UiState.Loading
        queryFlow.value = queryString
        return true
    }
}
