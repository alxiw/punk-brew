package io.github.alxiw.punkbrew.data.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single

@Dao
interface BeersDao {

    @Query("INSERT OR REPLACE INTO beers (id, name, tagline, first_brewed, description, image_url, abv, ibu, target_fg, target_og, ebc, srm, ph, attenuation_level, volume_json, boil_volume_json, method_json, ingredients_json, food_pairing_json, brewers_tips, contributed_by, favorite) VALUES ( :id, :name, :tagline, :firstBrewed, :description, :imageUrl, :abv, :ibu, :targetFg, :targetOg, :ebc, :srm, :ph, :attenuationLevel, :volumeJson, :boilVolumeJson, :methodJson, :ingredientsJson, :foodPairingJson, :brewersTips, :contributedBy, COALESCE((SELECT favorite FROM beers WHERE id = :id), 0))")
    fun insert(
        id: Int,
        name: String,
        tagline: String,
        firstBrewed: String?,
        description: String?,
        imageUrl: String?,
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
    fun beer(id: Int): Single<BeerEntity>

    @Query("SELECT * FROM beers")
    fun beers(): DataSource.Factory<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE (id IS :id)")
    fun beersById(id: Int): DataSource.Factory<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE (name LIKE :name)")
    fun beersByName(name: String): DataSource.Factory<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE (favorite = 1)")
    fun favorites(): DataSource.Factory<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE (favorite = 1 AND name LIKE :name)")
    fun favoritesByName(name: String): DataSource.Factory<Int, BeerEntity>
}
