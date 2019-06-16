package io.github.alxiw.punkbrew.service

import io.github.alxiw.punkbrew.data.BeerItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BeerService {
    @GET("beers")
    fun list(): Call<List<BeerItem>>

    @GET("beers")
    fun list(@Query("page") page: Int, @Query("per_page") per_page: Int): Call<List<BeerItem>>

    @GET("beers")
    fun list(@Query("page") currentPage: Int, @Query("per_page") perPage: Int,
             @Query("beer_name") currentQuery: String): Call<List<BeerItem>>

    @GET("beers")
    fun list(@Query("ids") ids: String): Call<List<BeerItem>>
}