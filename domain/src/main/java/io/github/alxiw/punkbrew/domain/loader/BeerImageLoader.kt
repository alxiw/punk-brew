package io.github.alxiw.punkbrew.domain.loader

import android.widget.ImageView
import androidx.annotation.DrawableRes
import io.github.alxiw.punkbrew.data.loader.ImageLoader as DataImageLoader

internal class BeerImageLoader(private val imageLoader: DataImageLoader) : ImageLoader {

    override fun loadImage(
        target: ImageView,
        imageName: String,
        @DrawableRes placeholderRes: Int,
        callback: (() -> Unit)?
    ) {
        imageLoader.loadImage(target, imageName, placeholderRes, callback)
    }
}
