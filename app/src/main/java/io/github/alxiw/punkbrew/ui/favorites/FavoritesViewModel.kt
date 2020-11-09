package io.github.alxiw.punkbrew.ui.favorites

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import io.github.alxiw.punkbrew.data.BeersRepository
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.list.BeersViewModel

class FavoritesViewModel(
    repository: BeersRepository
) : BeersViewModel(repository) {

    override val beers: LiveData<PagedList<BeerEntity>> = repository.favorites()
}
