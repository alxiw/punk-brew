package io.github.alxiw.punkbrew.presentation.list

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.domain.Interactor
import io.github.alxiw.punkbrew.domain.model.Beer
import io.github.alxiw.punkbrew.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


abstract class BeersViewModel(
    private val interactor: Interactor
) : BaseViewModel()  {

    abstract val beers: StateFlow<PagedList<Beer>?>

    fun toggleFavorite(beer: Beer, updateFinished: () -> Unit) {
        viewModelScope.launch {
            interactor.toggleFavorite(beer.id)
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
