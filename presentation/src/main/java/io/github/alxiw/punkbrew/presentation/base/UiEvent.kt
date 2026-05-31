package io.github.alxiw.punkbrew.presentation.base

sealed interface UiEvent {
    data class FavoriteToggled(val id: Int, val favorite: Boolean) : UiEvent
}
