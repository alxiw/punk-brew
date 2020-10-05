package io.github.alxiw.punkbrew.ui.list

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.PunkRepository
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.base.BaseViewModel
import timber.log.Timber

abstract class BeersViewModel(
    private val repository: PunkRepository,
    private val imm: InputMethodManager
) : BaseViewModel()  {

    abstract val beers: LiveData<PagedList<BeerEntity>>

    fun updateBeer(beer: BeerEntity, updateFinished: () -> Unit) {
        repository.update(beer) {
            Timber.d("Beer #%d has updated from %s", beer.id, javaClass.name)
            updateFinished()
        }
    }

    fun hideKeyboard(input: View?) {
        input?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}
