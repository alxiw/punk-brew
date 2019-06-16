package io.github.alxiw.punkbrew.listeners

import io.github.alxiw.punkbrew.data.BeerItem

interface OnItemClickListener {
    fun onItemClick(beer: BeerItem)
    fun onFavoritesButtonClick(beer: BeerItem)
}