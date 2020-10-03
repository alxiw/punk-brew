package io.github.alxiw.punkbrew.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BeerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PunkDatabase : RoomDatabase()  {

    abstract fun punkDao(): PunkDao
}