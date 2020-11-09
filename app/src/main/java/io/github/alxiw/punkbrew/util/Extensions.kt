package io.github.alxiw.punkbrew.util

import android.text.TextUtils
import android.view.View

const val EMPTY_PLACEHOLDER = "∅"

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun getFormattedBeerName(name : String?) : String?{
    return if (TextUtils.isEmpty(name)) null else name
}
