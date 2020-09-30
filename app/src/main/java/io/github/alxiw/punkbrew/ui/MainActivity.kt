package io.github.alxiw.punkbrew.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.ui.beers.BeersFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.root_container, BeersFragment.newInstance(), BACK_STACK_BEERS_TAG)
                .commit()
        }
    }

    companion object {
        const val BACK_STACK_BEERS_TAG = "BACK_STACK_ROOT_TAG"
        const val BACK_STACK_FAVORITES_TAG = "BACK_STACK_FAVORITES_TAG"
        const val BACK_STACK_DETAILS_TAG = "BACK_STACK_FAVORITES_TAG"
    }
}
