package io.github.alxiw.punkbrew.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PunkService {

    @GET("beers/{number}/")
    fun getBeer(@Path("number") number: Int) : Call<List<BeerResponse>>

    @GET("beers")
    fun getBeers(@Query("page") page: Int,
                 @Query("per_page") per_page: Int): Call<List<BeerResponse>>

    @GET("beers")
    fun getBeersByName(@Query("page") currentPage: Int,
                       @Query("per_page") perPage: Int,
                       @Query("beer_name") currentQuery: String): Call<List<BeerResponse>>

}
