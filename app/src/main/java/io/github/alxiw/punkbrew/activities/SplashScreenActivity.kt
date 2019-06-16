package io.github.alxiw.punkbrew.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import io.github.alxiw.punkbrew.R

class SplashScreenActivity : AppCompatActivity() {

    private val delay: Long = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({ goToHomeScreen() }, delay)
    }

    private fun goToHomeScreen() {
        val intent = Intent(this@SplashScreenActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
