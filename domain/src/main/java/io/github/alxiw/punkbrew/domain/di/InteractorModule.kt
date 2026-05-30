package io.github.alxiw.punkbrew.domain.di

import android.content.Context
import io.github.alxiw.punkbrew.data.di.databaseModule
import io.github.alxiw.punkbrew.data.di.networkModule
import io.github.alxiw.punkbrew.data.di.repositoryModule
import io.github.alxiw.punkbrew.domain.BeersInteractor
import io.github.alxiw.punkbrew.domain.Interactor
import io.github.alxiw.punkbrew.domain.loader.BeerImageLoader
import io.github.alxiw.punkbrew.domain.loader.ImageLoader
import io.github.alxiw.punkbrew.domain.mapper.DetailsFormatter
import io.github.alxiw.punkbrew.domain.mapper.BeerMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val interactorModule = module {

    single { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    factory { DetailsFormatter(get(), get() as Context) }
    factory { BeerMapper(get()) }
    factory { BeerImageLoader(get()) as ImageLoader }
    factory { BeersInteractor(get(), get(), get(), get()) as Interactor }
}

val allModules = listOf(
    databaseModule,
    networkModule,
    repositoryModule,
    interactorModule
)
