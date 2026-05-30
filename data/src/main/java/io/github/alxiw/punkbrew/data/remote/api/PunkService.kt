package io.github.alxiw.punkbrew.data.remote.api

import io.github.alxiw.punkbrew.data.remote.model.BeerResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface PunkService {

    @GET("beers")
    suspend fun getBeers(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int,
        @Query("beer_name") beerName: String?
    ): List<BeerResponse>

    @GET("beers")
    suspend fun getBeersById(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int,
        @Query("ids") beerId: Int
    ): List<BeerResponse>
}
