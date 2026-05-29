package io.github.alxiw.punkbrew.ui.catalog

import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.BeersRepository
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.data.model.SearchResult
import io.github.alxiw.punkbrew.ui.base.UiState
import io.github.alxiw.punkbrew.ui.list.BeersViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModel(
    repository: BeersRepository
) : BeersViewModel(repository) {

    var currentQuery: String? = null
        private set

    private var isLaunched = false

    private val queryFlow = MutableSharedFlow<String?>(replay = 1)

    private val beersResult: SharedFlow<SearchResult> = queryFlow.map {
        repository.search(it)
    }.shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    override val beers: StateFlow<PagedList<BeerEntity>?> = beersResult.flatMapLatest {
        it.data
    }.onEach {
        _uiState.value = if (it.isEmpty()) UiState.Empty else UiState.Content
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val networkErrors: StateFlow<String?> = beersResult.flatMapLatest {
        it.networkErrors
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun searchBeers(queryString: String?, force: Boolean = false): Boolean {
        if (!force && isLaunched && currentQuery == queryString) return false
        isLaunched = true
        currentQuery = queryString
        queryFlow.tryEmit(queryString)
        return true
    }
}
