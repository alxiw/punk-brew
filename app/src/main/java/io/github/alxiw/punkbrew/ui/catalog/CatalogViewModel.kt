package io.github.alxiw.punkbrew.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.BeersRepository
import io.github.alxiw.punkbrew.data.SearchResult
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.list.BeersViewModel

class CatalogViewModel(
    repository: BeersRepository
) : BeersViewModel(repository) {

    internal var filterEnabled = false

    var currentQuery: String? = null

    private val queryLiveData = MutableLiveData<String?>()

    private val beersResult : LiveData<SearchResult> = queryLiveData.map {
        repository.search(it)
    }

    override val beers: LiveData<PagedList<BeerEntity>> = beersResult.switchMap {
        it.data
    }
    val networkErrors: LiveData<String> = beersResult.switchMap {
        it.networkErrors
    }

    fun searchBeers(queryString: String?) {
        currentQuery = queryString
        queryLiveData.postValue(queryString)
    }
}
