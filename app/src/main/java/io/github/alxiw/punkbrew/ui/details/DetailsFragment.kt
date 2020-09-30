package io.github.alxiw.punkbrew.ui.details

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.MainActivity
import io.github.alxiw.punkbrew.ui.RefreshBeerListener
import io.github.alxiw.punkbrew.util.DateFormatter
import io.github.alxiw.punkbrew.util.hide
import io.github.alxiw.punkbrew.util.show
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_details.*
import kotlinx.android.synthetic.main.fragment_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DetailsFragment : Fragment() {

    private val viewModel: DetailsViewModel by viewModel()

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        val beerId = arguments?.getInt(BEER_ID_KEY) ?: -1
        if (beerId != -1) {
            viewModel.beer.observe(this, Observer { single ->
                single.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { beer ->
                            Timber.d("Received beer: $beer")
                            run {
                                details_toolbar.title = beer.name
                                beer_details_id.text = String.format("%s", beer.id)
                                beer_details_name.text = beer.name
                                beer_details_tagline.text = beer.tagline
                                beer_details_abv_value.text = String.format("%s%%", beer.abv)
                                beer_details_ibu_value.text = String.format("%s", beer.ibu)
                                beer_details_date.text =
                                    DateFormatter.formatDate(beer.firstBrewed, true)
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
                                    onBeerFavoriteBadgeClicked(beer, it as FloatingActionButton)
                                }
                            }
                            showContent()
                        },
                        { e: Throwable ->
                            showNetworkError(e.message ?: "Unknown error")
                        }
                    )
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BEER_ID_KEY, beerId)
    }

    private fun setupToolbar() {
        details_toolbar.also {
            (activity as AppCompatActivity).setSupportActionBar(it)
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            it.title = getString(R.string.details_label)
        }
    }

    private fun restoreSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            beerId = savedInstanceState.getInt(BEER_ID_KEY, -1)
        }
    }

    private fun showProgressBar() {
        details_progress_bar.show()
        details_fab.hide()
        details_content.hide()
    }

    private fun showContent() {
        details_fab.show()
        details_content.show()
        details_progress_bar.hide()
    }

    private fun showNetworkError(text: String) {
        Toast.makeText(activity, "⚠️ $text", Toast.LENGTH_LONG).show()
    }

    private fun onBeerFavoriteBadgeClicked(beer: BeerEntity, itemView: FloatingActionButton) {
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
                updateFragment(MainActivity.BACK_STACK_BEERS_TAG)
                updateFragment(MainActivity.BACK_STACK_FAVORITES_TAG)
            }
            mainHandler.post(runnable)
        }
    }

    private fun updateFragment(tag: String) {
        val favoritesFragment = requireFragmentManager().findFragmentByTag(tag)
        if (favoritesFragment != null && favoritesFragment is RefreshBeerListener) {
            favoritesFragment.refreshBeersAdapter()
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
