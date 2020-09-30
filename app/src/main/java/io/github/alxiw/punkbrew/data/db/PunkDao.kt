package io.github.alxiw.punkbrew.data.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single

@Dao
interface PunkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(beer: BeerEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(beers: List<BeerEntity>)

    @Query("DELETE FROM beers WHERE id = :id")
    fun delete(id: Long)

    @Query("DELETE FROM beers")
    fun deleteAll()

    @Query("SELECT * FROM beers WHERE id=:id LIMIT 1")
    fun beer(id: Int): Single<BeerEntity>

    @Query("SELECT * FROM beers")
    fun beers(): DataSource.Factory<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE (id IS :query)")
    fun beersById(query: Int): DataSource.Factory<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE (name LIKE :query)")
    fun beersByName(query: String): DataSource.Factory<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE (favorite = 1)")
    fun favorites(): DataSource.Factory<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE (favorite = 1 AND name LIKE :query)")
    fun favoritesByName(query: String): DataSource.Factory<Int, BeerEntity>
}
