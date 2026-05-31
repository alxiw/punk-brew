package io.github.alxiw.punkbrew.presentation.list

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.androidbroadcast.vbpd.viewBinding
import io.github.alxiw.punkbrew.presentation.R
import io.github.alxiw.punkbrew.domain.model.Beer
import io.github.alxiw.punkbrew.presentation.databinding.FragmentBeersBinding
import io.github.alxiw.punkbrew.domain.loader.ImageLoader
import io.github.alxiw.punkbrew.presentation.base.BaseFragment
import io.github.alxiw.punkbrew.presentation.dialog.BeerDialogFragment
import io.github.alxiw.punkbrew.presentation.navigation.Navigator
import io.github.alxiw.punkbrew.presentation.util.hide
import io.github.alxiw.punkbrew.presentation.util.show
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeActivity
import kotlin.getValue

abstract class BeersFragment : BaseFragment<BeersViewModel>(R.layout.fragment_beers) {

    abstract override val viewModel: BeersViewModel

    private val imageLoader: ImageLoader by inject()

    protected val navigator: Navigator by lazy {
        (requireActivity() as ScopeActivity)
            .scope
            .get<Navigator>()
    }

    protected val adapter = BeersAdapter(
        imageLoader,
        onItemClick = { beer -> onBeerClicked(beer) },
        onItemLongClick = { beer -> onBeerLongClicked(beer) },
        onLikeClick = { beer, itemView -> onFavoriteBadgeClicked(beer, itemView) }
    ).apply {
        stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

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
        //adapter.currentList?.dataSource?.invalidate()
        viewModel.beers.value?.dataSource?.invalidate()
    }

    open fun onBeerClicked(beer: Beer) {
        navigator.openDetails(beer.id)
    }

    abstract fun onFavoriteBadgeClicked(beer: Beer, itemView: View)

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

    protected fun onBeerLongClicked(beer: Beer): Boolean {
        val beerDialog = BeerDialogFragment.newInstance(
            beer.id,
            beer.name,
            beer.tagline,
            beer.description,
            beer.abv,
            beer.date,
            beer.image
        )
        beerDialog.show(childFragmentManager, "fragment_beer")
        return true
    }
}
