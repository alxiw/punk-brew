package io.github.alxiw.punkbrew.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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

    private val beersResult : LiveData<SearchResult> = Transformations.map(queryLiveData) {
        repository.search(it)
    }

    override val beers: LiveData<PagedList<BeerEntity>> = Transformations.switchMap(beersResult) {
        it.data
    }
    val networkErrors: LiveData<String> = Transformations.switchMap(beersResult) {
        it.networkErrors
    }

    fun searchBeers(queryString: String?) {
        currentQuery = queryString
        queryLiveData.postValue(queryString)
    }
}
