package io.github.alxiw.punkbrew.ui.details

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.api.BeerResponse
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_CATALOG_TAG
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_FAVORITES_TAG
import io.github.alxiw.punkbrew.ui.base.BaseFragment
import io.github.alxiw.punkbrew.ui.list.BeersView
import io.github.alxiw.punkbrew.util.DateFormatter
import io.github.alxiw.punkbrew.util.hide
import io.github.alxiw.punkbrew.util.show
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_details.*
import kotlinx.android.synthetic.main.fragment_details.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DetailsFragment : BaseFragment<DetailsViewModel>() {

    override val viewModel: DetailsViewModel by viewModel()
    override val layoutId: Int = R.layout.fragment_details

    private val gson: Gson by inject()

    private var beerId = -1

    private var favoriteItem: MenuItem? = null

    private val disposables = ArrayList<Disposable>()

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
        details_toolbar.also {
            (activity as AppCompatActivity).setSupportActionBar(it)
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
        }
    }

    override fun initView() {
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
        details_toolbar.title = beer.name
        initFavoriteItem(beer)

        if (beer.imageUrl.isNullOrEmpty()) {
            beer_details_image.setImageResource(R.drawable.bottle)
        } else {
            Picasso.get()
                .load(beer.imageUrl)
                .error(R.drawable.bottle)
                .fit().centerInside()
                .into(
                    beer_details_image,
                    object : Callback {
                        override fun onSuccess() {
                            beer_details_image.alpha = 0f
                            beer_details_image.animate().setDuration(500).alpha(1f).start()
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

        beer_details_id.text = String.format("#%s", beer.id)
        beer_details_date.text = DateFormatter.formatDate(beer.firstBrewed, false)

        beer_details_abv_value.text = String.format("%s%%", beer.abv)
        beer_details_ibu_value.text = String.format("%s", beer.ibu)
        beer_details_target_og_value.text = String.format("%s", beer.targetOg)
        beer_details_target_fg_value.text = String.format("%s", beer.targetFg)
        beer_details_ebc_value.text = String.format("%s", beer.ebc)
        beer_details_srm_value.text = String.format("%s", beer.srm)
        beer_details_ph_value.text = String.format("%s", beer.ph)
        beer_details_attenuation_value.text = String.format("%s%%", beer.attenuationLevel)

        val volume: BeerResponse.Value = gson.fromJson(
            beer.volumeJson,
            BeerResponse.Value::class.java
        )
        val boilVolume: BeerResponse.Value = gson.fromJson(
            beer.boilVolumeJson,
            BeerResponse.Value::class.java
        )
        beer_details_volume_value.text = String.format(
            "%s${if (volume.unit.equals("litres", ignoreCase = true)) "L" else ""}",
            volume.value
        )
        beer_details_boil_volume_value.text = String.format(
            "%s${if (boilVolume.unit.equals("litres", ignoreCase = true)) "L" else ""}",
            boilVolume.value
        )

        beer_details_name.text = beer.name
        beer_details_tagline.text = beer.tagline

        beer_details_description.text = beer.description
        beer_details_tips.text = beer.brewersTips
        beer_details_copyright.text = String.format("Contributed by %s", beer.contributedBy)
    }

    override fun onDestroyView() {
        disposables.forEach { it.dispose(); }
        super.onDestroyView()
    }

    override fun onLoading() {
        details_progress_bar.show()
        details_content.hide()
        details_error.hide()
    }

    override fun onContentReceived() {
        details_content.show()
        details_error.hide()
        details_progress_bar.hide()
    }

    override fun onEmptyContent() {
        details_error.show()
        details_content.hide()
        details_progress_bar.hide()
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
