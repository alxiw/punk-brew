package io.github.alxiw.punkbrew.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.github.alxiw.punkbrew.entities.BeerEntity

@Dao
interface BeerDao {

    @Insert
    fun insert(beer : BeerEntity)

    @Query("SELECT * FROM beers")
    fun getBeers(): List<BeerEntity>

    @Query("SELECT * FROM beers WHERE name LIKE :query")
    fun getQueriedBeers(query: String): List<BeerEntity>

    @Query("SELECT * FROM beers WHERE id=:id LIMIT 1")
    fun getBeerById(id: Int): BeerEntity

    @Query("DELETE FROM beers")
    fun cleanAllBeers()

}