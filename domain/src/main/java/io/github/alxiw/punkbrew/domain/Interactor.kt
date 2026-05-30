package io.github.alxiw.punkbrew.domain

import androidx.paging.PagedList
import io.github.alxiw.punkbrew.domain.model.Beer
import io.github.alxiw.punkbrew.domain.model.BeerDetails
import io.github.alxiw.punkbrew.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface Interactor {

    fun search(query: String?): SearchResult

    fun favorites(): Flow<PagedList<Beer>>

    suspend fun getBeer(id: Int): BeerDetails

    suspend fun toggleFavorite(id: Int)
}
