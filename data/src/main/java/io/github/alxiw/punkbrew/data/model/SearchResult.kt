package io.github.alxiw.punkbrew.data.model

import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import kotlinx.coroutines.flow.Flow

/**
 * RepoSearchResult from a search, which contains Flow<PagedList<BeerEntity>> holding query data,
 * and a Flow<String?> of network error state.
 */
data class SearchResult(
    val data: Flow<PagedList<BeerEntity>>,
    val networkErrors: Flow<String?>
)
