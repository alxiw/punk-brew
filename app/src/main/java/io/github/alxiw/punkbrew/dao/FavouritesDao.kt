package io.github.alxiw.punkbrew.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.github.alxiw.punkbrew.entities.FavouriteEntity

@Dao
interface FavouritesDao {

    @Insert
    fun insertBeer(favouriteEntity : FavouriteEntity)

    @Query("SELECT * FROM favourites")
    fun getFavourites(): List<FavouriteEntity>

    @Query("SELECT * FROM favourites WHERE id=:id LIMIT 1")
    fun getFavouriteById(id: Int): FavouriteEntity

    @Query("DELETE FROM favourites WHERE id=:id")
    fun remove(id: Int)

}