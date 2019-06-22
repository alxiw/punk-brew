package io.github.alxiw.punkbrew.di

import dagger.Module
import dagger.Provides
import io.github.alxiw.punkbrew.data.BrewRepository
import io.github.alxiw.punkbrew.ui.details.DetailsPresenter
import io.github.alxiw.punkbrew.ui.favourites.FavouritesPresenter
import io.github.alxiw.punkbrew.ui.home.HomePresenter
import io.github.alxiw.punkbrew.ui.splash.SplashPresenter
import javax.inject.Singleton

@Module
class PresenterModule {

    @Provides
    @Singleton
    fun provideSplashPresenter(): SplashPresenter = SplashPresenter()

    @Provides
    @Singleton
    fun provideHomePresenter(repository: BrewRepository): HomePresenter = HomePresenter(repository)

    @Provides
    @Singleton
    fun provideFavouritesPresenter(repository: BrewRepository): FavouritesPresenter = FavouritesPresenter(repository)

    @Provides
    @Singleton
    fun provideDetailsPresenter(repository: BrewRepository): DetailsPresenter = DetailsPresenter(repository)

}