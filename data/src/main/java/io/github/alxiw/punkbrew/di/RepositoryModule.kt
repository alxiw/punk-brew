package io.github.alxiw.punkbrew.di

import android.content.Context
import com.squareup.picasso.Downloader
import com.squareup.picasso.Picasso
import io.github.alxiw.punkbrew.data.BeersRepository
import io.github.alxiw.punkbrew.data.loader.DetailsLoader
import io.github.alxiw.punkbrew.data.loader.ImageLoader
import io.github.alxiw.punkbrew.data.loader.PicassoImageLoader
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.dsl.module

private const val IMAGES_URL = "${BASE_URL}images/"

val repositoryModule = module {

    factory {
        val downloader = object : Downloader {
            override fun load(request: Request) = (get() as OkHttpClient).newCall(request).execute()
            override fun shutdown() { (get() as OkHttpClient).cache?.close() }
        }
        PicassoImageLoader(Picasso.Builder(get() as Context).downloader(downloader).build(), IMAGES_URL) as ImageLoader
    }
    factory { BeersRepository(get(), get()) }
    factory { DetailsLoader(get()) }
}
