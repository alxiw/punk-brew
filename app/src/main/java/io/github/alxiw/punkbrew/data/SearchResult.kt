package io.github.alxiw.punkbrew.data

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.db.BeerEntity

/**
 * RepoSearchResult from a search, which contains LiveData<List<Repo>> holding query data,
 * and a LiveData<String> of network error state.
 */
data class SearchResult(
    val data: LiveData<PagedList<BeerEntity>>,
    val networkErrors: LiveData<String>
)
