package io.github.alxiw.punkbrew.data.loader

import android.widget.ImageView

interface ImageLoader {

    fun loadImage(target: ImageView, imageName: String, callback: (() -> Unit)? = null)

    fun getImageUrl(imageName: String): String
}
