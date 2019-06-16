package io.github.alxiw.punkbrew.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitWrapper {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.punkapi.com/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getBeerService(): BeerService {
        return retrofit.create(BeerService::class.java)
    }
}