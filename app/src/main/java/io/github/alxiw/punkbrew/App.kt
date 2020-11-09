package io.github.alxiw.punkbrew

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
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
        initStetho()
    }

    private fun initStetho() {
        if (!BuildConfig.DEBUG) {
            return
        }
        val initializer = Stetho.newInitializerBuilder(this).apply {
            // Chrome DevTools
            enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this@App))
            // Command line interface
            enableDumpapp(Stetho.defaultDumperPluginsProvider(this@App))
        }.build()
        Stetho.initialize(initializer)
    }

    companion object {
        @JvmStatic
        operator fun get(context: Context): App {
            return context.applicationContext as App
        }
    }
}
