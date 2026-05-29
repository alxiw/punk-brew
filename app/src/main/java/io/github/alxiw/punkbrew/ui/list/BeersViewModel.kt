package io.github.alxiw.punkbrew.ui.list

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.BeersRepository
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.ui.base.BaseViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


abstract class BeersViewModel(
    private val repository: BeersRepository
) : BaseViewModel()  {

    abstract val beers: StateFlow<PagedList<BeerEntity>?>

    fun updateBeer(beer: BeerEntity, updateFinished: () -> Unit) {
        viewModelScope.launch {
            repository.update(beer)
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
