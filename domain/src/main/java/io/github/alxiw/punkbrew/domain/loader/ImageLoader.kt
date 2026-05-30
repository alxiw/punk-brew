package io.github.alxiw.punkbrew.domain.loader

import android.widget.ImageView
import androidx.annotation.DrawableRes

interface ImageLoader {

    fun loadImage(
        target: ImageView,
        imageName: String,
        @DrawableRes placeholderRes: Int,
        callback: (() -> Unit)? = null
    )
}
