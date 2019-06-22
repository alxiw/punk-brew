package io.github.alxiw.punkbrew.di

import dagger.Component
import io.github.alxiw.punkbrew.ui.favourites.FavouritesActivity
import io.github.alxiw.punkbrew.ui.home.HomeActivity
import io.github.alxiw.punkbrew.ui.splash.SplashActivity
import io.github.alxiw.punkbrew.ui.details.DetailsActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    PresenterModule::class,
    DatabaseModule::class,
    ServiceModule::class,
    RepositoryModule::class])
interface AppComponent {

    fun inject(target: SplashActivity)
    fun inject(target: HomeActivity)
    fun inject(target: FavouritesActivity)
    fun inject(target: DetailsActivity)

}