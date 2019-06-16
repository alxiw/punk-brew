package io.github.alxiw.punkbrew.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import io.github.alxiw.punkbrew.entities.BeerEntity
import io.github.alxiw.punkbrew.entities.FavouriteEntity

@Database(entities = [BeerEntity::class, FavouriteEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getBeerDao() : BeerDao

    abstract fun getFavouritesDao() : FavouritesDao

    companion object {
        private var appDatabase: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase? {
            if (appDatabase == null){
                synchronized(AppDatabase::class){
                    appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "PUNKBREW")
                        .allowMainThreadQueries().build()
                }
            }
            return appDatabase
        }

        fun closeDatabase(){
            appDatabase?.close()
            appDatabase = null
        }
    }
}