package io.github.alxiw.punkbrew.presentation.details

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
import io.github.alxiw.punkbrew.presentation.base.UiEvent
import io.github.alxiw.punkbrew.presentation.catalog.CatalogFragment
import io.github.alxiw.punkbrew.presentation.details.items.HeaderItem
import io.github.alxiw.punkbrew.presentation.details.items.TextItem
import io.github.alxiw.punkbrew.presentation.favorites.FavoritesFragment
import io.github.alxiw.punkbrew.presentation.list.BeersFragment
import io.github.alxiw.punkbrew.presentation.navigation.Navigator
import io.github.alxiw.punkbrew.presentation.util.hide
import io.github.alxiw.punkbrew.presentation.util.load
import io.github.alxiw.punkbrew.presentation.util.show
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class DetailsFragment : BaseFragment<DetailsViewModel>(R.layout.fragment_details), MenuProvider {

    override val viewModel: DetailsViewModel by viewModel()

    private val binding by viewBinding(FragmentDetailsBinding::bind)
    private val imageLoader: ImageLoader by inject()

    private val navigator: Navigator by lazy {
        (requireActivity() as ScopeActivity)
            .scope
            .get<Navigator>()
    }

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private var favoriteItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.beerId = arguments?.getInt(BEER_ID_KEY)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_details, menu)
        favoriteItem = menu.findItem(R.id.details_menu_favorite)
        updateFavoriteIcon()
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        if (item.itemId != R.id.details_menu_favorite) return false
        viewModel.toggleFavorite()
        return true
    }

    override fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.detailsToolbar)
        binding.detailsToolbar.apply {
            title = ""
            subtitle = ""
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener { navigator.close() }
        }
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun initView(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.detailsAppBarLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.detailsContent.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        binding.detailsContent.beerDetailsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = groupAdapter
        }

        binding.detailsAppBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            if (totalScrollRange == 0) return@addOnOffsetChangedListener
            
            val offset = abs(verticalOffset).toFloat()
            val percentage = offset / totalScrollRange

            // Сабтайтл исчезает первым на 10% скролла
            binding.detailsToolbar.subtitle = if (percentage > 0.1f) "" else getString(R.string.app_tagline)

            // Начинаем плавно скрывать тайтл, кнопка назад и сердечко после того, как сабтайтл ушел
            val contentAlpha = when {
                percentage < 0.2f -> 1f
                percentage > 0.8f -> 0f
                else -> 1f - (percentage - 0.2f) / 0.6f
            }
            binding.detailsToolbar.alpha = contentAlpha
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.beer.collect { beer ->
                        beer?.let {
                            Log.d("HELLO", "Received beer ${it.id} to show details")
                            (activity as? AppCompatActivity)?.supportActionBar?.apply {
                                title = beer.name
                            }
                            updateFavoriteIcon()
                            updateBasicsView(beer)
                            updateRecyclerView(beer)
                            binding.detailsContent.beerDetailsCopyright.text = beer.contributedBy
                        }
                    }
                }
                launch {
                    viewModel.events.collect { event ->
                        if (event is UiEvent.FavoriteToggled) {
                            listOf(CatalogFragment.BACK_STACK_CATALOG_TAG, FavoritesFragment.BACK_STACK_FAVORITES_TAG)
                                .forEach { updateList(it) }
                        }
                    }
                }
            }
        }
        viewModel.findBeer()
    }

    private fun updateFavoriteIcon() {
        val favorite = viewModel.beer.value?.favorite
        favoriteItem?.isVisible = viewModel.beerId != null && favorite != null
        favoriteItem?.setIcon(
            if (favorite == true) R.drawable.ic_menu_favorite_true
            else R.drawable.ic_menu_favorite_false
        )
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
        groupAdapter.clear()
        groupAdapter.apply {
            add(Section().apply {
                setHeader(HeaderItem(getString(R.string.header_description)))
                add(TextItem(beer.description))
            })
            if (beer.foodPairing.isNotEmpty()) add(Section().apply {
                setHeader(HeaderItem(getString(R.string.header_food_pairing)))
                addAll(beer.foodPairing.map { TextItem(it) })
            })
            if (beer.method.isNotEmpty()) add(Section().apply {
                setHeader(HeaderItem(getString(R.string.header_method)))
                addAll(beer.method.map { TextItem(it) })
            })
            if (beer.ingredients.isNotEmpty()) add(Section().apply {
                setHeader(HeaderItem(getString(R.string.header_ingredients)))
                addAll(beer.ingredients.map { TextItem(it) })
            })
            add(Section().apply {
                setHeader(HeaderItem(getString(R.string.header_brewers_tips)))
                add(TextItem(beer.brewersTips))
            })
        }
    }

    override fun onLoading() = with(binding) {
        detailsProgressBar.show()
        detailsContent.root.hide()
        detailsError.hide()
    }

    override fun onContentReceived() = with(binding) {
        detailsContent.root.show()
        detailsError.hide()
        detailsProgressBar.hide()
    }

    override fun onEmptyContent() = with(binding) {
        detailsError.show()
        detailsContent.root.hide()
        detailsProgressBar.hide()
    }

    private fun updateList(tag: String) {
        (parentFragmentManager.findFragmentByTag(tag) as? BeersFragment)?.onBeerUpdated()
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
