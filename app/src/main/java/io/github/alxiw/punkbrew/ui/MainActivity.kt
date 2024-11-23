package io.github.alxiw.punkbrew.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.ui.catalog.CatalogFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.root_container, CatalogFragment.newInstance(), BACK_STACK_CATALOG_TAG)
                .commit()
        }
    }

    companion object {
        const val BACK_STACK_CATALOG_TAG = "BACK_STACK_CATALOG_TAG"
        const val BACK_STACK_FAVORITES_TAG = "BACK_STACK_FAVORITES_TAG"
        const val BACK_STACK_DETAILS_TAG = "BACK_STACK_DETAILS_TAG"
    }
}
