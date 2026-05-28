package io.github.alxiw.punkbrew.data.model

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity

/**
 * RepoSearchResult from a search, which contains LiveData<List<Repo>> holding query data,
 * and a LiveData<String> of network error state.
 */
data class SearchResult(
    val data: LiveData<PagedList<BeerEntity>>,
    val networkErrors: LiveData<String?>
)
