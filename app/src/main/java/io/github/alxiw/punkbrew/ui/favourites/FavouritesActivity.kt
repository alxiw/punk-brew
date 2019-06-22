package io.github.alxiw.punkbrew.ui.favourites

import android.os.Bundle
import android.view.MenuItem
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.PunkBrewApplication
import io.github.alxiw.punkbrew.model.Beer
import io.github.alxiw.punkbrew.ui.base.BaseActivity
import javax.inject.Inject

class FavouritesActivity : FavouritesView, BaseActivity() {

    @Inject
    lateinit var favouritesPresenter: FavouritesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as PunkBrewApplication).appComponent.inject(this)

        favouritesPresenter.setView(this)

        loadBeerPage()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            this@FavouritesActivity.overridePendingTransition(R.anim.animate_fade_enter, R.anim.animate_fade_exit)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // VIEW METHODS

    override fun updateList(beers: List<Beer>) {
        mRecyclerView.post {
            updateRecyclerView(beers)
        }
    }

    override fun addLoadingFooter() {
        mAdapter.addLoadingFooter()
    }

    // OVERRIDE ABSTRACT METHODS

    override fun loadBeerPage() {
        favouritesPresenter.loadFavouritesPage(currentPage, pageSize)
    }

    override fun onFavoriteItemButtonClick(beer: Beer) {
        favouritesPresenter.deleteBeer(beer)
        mAdapter.remove(beer)
    }
}