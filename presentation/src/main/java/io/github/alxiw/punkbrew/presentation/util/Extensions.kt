package io.github.alxiw.punkbrew.presentation.util

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import io.github.alxiw.punkbrew.domain.loader.ImageLoader

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun ImageView.load(
    loader: ImageLoader,
    image: String,
    @DrawableRes placeholderRes: Int,
    callback: (() -> Unit)? = null
) {
    loader.loadImage(this, image, placeholderRes, callback)
}
