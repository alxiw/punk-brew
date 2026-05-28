package io.github.alxiw.punkbrew.ui.base

sealed class UiState {
    object Loading : UiState()
    object Content : UiState()
    object Empty : UiState()
    data class Error(val message: String?) : UiState()
}
