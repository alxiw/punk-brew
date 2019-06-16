package io.github.alxiw.punkbrew.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "beers")
data class BeerEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val tagline: String,
    val description: String?,
    val abv: Double,
    val ibu: Double,
    val ebc: Double,
    val srm: Double,
    val date: String?,
    val image: String?
): Serializable {
    var isFavorite : Boolean = false
}