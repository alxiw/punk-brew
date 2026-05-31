package io.github.alxiw.punkbrew.presentation.catalog

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.domain.Interactor
import io.github.alxiw.punkbrew.domain.model.Beer
import io.github.alxiw.punkbrew.presentation.base.UiState
import io.github.alxiw.punkbrew.presentation.list.BeersViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class CatalogViewModel(
    private val interactor: Interactor
) : BeersViewModel(interactor) {

    var currentQuery: String? = null
        private set

    private var isLaunched = false

    private val queryFlow = MutableStateFlow<String?>(null)

    private val beersResult = queryFlow
        .debounce(300)
        .distinctUntilChanged()
        .onEach {
            Log.d("HELLO", "beersResult: query=$it")
            _uiState.value = UiState.Loading
        }
        .map { interactor.search(it) }
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    override val beers: StateFlow<PagedList<Beer>?> = beersResult
        .flatMapLatest {
            Log.d("HELLO", "flatMapLatest: new Listing received")
            it.data
        }
        .onEach { list ->
            Log.d("HELLO", "beers emitted: size=${list?.size}")
            _uiState.value = if (list.isEmpty()) UiState.Empty else UiState.Content
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val networkErrors: StateFlow<String?> = beersResult
        .flatMapLatest { it.networkErrors }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun searchBeers(queryString: String?, force: Boolean = false): Boolean {
        val query = queryString.normalize()
        Log.d("HELLO", "searchBeers: query=$queryString force=$force isLaunched=$isLaunched currentQuery=$currentQuery")
        if (!force && isLaunched && currentQuery == queryString) return false
        isLaunched = true
        currentQuery = query
        if (force) {
            Log.d("HELLO", "force invalidate: dataSource=${beers.value?.dataSource}, isInvalid=${beers.value?.dataSource?.isInvalid}")
            // инвалидируем текущий DataSource — Paging сам перезапросит данные
            // через тот же Listing, PagedList остаётся тем же объектом
            beers.value?.dataSource?.invalidate()
            return true
        }
        queryFlow.value = query
        Log.d("HELLO", "searchBeers: queryFlow updated to $queryString")
        return true
    }

    private fun String?.normalize() = if (isNullOrBlank()) null else trim()
}
