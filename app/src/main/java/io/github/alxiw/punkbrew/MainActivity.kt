package io.github.alxiw.punkbrew

import android.os.Bundle
import io.github.alxiw.punkbrew.presentation.catalog.CatalogFragment
import io.github.alxiw.punkbrew.databinding.ActivityMainBinding
import io.github.alxiw.punkbrew.presentation.details.DetailsFragment
import io.github.alxiw.punkbrew.presentation.favorites.FavoritesFragment
import io.github.alxiw.punkbrew.presentation.navigation.Navigator
import org.koin.androidx.scope.ScopeActivity

class MainActivity : ScopeActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            openCatalog()
        }
    }

    override fun openCatalog() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.root_container,
                CatalogFragment.newInstance(),
                CatalogFragment.BACK_STACK_CATALOG_TAG
            )
            .commit()
    }

    override fun openFavorites() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.root_container,
                FavoritesFragment.newInstance(),
                FavoritesFragment.BACK_STACK_FAVORITES_TAG
            )
            .addToBackStack(null)
            .commit()
    }

    override fun openDetails(id: Int) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.root_container,
                DetailsFragment.newInstance(id),
                DetailsFragment.BACK_STACK_DETAILS_TAG
            )
            .addToBackStack(null)
            .commit()
    }
}
