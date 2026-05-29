package io.github.alxiw.punkbrew.util

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import io.github.alxiw.punkbrew.data.loader.ImageLoader

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun getFormattedBeerName(name : String?) : String? {
    return if (TextUtils.isEmpty(name)) null else name?.trim()
}

fun ImageView.load(
    loader: ImageLoader,
    image: String,
    @DrawableRes placeholderRes: Int,
    callback: (() -> Unit)? = null
) {
    loader.loadImage(this, image, placeholderRes, callback)
}

fun Double.toCleanString(): String {
    return if (this % 1.0 == 0.0) toLong().toString() else toString()
}

