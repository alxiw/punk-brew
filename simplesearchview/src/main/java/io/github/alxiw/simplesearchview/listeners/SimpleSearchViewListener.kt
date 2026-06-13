package io.github.alxiw.simplesearchview.listeners

import io.github.alxiw.simplesearchview.SimpleSearchView

internal abstract class SimpleSearchViewListener : SimpleSearchView.SearchViewListener {
    override fun onSearchViewShown() = Unit
    override fun onSearchViewClosed() = Unit
    override fun onSearchViewShownAnimation() = Unit
    override fun onSearchViewClosedAnimation() = Unit
}
