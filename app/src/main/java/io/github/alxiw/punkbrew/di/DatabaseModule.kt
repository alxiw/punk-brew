package io.github.alxiw.punkbrew.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.alxiw.punkbrew.data.db.BeersDao
import io.github.alxiw.punkbrew.data.db.PunkDatabase
import io.github.alxiw.punkbrew.data.source.BeersLocalSource
import org.koin.dsl.module
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val DB_NAME = "punkbrew.db"

val databaseModule = module {

    factory { Executors.newSingleThreadExecutor() as Executor }
    factory { (get() as PunkDatabase).beersDao() as BeersDao }
    factory {
        Room.databaseBuilder((get() as Context), PunkDatabase::class.java, DB_NAME)
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .build() as PunkDatabase
    }
    factory { BeersLocalSource(get(), get()) as BeersLocalSource }
}

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE beers ADD COLUMN target_fg DOUBLE default NULL")
        database.execSQL("ALTER TABLE beers ADD COLUMN target_og DOUBLE default NULL")
        database.execSQL("ALTER TABLE beers ADD COLUMN ebc DOUBLE default NULL")
        database.execSQL("ALTER TABLE beers ADD COLUMN srm DOUBLE default NULL")
        database.execSQL("ALTER TABLE beers ADD COLUMN ph DOUBLE default NULL")
        database.execSQL("ALTER TABLE beers ADD COLUMN attenuation_level DOUBLE default NULL")
        database.execSQL("ALTER TABLE beers ADD COLUMN brewers_tips TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE beers ADD COLUMN contributed_by TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE beers ADD COLUMN volume_json TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE beers ADD COLUMN boil_volume_json TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE beers ADD COLUMN method_json TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE beers ADD COLUMN ingredients_json TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE beers ADD COLUMN food_pairing_json TEXT NOT NULL DEFAULT ''")
    }
}
