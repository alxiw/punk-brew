package io.github.alxiw.punkbrew.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BeerItem(
    val id: Long,
    val name: String,
    val tagline: String,
    val description: String?,
    val abv: Double,
    val ibu: Double,
    val ebc: Double,
    val srm: Double,
    @SerializedName("first_brewed")
    val date: String?,
    @SerializedName("image_url")
    val image: String?
): Serializable {
    var isFavorite : Boolean = false
}