package io.github.alxiw.punkbrew.ui.list

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.BeersRepository
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.ui.base.BaseViewModel
import kotlinx.coroutines.flow.StateFlow


abstract class BeersViewModel(
    private val repository: BeersRepository
) : BaseViewModel()  {

    abstract val beers: StateFlow<PagedList<BeerEntity>?>

    fun updateBeer(beer: BeerEntity, updateFinished: () -> Unit) {
        repository.update(beer) {
            Log.d("HELLO", "Beer #${beer.id} has updated from ${javaClass.name}")
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
