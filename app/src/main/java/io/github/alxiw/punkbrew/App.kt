package io.github.alxiw.punkbrew

import android.app.Application
import android.content.Context
import io.github.alxiw.punkbrew.di.appModule
import io.github.alxiw.punkbrew.di.databaseModule
import io.github.alxiw.punkbrew.di.networkModule
import io.github.alxiw.punkbrew.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(appModule, networkModule, databaseModule, repositoryModule))
        }
    }

    companion object {
        @JvmStatic
        operator fun get(context: Context): App {
            return context.applicationContext as App
        }
    }
}
