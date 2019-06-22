package io.github.alxiw.punkbrew

import android.app.Application
import io.github.alxiw.punkbrew.di.AppComponent
import io.github.alxiw.punkbrew.di.AppModule
import io.github.alxiw.punkbrew.di.DaggerAppComponent

class PunkBrewApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = initDagger(this)
    }

    private fun initDagger(app: PunkBrewApplication): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()

}