package io.github.alxiw.punkbrew.api

import com.google.gson.annotations.SerializedName

data class BeerResponse(
    val id: Int,
    val name: String,
    val tagline: String,
    @SerializedName("first_brewed")
    val firstBrewed: String?,
    val description: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    val abv: Double,
    val ibu: Double,
    @SerializedName("target_fg")
    val targetFg: Double,
    @SerializedName("target_og")
    val targetOg: Double,
    val ebc: Double,
    val srm: Double,
    val ph: Double,
    @SerializedName("attenuation_level")
    val attenuationLevel: Double,
    @SerializedName("volume")
    val volume: Value,
    @SerializedName("boil_volume")
    val boilVolume: Value,
    @SerializedName("food_pairing")
    val foodPairing: List<String>,
    @SerializedName("brewers_tips")
    val brewersTips: String,
    @SerializedName("contributed_by")
    val contributedBy: String
) {
    data class Value (
            val value: Double,
            val unit: String
    )
}
