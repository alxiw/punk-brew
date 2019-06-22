package io.github.alxiw.punkbrew.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "beers")
data class Beer(
    @PrimaryKey
    val id: Int,
    val name: String,
    val tagline: String,
    @ColumnInfo(name = "first_brewed")
    val firstBrewed: String?,
    val description: String?,
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    val abv: Double,
    val ibu: Double,
    @ColumnInfo(name = "target_fg")
    val targetFg: Double,
    @ColumnInfo(name = "target_og")
    val targetOg: Double,
    val ebc: Double,
    val srm: Double,
    val ph: Double,
    @ColumnInfo(name = "attenuation_level")
    val attenuationLevel: Double,
    @ColumnInfo(name = "brewers_tips")
    val brewersTips: String,
    @ColumnInfo(name = "contributed_by")
    val contributedBy: String
) : Serializable {
    @ColumnInfo(name = "is_favourite")
    var isFavourite : Boolean = false

    override fun equals(other: Any?): Boolean {
        return if (other is Beer && other.id == this.id) {
            return this.hashCode() == other.hashCode()
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + tagline.hashCode()
        result = 31 * result + (firstBrewed?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + abv.hashCode()
        result = 31 * result + ibu.hashCode()
        result = 31 * result + targetFg.hashCode()
        result = 31 * result + targetOg.hashCode()
        result = 31 * result + ebc.hashCode()
        result = 31 * result + srm.hashCode()
        result = 31 * result + ph.hashCode()
        result = 31 * result + attenuationLevel.hashCode()
        result = 31 * result + brewersTips.hashCode()
        result = 31 * result + contributedBy.hashCode()
        return result
    }
}