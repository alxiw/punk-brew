package io.github.alxiw.punkbrew.data.loader

import android.widget.ImageView
import androidx.annotation.DrawableRes

interface ImageLoader {

    fun loadImage(
        target: ImageView,
        imageName: String,
        @DrawableRes placeholderRes: Int,
        callback: (() -> Unit)? = null
    )

    fun getImageUrl(imageName: String): String
}
