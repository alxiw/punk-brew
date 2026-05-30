package io.github.alxiw.punkbrew.ui.details

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
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.loader.DetailsLoader
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.data.loader.ImageLoader
import io.github.alxiw.punkbrew.databinding.FragmentDetailsBinding
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_CATALOG_TAG
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_FAVORITES_TAG
import io.github.alxiw.punkbrew.ui.base.BaseFragment
import io.github.alxiw.punkbrew.ui.details.items.HeaderItem
import io.github.alxiw.punkbrew.ui.details.items.TextItem
import io.github.alxiw.punkbrew.ui.list.BeersFragment
import io.github.alxiw.punkbrew.util.DateFormatter
import io.github.alxiw.punkbrew.util.formatNullableDegreeBeerValue
import io.github.alxiw.punkbrew.util.formatNullableSimpleBeerValue
import io.github.alxiw.punkbrew.util.hide
import io.github.alxiw.punkbrew.util.load
import io.github.alxiw.punkbrew.util.show
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsFragment : BaseFragment<DetailsViewModel>(R.layout.fragment_details), MenuProvider {

    override val viewModel: DetailsViewModel by viewModel()

    private val binding by viewBinding(FragmentDetailsBinding::bind)

    private val imageLoader: ImageLoader by inject()

    private val detailsLoader: DetailsLoader by inject()

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
                viewModel.currentBeer?.let { onFavoriteBadgeClicked(it) }
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

    private fun updateFavoriteIcon(beer: BeerEntity?) {
        if (beer == null) {
            favoriteItem?.isVisible = false
            return
        }
        favoriteItem?.let {
            it.setIcon(
                if (beer.favorite) {
                    R.drawable.ic_menu_favorite_true
                }
                else {
                    R.drawable.ic_menu_favorite_false
                }
            )
            it.isVisible = true
        }
    }

    private fun initViews(beer: BeerEntity) {
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            title = beer.name
            subtitle = resources.getString(R.string.app_tagline)
        }
        updateFavoriteIcon(beer)
        updateBasicsView(beer)
        updateRecyclerView(beer)
        binding.detailsContent.beerDetailsCopyright.text = String.format("Contributed by %s", beer.contributedBy)
    }

    private fun updateBasicsView(beer: BeerEntity) {
        with(binding.detailsContent) {
            beerDetailsId.text = String.format("#%s", beer.id)
            beerDetailsDate.text = DateFormatter.formatDate(beer.firstBrewed, false)

            beerDetailsImage.load(imageLoader, beer.image, R.drawable.bottle) {
                view?.let {
                    beerDetailsImage.alpha = 0f
                    beerDetailsImage.animate().setDuration(500).alpha(1f).start()
                }
            }

            beerDetailsAbvValue.text = formatNullableDegreeBeerValue(beer.abv) // av is non-nullable
            beerDetailsIbuValue.text = formatNullableSimpleBeerValue(beer.ibu)
            beerDetailsTargetOgValue.text = formatNullableSimpleBeerValue(beer.targetOg)
            beerDetailsTargetFgValue.text = formatNullableSimpleBeerValue(beer.targetFg)
            beerDetailsEbcValue.text = formatNullableSimpleBeerValue(beer.ebc)
            beerDetailsSrmValue.text = formatNullableSimpleBeerValue(beer.srm)
            beerDetailsPhValue.text = formatNullableSimpleBeerValue(beer.ph)
            beerDetailsAttenuationValue.text = formatNullableDegreeBeerValue(beer.attenuationLevel)

            beerDetailsVolumeValue.text = detailsLoader.getVolume(beer.volumeJson)
            beerDetailsBoilVolumeValue.text = detailsLoader.getVolume(beer.boilVolumeJson)

            beerDetailsName.text = beer.name
            beerDetailsTagline.text = beer.tagline
        }
    }

    private fun updateRecyclerView(beer: BeerEntity) {
        val descriptionSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_description)))
            add(TextItem(beer.description))
        }

        val foodPairing = detailsLoader.getFoodPairing(beer.foodPairingJson)
        val foodPairingSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_food_pairing)))
            addAll(foodPairing.map {
                TextItem(String.format(requireContext().getString(R.string.format_item_text_bullet), it))
            })
        }

        val method = detailsLoader.getMethod(beer.methodJson)
        val methodSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_method)))
            addAll(method.map { TextItem(it) })
        }

        val ingredients = detailsLoader.getIngredients(beer.ingredientsJson)
        val ingredientsSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_ingredients)))
            addAll(ingredients.map { TextItem(it) })
        }

        val brewersTipsSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_brewers_tips)))
            add(TextItem(beer.brewersTips))
        }

        groupAdapter.clear()
        groupAdapter.apply {
            add(descriptionSection)
            if (foodPairing.isNotEmpty()) add(foodPairingSection)
            if (method.isNotEmpty()) add(methodSection)
            if (ingredients.isNotEmpty()) add(ingredientsSection)
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

    private fun onFavoriteBadgeClicked(beer: BeerEntity) {
        val updatedBeer = beer.copy(favorite = !beer.favorite)
        viewModel.updateBeer(updatedBeer) {
            if (isAdded) {
                updateList(BACK_STACK_CATALOG_TAG)
                updateList(BACK_STACK_FAVORITES_TAG)
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
