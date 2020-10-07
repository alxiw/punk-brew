package io.github.alxiw.punkbrew.ui.details

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_CATALOG_TAG
import io.github.alxiw.punkbrew.ui.MainActivity.Companion.BACK_STACK_FAVORITES_TAG
import io.github.alxiw.punkbrew.ui.base.BaseFragment
import io.github.alxiw.punkbrew.ui.list.BeersView
import io.github.alxiw.punkbrew.util.DateFormatter
import io.github.alxiw.punkbrew.util.hide
import io.github.alxiw.punkbrew.util.show
import io.reactivex.android.schedulers.AndroidSchedulers
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            beerId = it.getInt(BEER_ID_KEY)
        }

        restoreSavedInstanceState(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.findBeer(beerId)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BEER_ID_KEY, beerId)
    }

    override fun setupToolbar() {
        details_toolbar.also {
            (activity as AppCompatActivity).setSupportActionBar(it)
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            it.title = getString(R.string.details_label)
        }
    }

    override fun initView() {
        val beerId = arguments?.getInt(BEER_ID_KEY) ?: -1
        if (beerId != -1) {
            viewModel.beer.observe(this, Observer { single ->
                single.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { beer ->
                            Timber.d("Received beer: $beer")
                            run {
                                initViews(beer)
                                onContentReceived()
                            }
                        },
                        { e: Throwable ->
                            Timber.d("Error occurred while receiving a beer with number %d, %s", beerId, e.message)
                            onEmptyContent()
                        }
                    )
            })
        }
    }

    private fun initViews(beer: BeerEntity) {
        details_toolbar.title = beer.name
        beer_details_id.text = String.format("%s", beer.id)
        beer_details_name.text = beer.name
        beer_details_tagline.text = beer.tagline
        beer_details_abv_value.text = String.format("%s%%", beer.abv)
        beer_details_ibu_value.text = String.format("%s", beer.ibu)
        beer_details_date.text =
            DateFormatter.formatDate(beer.firstBrewed, false)
        beer_details_description.text = beer.description

        Picasso.get()
            .load(beer.imageUrl)
            .placeholder(R.drawable.bottle)
            .error(R.drawable.bottle)
            .fit().centerInside()
            .into(beer_details_image)

        details_fab.setImageResource(
            if (beer.favorite) {
                R.drawable.badge_favorite_true
            } else {
                R.drawable.badge_favorite_false_white
            }
        )
        details_fab.setOnClickListener {
            onFavoriteBadgeClicked(beer, it as FloatingActionButton)
        }
    }

    override fun onLoading() {
        details_progress_bar.show()
        details_fab.hide()
        details_content.hide()
        details_error.hide()
    }

    override fun onContentReceived() {
        details_fab.show()
        details_content.show()
        details_error.hide()
        details_progress_bar.hide()
    }

    override fun onEmptyContent() {
        details_error.show()
        details_fab.hide()
        details_content.hide()
        details_progress_bar.hide()
    }

    private fun restoreSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            beerId = savedInstanceState.getInt(BEER_ID_KEY, -1)
        }
    }

    private fun onFavoriteBadgeClicked(beer: BeerEntity, itemView: FloatingActionButton) {
        beer.favorite = !beer.favorite
        viewModel.updateBeer(beer) {
            val mainHandler = Handler(requireContext().mainLooper)
            val runnable = Runnable {
                itemView.setImageResource(
                    if (beer.favorite) {
                        R.drawable.badge_favorite_true
                    } else {
                        R.drawable.badge_favorite_false_white
                    }
                )
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
