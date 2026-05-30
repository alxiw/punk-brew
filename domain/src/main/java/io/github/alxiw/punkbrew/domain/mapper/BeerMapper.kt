package io.github.alxiw.punkbrew.domain.mapper

import io.github.alxiw.punkbrew.data.local.model.BeerEntity
import io.github.alxiw.punkbrew.domain.model.Beer
import io.github.alxiw.punkbrew.domain.model.BeerDetails
import io.github.alxiw.punkbrew.domain.utils.DateFormatter

internal class BeerMapper(private val detailsFormatter: DetailsFormatter) {

    fun mapToBeer(entity: BeerEntity): Beer {
        return Beer(
            id = entity.id,
            number = detailsFormatter.formatNumber(entity.id),
            name = entity.name,
            tagline = entity.tagline,
            image = entity.image,
            abv = detailsFormatter.formatNullableDegreeBeerValue(entity.abv),
            date = DateFormatter.formatDate(entity.firstBrewed, true),
            description = entity.description,
            favorite = entity.favorite
        )
    }

    fun mapToDetails(entity: BeerEntity): BeerDetails {
        return BeerDetails(
            id = entity.id,
            number = detailsFormatter.formatNumber(entity.id),
            name = entity.name,
            tagline = entity.tagline,
            firstBrewed = DateFormatter.formatDate(entity.firstBrewed, false),
            description = entity.description,
            image = entity.image,
            abv = detailsFormatter.formatNullableDegreeBeerValue(entity.abv),
            ibu = detailsFormatter.formatNullableSimpleBeerValue(entity.ibu),
            targetFg = detailsFormatter.formatNullableSimpleBeerValue(entity.targetFg),
            targetOg = detailsFormatter.formatNullableSimpleBeerValue(entity.targetOg),
            ebc = detailsFormatter.formatNullableSimpleBeerValue(entity.ebc),
            srm = detailsFormatter.formatNullableSimpleBeerValue(entity.srm),
            ph = detailsFormatter.formatNullableSimpleBeerValue(entity.ph),
            attenuationLevel = detailsFormatter.formatNullableDegreeBeerValue(entity.attenuationLevel),
            volume = detailsFormatter.getVolume(entity.volumeJson),
            boilVolume = detailsFormatter.getVolume(entity.boilVolumeJson),
            foodPairing = detailsFormatter.getFoodPairing(entity.foodPairingJson),
            method = detailsFormatter.getMethod(entity.methodJson),
            ingredients = detailsFormatter.getIngredients(entity.ingredientsJson),
            brewersTips = entity.brewersTips,
            contributedBy = detailsFormatter.formatContributedBy(entity.contributedBy),
            favorite = entity.favorite
        )
    }
}
