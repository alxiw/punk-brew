package io.github.alxiw.punkbrew.data.local.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import io.github.alxiw.punkbrew.data.local.model.BeerEntity

@Dao
@JvmSuppressWildcards
internal interface BeersDao {

    @Query("INSERT OR REPLACE INTO beers (id, name, tagline, first_brewed, description, image_url, abv, ibu, target_fg, target_og, ebc, srm, ph, attenuation_level, volume_json, boil_volume_json, method_json, ingredients_json, food_pairing_json, brewers_tips, contributed_by, favorite) VALUES ( :id, :name, :tagline, :firstBrewed, :description, :image, :abv, :ibu, :targetFg, :targetOg, :ebc, :srm, :ph, :attenuationLevel, :volumeJson, :boilVolumeJson, :methodJson, :ingredientsJson, :foodPairingJson, :brewersTips, :contributedBy, COALESCE((SELECT favorite FROM beers WHERE id = :id), 0))")
    fun insert(
        id: Int,
        name: String,
        tagline: String,
        firstBrewed: String?,
        description: String?,
        image: String?,
        abv: Double,
        ibu: Double?,
        targetFg: Double?,
        targetOg: Double?,
        ebc: Double?,
        srm: Double?,
        ph: Double?,
        attenuationLevel: Double?,
        volumeJson: String,
        boilVolumeJson: String,
        methodJson: String,
        ingredientsJson: String,
        foodPairingJson: String,
        brewersTips: String,
        contributedBy: String
    )

    @Query("UPDATE beers SET favorite=:favorite WHERE id=:id")
    fun update(id: Int, favorite: Boolean)

    @Query("DELETE FROM beers WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM beers")
    fun deleteAll()

    @Query("SELECT * FROM beers WHERE id=:id LIMIT 1")
    suspend fun beer(id: Int): BeerEntity

    @Query("SELECT * FROM beers ORDER BY id ASC")
    fun beers(): DataSource.Factory<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE (id IS :id) ORDER BY id ASC")
    fun beersById(id: Int): DataSource.Factory<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE (name LIKE :name) ORDER BY id ASC")
    fun beersByName(name: String): DataSource.Factory<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE (favorite = 1) ORDER BY id ASC")
    fun favorites(): DataSource.Factory<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE (favorite = 1 AND name LIKE :name) ORDER BY id ASC")
    fun favoritesByName(name: String): DataSource.Factory<Int, BeerEntity>

    @Query("SELECT COUNT(*) FROM beers")
    suspend fun getBeersCount(): Int

    @Query("SELECT COUNT(*) FROM beers WHERE (name LIKE :name)")
    suspend fun getBeersCountByName(name: String): Int
}
