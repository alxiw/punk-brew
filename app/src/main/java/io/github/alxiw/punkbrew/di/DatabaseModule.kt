package io.github.alxiw.punkbrew.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.github.alxiw.punkbrew.db.BrewDao
import io.github.alxiw.punkbrew.db.BrewDatabase
import io.github.alxiw.punkbrew.db.BrewCache
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesRoomDatabase(app: Context): BrewDatabase {
        return Room.databaseBuilder(app, BrewDatabase::class.java, "PUNKBREW.DB").build()
    }

    @Singleton
    @Provides
    fun providesBrewDao(demoDatabase: BrewDatabase): BrewDao {
        return demoDatabase.getBrewDao()
    }

    @Provides
    @Singleton
    fun provideBrewCache(dao: BrewDao): BrewCache = BrewCache(dao)

}