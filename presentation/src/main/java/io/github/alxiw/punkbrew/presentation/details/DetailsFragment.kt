package io.github.alxiw.punkbrew.presentation.details

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import dev.androidbroadcast.vbpd.viewBinding
import io.github.alxiw.punkbrew.presentation.R
import io.github.alxiw.punkbrew.presentation.databinding.FragmentDetailsBinding
import io.github.alxiw.punkbrew.domain.loader.ImageLoader
import io.github.alxiw.punkbrew.domain.model.BeerDetails
import io.github.alxiw.punkbrew.presentation.base.BaseFragment
import io.github.alxiw.punkbrew.presentation.catalog.CatalogFragment
import io.github.alxiw.punkbrew.presentation.details.items.HeaderItem
import io.github.alxiw.punkbrew.presentation.details.items.TextItem
import io.github.alxiw.punkbrew.presentation.favorites.FavoritesFragment
import io.github.alxiw.punkbrew.presentation.list.BeersFragment
import io.github.alxiw.punkbrew.presentation.util.hide
import io.github.alxiw.punkbrew.presentation.util.load
import io.github.alxiw.punkbrew.presentation.util.show
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsFragment : BaseFragment<DetailsViewModel>(R.layout.fragment_details), MenuProvider {

    override val viewModel: DetailsViewModel by viewModel()

    private val binding by viewBinding(FragmentDetailsBinding::bind)

    private val imageLoader: ImageLoader by inject()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var favoriteItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { viewModel.beerId = it.getInt(BEER_ID_KEY) }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_details, menu)
        favoriteItem = menu.findItem(R.id.details_menu_favorite)
        updateFavoriteIcon(viewModel.currentBeer)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.details_menu_favorite -> {
                onFavoriteBadgeClicked()
                true
            }
            else -> {
                false
            }
        }
    }

    override fun setupToolbar() {
        binding.detailsToolbar.also {
            val activity = activity as AppCompatActivity
            activity.setSupportActionBar(it)
            it.title = ""
            it.subtitle = ""
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
        }

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun initView(view: View) {
        binding.detailsContent.beerDetailsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = groupAdapter
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.beer.collect { beer ->
                    beer?.let {
                        Log.d("HELLO", "Received beer ${it.id} to show details")
                        initViews(it)
                    }
                }
            }
        }

        if (viewModel.currentBeer == null) {
            viewModel.findBeer()
        }
    }

    private fun updateFavoriteIcon(beer: BeerDetails?) {
        if (beer == null) {
            favoriteItem?.isVisible = false
            return
        }
        favoriteItem?.let {
            it.setIcon(
                if (beer.favorite) {
                    R.drawable.ic_menu_favorite_true
                } else {
                    R.drawable.ic_menu_favorite_false
                }
            )
            it.isVisible = true
        }
    }

    private fun initViews(beer: BeerDetails) {
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            title = beer.name
            subtitle = resources.getString(R.string.app_tagline)
        }
        updateFavoriteIcon(beer)
        updateBasicsView(beer)
        updateRecyclerView(beer)
        binding.detailsContent.beerDetailsCopyright.text = beer.contributedBy
    }

    private fun updateBasicsView(beer: BeerDetails) {
        with(binding.detailsContent) {
            beerDetailsNumber.text = beer.number
            beerDetailsDate.text = beer.firstBrewed

            beerDetailsImage.load(imageLoader, beer.image, R.drawable.bottle) {
                view?.let {
                    beerDetailsImage.alpha = 0f
                    beerDetailsImage.animate().setDuration(500).alpha(1f).start()
                }
            }

            beerDetailsAbvValue.text = beer.abv
            beerDetailsIbuValue.text = beer.ibu
            beerDetailsTargetOgValue.text = beer.targetOg
            beerDetailsTargetFgValue.text = beer.targetFg
            beerDetailsEbcValue.text = beer.ebc
            beerDetailsSrmValue.text = beer.srm
            beerDetailsPhValue.text = beer.ph
            beerDetailsAttenuationValue.text = beer.attenuationLevel

            beerDetailsVolumeValue.text = beer.volume
            beerDetailsBoilVolumeValue.text = beer.boilVolume

            beerDetailsName.text = beer.name
            beerDetailsTagline.text = beer.tagline
        }
    }

    private fun updateRecyclerView(beer: BeerDetails) {
        val descriptionSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_description)))
            add(TextItem(beer.description))
        }

        val foodPairingSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_food_pairing)))
            addAll(beer.foodPairing.map { TextItem(it) })
        }

        val methodSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_method)))
            addAll(beer.method.map { TextItem(it) })
        }

        val ingredientsSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_ingredients)))
            addAll(beer.ingredients.map { TextItem(it) })
        }

        val brewersTipsSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_brewers_tips)))
            add(TextItem(beer.brewersTips))
        }

        groupAdapter.clear()
        groupAdapter.apply {
            add(descriptionSection)
            if (beer.foodPairing.isNotEmpty()) add(foodPairingSection)
            if (beer.method.isNotEmpty()) add(methodSection)
            if (beer.ingredients.isNotEmpty()) add(ingredientsSection)
            add(brewersTipsSection)
        }
    }

    override fun onLoading() {
        with(binding) {
            detailsProgressBar.show()
            detailsContent.root.hide()
            detailsError.hide()
        }
    }

    override fun onContentReceived() {
        with(binding) {
            detailsContent.root.show()
            detailsError.hide()
            detailsProgressBar.hide()
        }
    }

    override fun onEmptyContent() {
        with(binding) {
            detailsError.show()
            detailsContent.root.hide()
            detailsProgressBar.hide()
        }
    }

    private fun onFavoriteBadgeClicked() {
        viewModel.toggleFavorite {
            if (isAdded) {
                updateList(CatalogFragment.BACK_STACK_CATALOG_TAG)
                updateList(FavoritesFragment.BACK_STACK_FAVORITES_TAG)
            }
        }
    }

    private fun updateList(tag: String) {
        val listFragment = parentFragmentManager.findFragmentByTag(tag)
        if (listFragment != null && listFragment is BeersFragment) {
            listFragment.onBeerUpdated()
        }
    }

    private fun finish() {
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object {
        const val BACK_STACK_DETAILS_TAG = "BACK_STACK_DETAILS_TAG"

        private const val BEER_ID_KEY = "beer_id"

        @JvmStatic
        fun newInstance(id: Int): DetailsFragment {
            return DetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(BEER_ID_KEY, id)
                }
            }
        }
    }
}
