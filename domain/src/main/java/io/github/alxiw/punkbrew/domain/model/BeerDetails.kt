package io.github.alxiw.punkbrew.domain.model

data class BeerDetails(
    val id: Int,
    val number: String,
    val name: String,
    val tagline: String,
    val firstBrewed: String,
    val description: String,
    val image: String,
    val abv: String,
    val ibu: String,
    val targetFg: String,
    val targetOg: String,
    val ebc: String,
    val srm: String,
    val ph: String,
    val attenuationLevel: String,
    val volume: String,
    val boilVolume: String,
    val foodPairing: List<String>,
    val method: List<String>,
    val ingredients: List<String>,
    val brewersTips: String,
    val contributedBy: String,
    val favorite: Boolean
)
