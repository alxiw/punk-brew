package io.github.alxiw.punkbrew.ui.favorites

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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_CATALOG_TAG
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_DETAILS_TAG
import io.github.alxiw.punkbrew.ui.details.DetailsFragment
import io.github.alxiw.punkbrew.ui.list.BeersFragment
import kotlinx.coroutines.launch
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.beers.collect { list ->
                    list?.let {
                        Log.d("HELLO", "Received list of favorites with size of: ${it.size}")
                        adapter.submitList(it)
                    }
                }
            }
        }
    }

    override fun onBeerClicked(beer: BeerEntity) {
        parentFragmentManager.beginTransaction()
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
            if (!isAdded) {
                return@updateBeer
            }

            if (!updatedBeer.favorite) {
                itemView.findViewById<ImageView>(R.id.item_favorite).setImageResource(R.drawable.badge_favorite_false)
                onBeerUpdated()
                updateFragment(BACK_STACK_CATALOG_TAG)
            }
        }
    }

    private fun updateFragment(tag: String) {
        parentFragmentManager.findFragmentByTag(tag)?.let {
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
