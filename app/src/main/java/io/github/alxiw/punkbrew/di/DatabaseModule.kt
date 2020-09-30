package io.github.alxiw.punkbrew.di

import android.content.Context
import androidx.room.Room
import io.github.alxiw.punkbrew.data.db.PunkDao
import io.github.alxiw.punkbrew.data.db.PunkDatabase
import io.github.alxiw.punkbrew.data.source.PunkLocalSource
import org.koin.dsl.module
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val DB_NAME = "punkbrew.db"

val databaseModule = module {

    factory { Executors.newSingleThreadExecutor() as Executor }
    factory { (get() as PunkDatabase).punkDao() as PunkDao }
    factory {
        Room.databaseBuilder(
            (get() as Context),
            PunkDatabase::class.java,
            DB_NAME
        ).build() as PunkDatabase
    }
    factory { PunkLocalSource(get(), get()) as PunkLocalSource }
}
