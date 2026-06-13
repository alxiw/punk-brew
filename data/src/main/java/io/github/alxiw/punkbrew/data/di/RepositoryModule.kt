package io.github.alxiw.punkbrew.data.di

import com.squareup.picasso.Downloader
import com.squareup.picasso.Picasso
import io.github.alxiw.punkbrew.data.loader.ImageLoader
import io.github.alxiw.punkbrew.data.loader.PicassoImageLoader
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val IMAGES_URL = "${BASE_URL}images/"

val repositoryModule = module {

    factory {
        val downloader = object : Downloader {
            override fun load(request: Request) = get<OkHttpClient>().newCall(request).execute()
            override fun shutdown() { get<OkHttpClient>().cache?.close() }
        }
        PicassoImageLoader(
            picasso = Picasso.Builder(androidContext()).downloader(downloader).build(),
            imageUrl = IMAGES_URL
        ) as ImageLoader
    }
}
