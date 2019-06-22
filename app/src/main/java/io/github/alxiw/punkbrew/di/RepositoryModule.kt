package io.github.alxiw.punkbrew.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.alxiw.punkbrew.api.PunkService
import io.github.alxiw.punkbrew.data.BrewRepository
import io.github.alxiw.punkbrew.db.BrewCache
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideBrewRepository(
            context: Context,
            cache: BrewCache,
            service: PunkService
    ): BrewRepository =
            BrewRepository(context, cache, service)

}