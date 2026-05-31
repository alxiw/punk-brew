package io.github.alxiw.punkbrew.presentation.navigation

interface Navigator {
    fun openCatalog()
    fun openFavorites()
    fun openDetails(id: Int)
}