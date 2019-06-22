package io.github.alxiw.punkbrew.ui.favourites

import io.github.alxiw.punkbrew.model.Beer

interface FavouritesView {
    fun updateList(beers: List<Beer>)
    fun addLoadingFooter()
}