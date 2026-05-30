package io.github.alxiw.punkbrew.domain.model

import androidx.paging.PagedList
import kotlinx.coroutines.flow.Flow

data class SearchResult(
    val data: Flow<PagedList<Beer>>,
    val networkErrors: Flow<String?>
)
