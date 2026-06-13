package io.github.alxiw.punkbrew.presentation.favorites

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.alxiw.punkbrew.presentation.R
import io.github.alxiw.punkbrew.presentation.catalog.CatalogFragment
import io.github.alxiw.punkbrew.presentation.list.BeersFragment
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
        binding.toolbar.apply {
            (activity as AppCompatActivity).setSupportActionBar(this)
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener { navigator.close() }
            title = getString(R.string.favorites_label)
        }

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun initView(view: View) {
        super.initView(view)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.beers.collect { list ->
                    list?.let {
                        adapter.submitList(it)
                    }
                }
            }
        }
    }

    override fun onFavoriteToggled(id: Int, favorite: Boolean) {
        if (!favorite) { // new value
            onBeerUpdated()
            updateFragment(CatalogFragment.BACK_STACK_CATALOG_TAG)
        }
    }

    private fun updateFragment(tag: String) {
        parentFragmentManager.findFragmentByTag(tag)?.let {
            if (it is BeersFragment) {
                it.onBeerUpdated()
            }
        }
    }

    companion object {
        const val BACK_STACK_FAVORITES_TAG = "BACK_STACK_FAVORITES_TAG"

        @JvmStatic
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }
}
