package io.github.alxiw.punkbrew.ui.favorites

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.PunkRepository
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.list.BeersViewModel

class FavoritesViewModel(
    repository: PunkRepository
) : BeersViewModel(repository) {

    override val beers: LiveData<PagedList<BeerEntity>> = repository.favorites()
}
