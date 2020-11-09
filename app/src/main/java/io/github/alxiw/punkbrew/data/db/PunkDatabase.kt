package io.github.alxiw.punkbrew.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BeerEntity::class],
    version = 3,
    exportSchema = false
)
abstract class PunkDatabase : RoomDatabase()  {

    abstract fun beersDao(): BeersDao
}
