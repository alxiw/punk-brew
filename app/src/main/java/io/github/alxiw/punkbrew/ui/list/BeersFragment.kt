package io.github.alxiw.punkbrew.ui.list

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import dev.androidbroadcast.vbpd.viewBinding
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.data.loader.ImageLoader
import io.github.alxiw.punkbrew.databinding.FragmentBeersBinding
import io.github.alxiw.punkbrew.ui.base.BaseFragment
import io.github.alxiw.punkbrew.ui.dialog.BeerDialogFragment
import io.github.alxiw.punkbrew.util.hide
import io.github.alxiw.punkbrew.util.show
import org.koin.android.ext.android.inject
import kotlin.getValue

abstract class BeersFragment : BaseFragment<BeersViewModel>(R.layout.fragment_beers) {

    abstract override val viewModel: BeersViewModel

    private val imageLoader: ImageLoader by inject()

    protected val adapter = BeersAdapter(
        imageLoader,
        onItemClick = { beer -> onBeerClicked(beer) },
        onItemLongClick = { beer -> onBeerLongClicked(beer) },
        onLikeClick = { beer, itemView -> onFavoriteBadgeClicked(beer, itemView) }
    )

    protected val binding by viewBinding(FragmentBeersBinding::bind)

    override fun initView(view: View) {
        binding.beersRecyclerView.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }
    }

    override fun onDestroyView() {
        binding.beersRecyclerView.adapter = null
        super.onDestroyView()
    }

    open fun onBeerUpdated() {
        viewModel.beers.value?.dataSource?.invalidate()
    }

    abstract fun onBeerClicked(beer: BeerEntity)

    abstract fun onFavoriteBadgeClicked(beer: BeerEntity, itemView: View)

    override fun onLoading() {
        binding.beersProgressBar.show()
        binding.beersRecyclerView.hide()
        binding.beersEmptyList.hide()
    }

    override fun onContentReceived() {
        binding.beersRecyclerView.show()
        binding.beersProgressBar.hide()
        binding.beersEmptyList.hide()
    }

    override fun onEmptyContent() {
        binding.beersEmptyList.show()
        binding.beersRecyclerView.hide()
        binding.beersProgressBar.hide()
    }

    protected fun onBeerLongClicked(beer: BeerEntity): Boolean {
        val fm: FragmentManager = childFragmentManager
        val beerDialog = BeerDialogFragment.newInstance(
            beer.id,
            beer.name,
            beer.tagline,
            beer.description,
            beer.abv,
            beer.firstBrewed,
            beer.image
        )
        beerDialog.show(fm, "fragment_beer")
        return true
    }
}
