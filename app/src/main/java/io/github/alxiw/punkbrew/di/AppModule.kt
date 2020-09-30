package io.github.alxiw.punkbrew.di

import android.content.Context
import android.content.SharedPreferences
import io.github.alxiw.punkbrew.data.PunkRepository
import io.github.alxiw.punkbrew.ui.beers.BeersViewModel
import io.github.alxiw.punkbrew.ui.details.DetailsViewModel
import io.github.alxiw.punkbrew.ui.favorites.FavoritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val PREF_FILE_NAME = "punkbrew_prefs"

val appModule = module {

    single {
        (get() as Context).getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE
        ) as SharedPreferences
    }
    factory { PunkRepository(get(), get()) }
    viewModel { BeersViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { DetailsViewModel(get()) }
}
