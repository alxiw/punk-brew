package io.github.alxiw.punkbrew.ui.favorites

import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_CATALOG_TAG
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_DETAILS_TAG
import io.github.alxiw.punkbrew.ui.list.BeersView
import io.github.alxiw.punkbrew.ui.details.DetailsFragment
import io.github.alxiw.punkbrew.ui.list.BeersFragment
import kotlinx.android.synthetic.main.fragment_beers.*
import kotlinx.android.synthetic.main.item_beer.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class FavoritesFragment : BeersFragment() {

    override val viewModel: FavoritesViewModel by viewModel()

    override fun setupToolbar() {
        beers_toolbar.also {
            (activity as AppCompatActivity).setSupportActionBar(it)
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            it.title = getString(R.string.favorites_label)
        }
    }

    override fun initView() {
        super.initView()
        viewModel.beers.observe(this, Observer {
            Timber.d("Received list of favorites with size of: ${it.size}")
            if (it.size > 0) {
                onContentReceived()
            } else {
                onEmptyContent()
            }
            adapter.submitList(it)
        })
    }

    override fun onBeerClicked(beer: BeerEntity) {
        requireFragmentManager().beginTransaction()
            .replace(
                R.id.root_container,
                DetailsFragment.newInstance(beer.id),
                BACK_STACK_DETAILS_TAG
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onFavoriteBadgeClicked(beer: BeerEntity, itemView: View) {
        beer.favorite = !beer.favorite
        viewModel.updateBeer(beer) {
            val mainHandler = Handler(requireContext().mainLooper)
            val runnable = Runnable {
                if (!beer.favorite) {
                    itemView.item_favorite.setImageResource(R.drawable.badge_favorite_false)
                    onBeerUpdated()
                    updateFragment(BACK_STACK_CATALOG_TAG)
                }
            }
            mainHandler.post(runnable)
        }
    }

    private fun updateFragment(tag: String) {
        requireFragmentManager().findFragmentByTag(tag)?.let {
            if (it is BeersView<*>) {
                it.onBeerUpdated()
            }
        }
    }

    private fun finish() {
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object {
        @JvmStatic
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }
}
