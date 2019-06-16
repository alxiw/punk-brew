package io.github.alxiw.punkbrew.listeners

import io.github.alxiw.punkbrew.data.BeerItem

interface LoadListListener {
    fun updateRecyclerView(beers: List<BeerItem>?)
    fun clearWhenFavourites()
}