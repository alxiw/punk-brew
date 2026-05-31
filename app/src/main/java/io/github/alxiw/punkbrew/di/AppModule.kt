package io.github.alxiw.punkbrew.di

import android.content.Context
import android.content.SharedPreferences
import io.github.alxiw.punkbrew.MainActivity
import io.github.alxiw.punkbrew.presentation.di.presentationModules
import io.github.alxiw.punkbrew.presentation.navigation.Navigator
import org.koin.dsl.module

private const val PREF_FILE_NAME = "punkbrew_prefs"

val appModule = module {

    single {
        (get() as Context).getSharedPreferences(
            PREF_FILE_NAME,
            Context.MODE_PRIVATE
        ) as SharedPreferences
    }

    scope<MainActivity> {
        scoped<Navigator> { getSource<MainActivity>() as Navigator }
    }
}

val allModules = appModule + presentationModules
