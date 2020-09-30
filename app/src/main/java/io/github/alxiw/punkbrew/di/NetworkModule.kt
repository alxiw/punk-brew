package io.github.alxiw.punkbrew.di

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.alxiw.punkbrew.BuildConfig
import io.github.alxiw.punkbrew.data.api.PunkService
import io.github.alxiw.punkbrew.data.source.PunkRemoteSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.punkapi.com/v2/"

val networkModule = module {

    factory { GsonBuilder().setLenient().create() as Gson }
    factory {
        with(OkHttpClient.Builder()) {
            readTimeout(1, TimeUnit.SECONDS)
            connectTimeout(1, TimeUnit.SECONDS)
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            if (BuildConfig.DEBUG) {
                addInterceptor(StethoInterceptor())
            }
            build()
        } as OkHttpClient
    }
    factory {
        with(Retrofit.Builder()) {
            baseUrl(BASE_URL)
            client(get())
            addConverterFactory(GsonConverterFactory.create(get()))
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            build()
        }.create(PunkService::class.java) as PunkService
    }
    factory { PunkRemoteSource(get()) as PunkRemoteSource }
}
