package io.github.alxiw.punkbrew.ui.favorites

import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_CATALOG_TAG
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_DETAILS_TAG
import io.github.alxiw.punkbrew.ui.details.DetailsFragment
import io.github.alxiw.punkbrew.ui.list.BeersFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : BeersFragment(), MenuProvider {

    override val viewModel: FavoritesViewModel by viewModel()

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_favorites, menu)

        val informationItem = menu.findItem(R.id.favorites_menu_information)
        informationItem.isVisible = false
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorites_menu_information -> {
                Toast.makeText(context, context?.getString(R.string.menu_information_title), Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }

    override fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.toolbar.also {
            (activity as AppCompatActivity).setSupportActionBar(it)
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            it.title = getString(R.string.favorites_label)
        }

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun initView(view: View) {
        super.initView(view)
        viewModel.beers.observe(this, Observer {
            Log.d("HELLO", "Received list of favorites with size of: ${it.size}")
            if (it.isNotEmpty()) {
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
        val updatedBeer = beer.copy(favorite = !beer.favorite)
        viewModel.updateBeer(updatedBeer) {
            val mainHandler = Handler(requireContext().mainLooper)
            val runnable = Runnable {
                if (!updatedBeer.favorite) {
                    itemView.findViewById<ImageView>(R.id.item_favorite).setImageResource(R.drawable.badge_favorite_false)
                    onBeerUpdated()
                    updateFragment(BACK_STACK_CATALOG_TAG)
                }
            }
            mainHandler.post(runnable)
        }
    }

    private fun updateFragment(tag: String) {
        requireFragmentManager().findFragmentByTag(tag)?.let {
            if (it is BeersFragment) {
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
