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
import io.github.alxiw.punkbrew.util.hide
import io.github.alxiw.punkbrew.util.load
import io.github.alxiw.punkbrew.util.show
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsFragment : BaseFragment<DetailsViewModel>(), MenuProvider {

    private val binding by viewBinding(FragmentDetailsBinding::bind)

    private val imageLoader: ImageLoader by inject()

    private val detailsLoader: DetailsLoader by inject()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override val viewModel: DetailsViewModel by viewModel()
    override val layoutId: Int = R.layout.fragment_details

    private var favoriteItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.beerId = it.getInt(BEER_ID_KEY)
        }
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
        //binding.detailsToolbar.title = beer.name
        (activity as? AppCompatActivity)?.supportActionBar?.title = beer.name
        updateFavoriteIcon(beer)

        binding.detailsContent.beerDetailsImage.load(imageLoader, beer.image, R.drawable.bottle) {
            binding.detailsContent.beerDetailsImage.alpha = 0f
            binding.detailsContent.beerDetailsImage.animate().setDuration(500).alpha(1f).start()
        }

        binding.detailsContent.beerDetailsId.text = String.format("#%s", beer.id)
        binding.detailsContent.beerDetailsDate.text = DateFormatter.formatDate(beer.firstBrewed, false)

        updateBasicsView(beer)
        binding.detailsContent.beerDetailsName.text = beer.name
        binding.detailsContent.beerDetailsTagline.text = beer.tagline

        updateRecyclerView(beer)
        binding.detailsContent.beerDetailsCopyright.text = String.format("Contributed by %s", beer.contributedBy)
    }

    private fun updateBasicsView(beer: BeerEntity) {
        with(binding.detailsContent) {
            beerDetailsAbvValue.text = String.format("%s%%", beer.abv)
            beerDetailsIbuValue.text = String.format("%s", beer.ibu)
            beerDetailsTargetOgValue.text = String.format("%s", beer.targetOg)
            beerDetailsTargetFgValue.text = String.format("%s", beer.targetFg)
            beerDetailsEbcValue.text = String.format("%s", beer.ebc)
            beerDetailsSrmValue.text = String.format("%s", beer.srm)
            beerDetailsPhValue.text = String.format("%s", beer.ph)
            beerDetailsAttenuationValue.text = String.format("%s%%", beer.attenuationLevel)
            val volume = detailsLoader.getVolume(beer.volumeJson)
            val boilVolume = detailsLoader.getVolume(beer.boilVolumeJson)
            beerDetailsVolumeValue.text = String.format(
                "%s${if (volume.second.equals("litres", ignoreCase = true)) "L" else ""}",
                volume.first
            )
            beerDetailsBoilVolumeValue.text = String.format(
                "%s${if (boilVolume.second.equals("litres", ignoreCase = true)) "L" else ""}",
                boilVolume.first
            )
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

        val brewersTipsSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_brewers_tips)))
            add(TextItem(beer.brewersTips))
        }

        groupAdapter.clear()
        groupAdapter.apply {
            add(descriptionSection)
            add(foodPairingSection)
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
