package io.github.alxiw.punkbrew.presentation.list

import android.view.View
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import dev.androidbroadcast.vbpd.viewBinding
import io.github.alxiw.punkbrew.presentation.R
import io.github.alxiw.punkbrew.domain.model.Beer
import io.github.alxiw.punkbrew.presentation.databinding.FragmentBeersBinding
import io.github.alxiw.punkbrew.domain.loader.ImageLoader
import io.github.alxiw.punkbrew.presentation.base.BaseFragment
import io.github.alxiw.punkbrew.presentation.base.UiEvent
import io.github.alxiw.punkbrew.presentation.dialog.BeerDialogFragment
import io.github.alxiw.punkbrew.presentation.navigation.Navigator
import io.github.alxiw.punkbrew.presentation.util.hide
import io.github.alxiw.punkbrew.presentation.util.show
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeActivity
import kotlin.getValue
import kotlin.math.abs

abstract class BeersFragment : BaseFragment<BeersViewModel>(R.layout.fragment_beers) {

    abstract override val viewModel: BeersViewModel

    protected val binding by viewBinding(FragmentBeersBinding::bind)

    private val imageLoader: ImageLoader by inject()

    protected val navigator: Navigator by lazy {
        (requireActivity() as ScopeActivity)
            .scope
            .get<Navigator>()
    }

    protected val adapter = BeersAdapter(
        imageLoader,
        onItemClick = { beer -> onBeerClick(beer) },
        onItemLongClick = { beer -> onBeerLongClick(beer) },
        onLikeClick = { beer -> onFavoriteBadgeClick(beer) }
    ).apply {
        stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private val appBarOffsetListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        val totalScrollRange = appBarLayout.totalScrollRange
        if (totalScrollRange == 0) return@OnOffsetChangedListener
        val offset = abs(verticalOffset).toFloat()
        val percentage = offset / totalScrollRange

        val showSubtitle = percentage <= 0.1f
        binding.toolbar.subtitle = if (showSubtitle) getString(R.string.app_tagline) else ""

        val contentAlpha = when {
            percentage < 0.2f -> 1f
            percentage > 0.8f -> 0f
            else -> 1f - (percentage - 0.2f) / 0.6f
        }
        binding.toolbarContainer.alpha = contentAlpha
    }

    override fun initView(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.beersAppBarLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.beersRecyclerView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        binding.beersRecyclerView.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }

        binding.beersAppBarLayout.addOnOffsetChangedListener(appBarOffsetListener)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is UiEvent.FavoriteToggled -> onFavoriteToggled(event.id, event.favorite)
                        is UiEvent.Error -> showNetworkError(event.message)
                    }
                }
            }
        }
    }

    open fun onBeerUpdated() {
        //adapter.currentList?.dataSource?.invalidate()
        viewModel.beers.value?.dataSource?.invalidate()
    }

    open fun onFavoriteToggled(id: Int, favorite: Boolean) {
        onBeerUpdated()
    }

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

    protected open fun onBeerClick(beer: Beer) {
        navigator.openDetails(beer.id)
    }

    protected fun onFavoriteBadgeClick(beer: Beer) {
        viewModel.toggleFavorite(beer)
    }

    protected fun onBeerLongClick(beer: Beer): Boolean {
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

    private fun showNetworkError(text: String?) {
        val message = getString(R.string.format_error, text)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        with(binding) {
            beersRecyclerView.adapter = null
            beersAppBarLayout.removeOnOffsetChangedListener(appBarOffsetListener)
        }
        super.onDestroyView()
    }
}
