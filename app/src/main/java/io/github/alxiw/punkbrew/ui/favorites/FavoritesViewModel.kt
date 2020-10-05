package io.github.alxiw.punkbrew.ui.favorites

import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.PunkRepository
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.list.BeersViewModel

class FavoritesViewModel(
    repository: PunkRepository,
    imm: InputMethodManager
) : BeersViewModel(repository, imm) {

    override val beers: LiveData<PagedList<BeerEntity>> = repository.favorites()
}
