package io.github.alxiw.punkbrew.util

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import io.github.alxiw.punkbrew.data.loader.ImageLoader
import io.github.alxiw.punkbrew.util.Extensions.format

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

fun formatNullableSimpleBeerValue(value: Double?): String {
    return value?.format() ?: DateFormatter.EMPTY_PLACEHOLDER
}

fun formatNullableDegreeBeerValue(value: Double?): String {
    return value?.let { "${it.format()}%" } ?: DateFormatter.EMPTY_PLACEHOLDER
}

