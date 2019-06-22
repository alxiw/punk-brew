package io.github.alxiw.punkbrew.ui.splash

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.PunkBrewApplication
import io.github.alxiw.punkbrew.ui.home.HomeActivity
import javax.inject.Inject

class SplashActivity : AppCompatActivity(), SplashView {

    private lateinit var mTextHello : TextView

    @Inject
    lateinit var splashPresenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        (application as PunkBrewApplication).appComponent.inject(this)

        splashPresenter.setView(this)

        mTextHello = findViewById(R.id.text_splash_hello)

        splashPresenter.enter(true)

    }

    override fun showError(textResource: Int) {
        Toast.makeText(applicationContext, getString(textResource), Toast.LENGTH_SHORT).show()
    }

    override fun openHome() {
        val intent = Intent(this@SplashActivity, HomeActivity::class.java)
        startActivity(intent)
        this@SplashActivity.overridePendingTransition(R.anim.animate_fade_enter, R.anim.animate_fade_exit)
        finish()
    }

}
