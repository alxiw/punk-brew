package io.github.alxiw.punkbrew.data.remote.api

import io.github.alxiw.punkbrew.data.remote.api.model.BeerResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PunkService {

    @GET("beers")
    fun getBeers(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int,
        @Query("beer_name") beerName: String?
    ): Single<List<BeerResponse>>

    @GET("beers")
    fun getBeersById(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int,
        @Query("ids") beerId: Int
    ): Single<List<BeerResponse>>
}
