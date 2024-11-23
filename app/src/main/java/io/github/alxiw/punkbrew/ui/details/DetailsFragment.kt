package io.github.alxiw.punkbrew.ui.details

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.api.BeerResponse
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.databinding.FragmentDetailsBinding
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_CATALOG_TAG
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_FAVORITES_TAG
import io.github.alxiw.punkbrew.ui.base.BaseFragment
import io.github.alxiw.punkbrew.ui.details.items.HeaderItem
import io.github.alxiw.punkbrew.ui.details.items.TextItem
import io.github.alxiw.punkbrew.ui.list.BeersView
import io.github.alxiw.punkbrew.util.DateFormatter
import io.github.alxiw.punkbrew.util.EMPTY_PLACEHOLDER
import io.github.alxiw.punkbrew.util.hide
import io.github.alxiw.punkbrew.util.show
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class DetailsFragment : BaseFragment<DetailsViewModel>() {

    override val viewModel: DetailsViewModel by viewModel()
    override val layoutId: Int = R.layout.fragment_details

    private val gson: Gson by inject()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var beerId = -1
    private var favoriteItem: MenuItem? = null
    private val disposables = ArrayList<Disposable>()

    private lateinit var binding: FragmentDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            beerId = it.getInt(BEER_ID_KEY)
        }

        restoreSavedInstanceState(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.findBeer(beerId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_details, menu)

        favoriteItem = menu.findItem(R.id.details_menu_favorite)
        if (viewModel.currentBeer?.favorite == true) {
            favoriteItem?.setIcon(R.drawable.ic_menu_favorite_true)
        }
        initFavoriteItem(viewModel.currentBeer)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun setupToolbar() {
        binding.detailsToolbar.also {
            (activity as AppCompatActivity).setSupportActionBar(it)
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
        }
    }

    override fun initView(view: View) {
        binding = FragmentDetailsBinding.bind(view)
        binding.detailsContent.beerDetailsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = groupAdapter
        }
        val beerId = arguments?.getInt(BEER_ID_KEY) ?: -1
        if (beerId != -1) {
            viewModel.beer.observe(this, Observer { single ->
                disposables.add(
                    single.subscribeOn(Schedulers.io())
                        //.delay(5, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { beer ->
                                Timber.d("Received beer: $beer")
                                viewModel.currentBeer = beer
                                run {
                                    initViews(beer)
                                    onContentReceived()
                                }
                            },
                            { e: Throwable ->
                                Timber.d(
                                    "Error occurred while receiving a beer with number %d, %s",
                                    beerId,
                                    e.message
                                )
                                onEmptyContent()
                            }
                        )
                )
            })
        }
    }

    private fun initFavoriteItem(beer: BeerEntity?) {
        if (beer == null) {
            return
        }
        favoriteItem?.let {
            if (beer.favorite) {
                it.setIcon(R.drawable.ic_menu_favorite_true)
            } else {
                it.setIcon(R.drawable.ic_menu_favorite_false)
            }
            it.setOnMenuItemClickListener { item ->
                onFavoriteBadgeClicked(beer, item)
                true
            }
            it.isVisible = true
        }
    }

    private fun initViews(beer: BeerEntity) {
        binding.detailsToolbar.title = beer.name
        initFavoriteItem(beer)

        if (beer.imageUrl.isNullOrEmpty()) {
            binding.detailsContent.beerDetailsImage.setImageResource(R.drawable.bottle)
        } else {
            Picasso.get()
                .load(fixImageUrl(beer.imageUrl))
                .error(R.drawable.bottle)
                .fit().centerInside()
                .into(
                    binding.detailsContent.beerDetailsImage,
                    object : Callback {
                        override fun onSuccess() {
                            binding.detailsContent.beerDetailsImage.alpha = 0f
                            binding.detailsContent.beerDetailsImage.animate().setDuration(500).alpha(1f).start()
                        }

                        override fun onError(e: Exception?) {
                            Timber.d(
                                "Error occurred while loading image of beer with number %d, %s",
                                beerId,
                                e?.message ?: "Unknown error"
                            )
                        }
                    }
                )
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
        binding.detailsContent.beerDetailsAbvValue.text = String.format("%s%%", beer.abv)
        binding.detailsContent.beerDetailsIbuValue.text = String.format("%s", beer.ibu)
        binding.detailsContent.beerDetailsTargetOgValue.text = String.format("%s", beer.targetOg)
        binding.detailsContent.beerDetailsTargetFgValue.text = String.format("%s", beer.targetFg)
        binding.detailsContent.beerDetailsEbcValue.text = String.format("%s", beer.ebc)
        binding.detailsContent.beerDetailsSrmValue.text = String.format("%s", beer.srm)
        binding.detailsContent.beerDetailsPhValue.text = String.format("%s", beer.ph)
        binding.detailsContent.beerDetailsAttenuationValue.text = String.format("%s%%", beer.attenuationLevel)

        val volume: BeerResponse.Value = gson.fromJson(
            beer.volumeJson,
            BeerResponse.Value::class.java
        )
        val boilVolume: BeerResponse.Value = gson.fromJson(
            beer.boilVolumeJson,
            BeerResponse.Value::class.java
        )
        binding.detailsContent.beerDetailsVolumeValue.text = String.format(
            "%s${if (volume.unit.equals("litres", ignoreCase = true)) "L" else ""}",
            volume.value
        )
        binding.detailsContent.beerDetailsBoilVolumeValue.text = String.format(
            "%s${if (boilVolume.unit.equals("litres", ignoreCase = true)) "L" else ""}",
            boilVolume.value
        )
    }

    private fun updateRecyclerView(beer: BeerEntity) {
        val descriptionSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_description)))
            add(TextItem(beer.description ?: EMPTY_PLACEHOLDER))
        }

        val foodPairing: List<String> = gson.fromJson(
            beer.foodPairingJson,
            object : TypeToken<List<String>>() {}.type
        )
        val foodPairingSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_food_pairing)))
            addAll(foodPairing.map {
                TextItem(
                    String.format(requireContext().getString(R.string.format_item_text_bullet), it)
                )
            })
        }

        val brewersTipsSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_brewers_tips)))
            add(TextItem(beer.brewersTips))
        }

        groupAdapter.apply {
            add(descriptionSection)
            add(foodPairingSection)
            add(brewersTipsSection)
        }
    }

    override fun onDestroyView() {
        disposables.forEach { it.dispose(); }
        super.onDestroyView()
    }

    override fun onLoading() {
        binding.detailsProgressBar.show()
        binding.detailsContent.root.hide()
        binding.detailsError.hide()
    }

    override fun onContentReceived() {
        binding.detailsContent.root.show()
        binding.detailsError.hide()
        binding.detailsProgressBar.hide()
    }

    override fun onEmptyContent() {
        binding.detailsError.show()
        binding.detailsContent.root.hide()
        binding.detailsProgressBar.hide()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(BEER_ID_KEY, beerId)
        super.onSaveInstanceState(outState)
    }

    private fun restoreSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            beerId = savedInstanceState.getInt(BEER_ID_KEY, -1)
        }
    }

    private fun onFavoriteBadgeClicked(beer: BeerEntity, menuItem: MenuItem) {
        beer.favorite = !beer.favorite
        viewModel.updateBeer(beer) {
            val mainHandler = Handler(requireContext().mainLooper)
            val runnable = Runnable {
                if (beer.favorite) {
                    menuItem.setTitle(R.string.menu_favorite_true_title)
                    menuItem.setIcon(R.drawable.ic_menu_favorite_true)
                } else {
                    menuItem.setTitle(R.string.menu_favorite_false_title)
                    menuItem.setIcon(R.drawable.ic_menu_favorite_false)
                }
                updateList(BACK_STACK_CATALOG_TAG)
                updateList(BACK_STACK_FAVORITES_TAG)
            }
            mainHandler.post(runnable)
        }
    }

    private fun updateList(tag: String) {
        val listFragment = requireFragmentManager().findFragmentByTag(tag)
        if (listFragment != null && listFragment is BeersView<*>) {
            listFragment.onBeerUpdated()
        }
    }

    private fun finish() {
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object {

        private fun fixImageUrl(imageUrl: String?): String? {
            // local image server: http://localhost:3000/images/v2/202.png
            // default image server: https://images.punkapi.com/v2/132.png
            if (imageUrl.isNullOrEmpty()) return imageUrl

            return imageUrl.replace("https://images.punkapi.com/", "http://localhost:3000/images/")
        }

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
