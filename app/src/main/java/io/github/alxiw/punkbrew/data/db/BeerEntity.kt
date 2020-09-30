package io.github.alxiw.punkbrew.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beers")
data class BeerEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "tagline")
    val tagline: String,
    @ColumnInfo(name = "first_brewed")
    val firstBrewed: String?,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    @ColumnInfo(name = "abv")
    val abv: Double,
    @ColumnInfo(name = "ibu")
    val ibu: Double?
) {
    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false
}
