package io.github.alxiw.simplesearchview.listeners

import io.github.alxiw.simplesearchview.SimpleSearchView

internal abstract class SimpleOnQueryTextListener : SimpleSearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String): Boolean = false
    override fun onQueryTextChange(newText: String): Boolean = false
    override fun onQueryTextCleared(): Boolean = false
}
