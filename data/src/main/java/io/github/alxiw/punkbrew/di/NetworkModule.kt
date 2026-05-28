package io.github.alxiw.punkbrew.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.alxiw.punkbrew.data.remote.api.PunkService
import io.github.alxiw.punkbrew.data.remote.BeersRemoteSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal const val BASE_URL = "https://punkapi-alxiw.amvera.io/v3/"

val networkModule = module {

    factory { GsonBuilder().setLenient().create() as Gson }
    factory {
        with(OkHttpClient.Builder()) {
            readTimeout(1, TimeUnit.SECONDS)
            connectTimeout(1, TimeUnit.SECONDS)
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
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
    factory { BeersRemoteSource(get(), get()) as BeersRemoteSource }
}
