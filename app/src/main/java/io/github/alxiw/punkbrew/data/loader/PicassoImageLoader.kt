package io.github.alxiw.punkbrew.data.loader

import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.github.alxiw.punkbrew.R

internal class PicassoImageLoader(
    private val picasso: Picasso,
    private val imageUrl: String
): ImageLoader {

    override fun loadImage(target: ImageView, imageName: String, callback: (() -> Unit)?) {
        picasso.setIndicatorsEnabled(true)
        picasso
            .load("$imageUrl${imageName}")
            .placeholder(R.drawable.bottle)
            .error(R.drawable.bottle)
            .fit().centerInside()
            .into(target, callback?.let {
                object : Callback {
                    override fun onSuccess() {
                        it.invoke()
                    }
                    override fun onError(e: Exception?) {
                        Log.d("HELLO", "Error occurred while loading image ${imageName}, ${e?.message ?: "Unknown error"}")
                    }
                }
            })
    }

    override fun getImageUrl(imageName: String): String {
        return "$imageUrl${imageName}"
    }
}
