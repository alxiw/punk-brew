package io.github.alxiw.punkbrew.ui.beers

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.PunkRepository
import io.github.alxiw.punkbrew.data.SearchResult
import io.github.alxiw.punkbrew.data.db.BeerEntity
import timber.log.Timber

class BeersViewModel(
    private val repository: PunkRepository,
    private val imm: InputMethodManager
) : ViewModel() {

    var currentQuery: String? = null

    private val queryLiveData = MutableLiveData<String?>()

    private val beersResult : LiveData<SearchResult> = Transformations.map(queryLiveData) {
        repository.search(it)
    }

    val beers: LiveData<PagedList<BeerEntity>> = Transformations.switchMap(beersResult) {
        it.data
    }
    val networkErrors: LiveData<String> = Transformations.switchMap(beersResult) {
        it.networkErrors
    }

    fun searchBeers(queryString: String?) {
        currentQuery = queryString
        queryLiveData.postValue(queryString)
    }

    fun updateBeer(beer: BeerEntity, updateFinished: () -> Unit) {
        repository.update(beer) {
            Timber.d("Beer update from beers fragment")
            updateFinished()
        }
    }

    fun hideKeyboard(input: View?) {
        input?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}
