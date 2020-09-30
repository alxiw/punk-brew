package io.github.alxiw.punkbrew.ui.favorites

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.MainActivity
import io.github.alxiw.punkbrew.ui.RefreshBeerListener
import io.github.alxiw.punkbrew.ui.beers.BeersAdapter
import io.github.alxiw.punkbrew.ui.details.DetailsFragment
import io.github.alxiw.punkbrew.util.hide
import io.github.alxiw.punkbrew.util.show
import kotlinx.android.synthetic.main.fragment_beers.*
import kotlinx.android.synthetic.main.item_beer.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class FavoritesFragment : Fragment(), RefreshBeerListener {

    private val viewModel: FavoritesViewModel by viewModel()

    private val adapter = BeersAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beers, container, false)

        adapter.setOnItemClickListener(object : BeersAdapter.OnItemClickListener {
            override fun onItemClick(beer: BeerEntity) {
                onBeerClicked(beer)
            }

            override fun onItemFavoriteBadgeClick(beer: BeerEntity, itemView: View) {
                onFavoriteBadgeClicked(beer, itemView)
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        beers_recycler_view.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }

        viewModel.favorites.observe(this, Observer {
            Timber.d("Received list of favorites with size of: ${it.size}")
            if (it.size > 0) {
                showRecyclerView()
            } else {
                showEmptyList()
            }
            adapter.submitList(it)
        })
    }

    override fun onDestroyView() {
        beers_recycler_view.adapter = null
        super.onDestroyView()
    }

    private fun setupToolbar() {
        beers_toolbar.also {
            (activity as AppCompatActivity).setSupportActionBar(it)
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
            it.title = getString(R.string.favorites_label)
        }
    }

    private fun showProgressBar() {
        beers_progress_bar.show()
        beers_recycler_view.hide()
        beers_empty_list.hide()
    }

    private fun showEmptyList() {
        beers_empty_list.show()
        beers_recycler_view.hide()
        beers_progress_bar.hide()
    }

    private fun showRecyclerView() {
        beers_recycler_view.show()
        beers_progress_bar.hide()
        beers_empty_list.hide()
    }

    private fun onBeerClicked(beer: BeerEntity) {
        requireFragmentManager().beginTransaction()
            .replace(R.id.root_container, DetailsFragment.newInstance(beer.id))
            .addToBackStack(null)
            .commit()
    }

    private fun updateFragment(tag: String) {
        requireFragmentManager().findFragmentByTag(tag)?.let {
            if (it is RefreshBeerListener) {
                it.refreshBeersAdapter()
            }
        }
    }

    private fun onFavoriteBadgeClicked(beer: BeerEntity, itemView: View) {
        beer.favorite = !beer.favorite
        viewModel.updateBeer(beer) {
            val mainHandler = Handler(requireContext().mainLooper)
            val runnable = Runnable {
                if (!beer.favorite) {
                    itemView.item_favorite.setImageResource(R.drawable.badge_favorite_false)
                    refreshBeersAdapter()
                    updateFragment(MainActivity.BACK_STACK_BEERS_TAG)
                }
            }
            mainHandler.post(runnable)
        }
    }

    override fun refreshBeersAdapter() {
        viewModel.favorites.value?.dataSource?.addInvalidatedCallback {
            adapter.notifyDataSetChanged()
        }
        viewModel.favorites.value?.dataSource?.invalidate()
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
