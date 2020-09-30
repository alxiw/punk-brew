package io.github.alxiw.punkbrew.data.map

import io.github.alxiw.punkbrew.data.api.BeerResponse
import io.github.alxiw.punkbrew.data.db.BeerEntity

object BeerMapper {

    fun fromResponse(response: List<BeerResponse>): List<BeerEntity> {
        return response.map {
            val beer = BeerEntity(
                it.id,
                it.name,
                it.tagline,
                it.firstBrewed,
                it.description,
                it.imageUrl,
                it.abv,
                it.ibu
            )
            beer
        }
    }
}
