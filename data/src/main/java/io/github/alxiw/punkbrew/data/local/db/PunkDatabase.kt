package io.github.alxiw.punkbrew.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.alxiw.punkbrew.data.local.model.BeerEntity

@Database(
    entities = [BeerEntity::class],
    version = 4,
    exportSchema = false
)
internal abstract class PunkDatabase : RoomDatabase()  {

    abstract fun beersDao(): BeersDao
}
