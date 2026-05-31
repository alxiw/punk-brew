package io.github.alxiw.punkbrew.presentation.di

import io.github.alxiw.punkbrew.domain.di.interactorModules
import io.github.alxiw.punkbrew.presentation.catalog.CatalogViewModel
import io.github.alxiw.punkbrew.presentation.details.DetailsViewModel
import io.github.alxiw.punkbrew.presentation.favorites.FavoritesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    viewModel { CatalogViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { DetailsViewModel(get()) }
}

val presentationModules = presentationModule + interactorModules
