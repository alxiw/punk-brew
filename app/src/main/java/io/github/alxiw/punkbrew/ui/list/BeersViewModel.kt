package io.github.alxiw.punkbrew.ui.list

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.BeersRepository
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.base.BaseViewModel
import timber.log.Timber

abstract class BeersViewModel(
    private val repository: BeersRepository
) : BaseViewModel()  {

    abstract val beers: LiveData<PagedList<BeerEntity>>

    fun updateBeer(beer: BeerEntity, updateFinished: () -> Unit) {
        repository.update(beer) {
            Timber.d("Beer #%d has updated from %s", beer.id, javaClass.name)
            updateFinished()
        }
    }

    fun hideKeyboard(context: Context, input: View?) {
        val imm = context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        input?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}
