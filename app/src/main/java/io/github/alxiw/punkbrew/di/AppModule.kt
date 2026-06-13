package io.github.alxiw.punkbrew.di

import io.github.alxiw.punkbrew.MainActivity
import io.github.alxiw.punkbrew.presentation.di.presentationModules
import io.github.alxiw.punkbrew.presentation.navigation.Navigator
import org.koin.dsl.module

val appModule = module {

    scope<MainActivity> {
        scoped<Navigator> { getSource<MainActivity>() as Navigator }
    }
}

val allModules = appModule + presentationModules
