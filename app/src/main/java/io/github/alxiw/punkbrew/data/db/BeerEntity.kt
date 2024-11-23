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
    val ibu: Double?,
    @ColumnInfo(name = "target_fg")
    val targetFg: Double?,
    @ColumnInfo(name = "target_og")
    val targetOg: Double?,
    @ColumnInfo(name = "ebc")
    val ebc: Double?,
    @ColumnInfo(name = "srm")
    val srm: Double?,
    @ColumnInfo(name = "ph")
    val ph: Double?,
    @ColumnInfo(name = "attenuation_level")
    val attenuationLevel: Double?,
    @ColumnInfo(name = "volume_json")
    val volumeJson: String,
    @ColumnInfo(name = "boil_volume_json")
    val boilVolumeJson: String,
    @ColumnInfo(name = "method_json")
    val methodJson: String,
    @ColumnInfo(name = "ingredients_json")
    val ingredientsJson: String,
    @ColumnInfo(name = "food_pairing_json")
    val foodPairingJson: String,
    @ColumnInfo(name = "brewers_tips")
    val brewersTips: String,
    @ColumnInfo(name = "contributed_by")
    val contributedBy: String,
) {
    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false
}
