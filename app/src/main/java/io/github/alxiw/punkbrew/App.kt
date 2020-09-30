package io.github.alxiw.punkbrew

import android.app.Application
import io.github.alxiw.punkbrew.di.appModule
import io.github.alxiw.punkbrew.di.databaseModule
import io.github.alxiw.punkbrew.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(appModule, networkModule, databaseModule))
        }
    }
}
