package io.github.alxiw.punkbrew.ui.base

import io.github.alxiw.punkbrew.model.Beer

interface OnItemClickListener {
    fun onItemClick(beer: Beer)
    fun onFavoritesButtonClick(beer: Beer)
}
