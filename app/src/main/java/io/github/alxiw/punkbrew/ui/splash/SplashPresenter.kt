package io.github.alxiw.punkbrew.ui.splash

import android.os.Handler
import io.github.alxiw.punkbrew.R

class SplashPresenter {

    private val delay: Long = 1000L

    private lateinit var splashView: SplashView

    fun setView(splashView: SplashView) {
        this.splashView = splashView
    }

    fun enter(isSuccess: Boolean) {
        Handler().postDelayed({
            if (isSuccess) {
                splashView.openHome()
            } else {
                splashView.showError(R.string.message_splash_error)
            }
        }, delay)
    }

}