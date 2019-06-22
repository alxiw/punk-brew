package io.github.alxiw.punkbrew.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.alxiw.punkbrew.model.Beer

@Database(
        entities = [Beer::class],
        version = 1,
        exportSchema = false
)
abstract class BrewDatabase : RoomDatabase() {

    abstract fun getBrewDao() : BrewDao

}
