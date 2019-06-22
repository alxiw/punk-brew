package io.github.alxiw.punkbrew.ui.home

import io.github.alxiw.punkbrew.model.Beer

interface HomeView {
    fun updateList(beers: List<Beer>)
    fun addLoadingFooter()
}