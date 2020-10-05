package io.github.alxiw.punkbrew.ui.base

interface BaseView<VM : BaseViewModel> {

    val viewModel: VM

    val layoutId: Int

    fun onLoading()

    fun onContentReceived()

    fun onEmptyContent()
}
