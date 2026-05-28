package io.github.alxiw.punkbrew.data.mapper

import com.google.gson.Gson
import io.github.alxiw.punkbrew.data.remote.api.model.BeerResponse
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity

object BeerMapper {

    fun fromResponse(response: List<BeerResponse>, gson: Gson): List<BeerEntity> {
        return response.map {
            val beer = BeerEntity(
                it.id,
                it.name,
                it.tagline,
                it.firstBrewed,
                it.description,
                it.image,
                it.abv,
                it.ibu,
                it.targetFg,
                it.targetOg,
                it.ebc,
                it.srm,
                it.ph,
                it.attenuationLevel,
                gson.toJson(it.volume),
                gson.toJson(it.boilVolume),
                gson.toJson(it.method),
                gson.toJson(it.ingredients),
                gson.toJson(it.foodPairing),
                it.brewersTips,
                it.contributedBy
            )
            beer
        }
    }
}
