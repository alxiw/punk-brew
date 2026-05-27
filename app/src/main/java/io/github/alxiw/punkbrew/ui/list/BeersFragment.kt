package io.github.alxiw.punkbrew.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.databinding.FragmentBeersBinding
import io.github.alxiw.punkbrew.ui.base.BaseFragment
import io.github.alxiw.punkbrew.ui.dialog.BeerDialogFragment
import io.github.alxiw.punkbrew.util.hide
import io.github.alxiw.punkbrew.util.show

abstract class BeersFragment : BaseFragment<BeersViewModel>(), BeersView<BeersViewModel> {

    abstract override val viewModel: BeersViewModel

    protected val adapter = BeersAdapter()
    override val layoutId: Int = R.layout.fragment_beers

    protected lateinit var binding: FragmentBeersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        adapter.setOnItemClickListener(object : BeersAdapter.OnItemClickListener {
            override fun onItemClick(beer: BeerEntity) {
                onBeerClicked(beer)
            }

            override fun onItemLongClick(beer: BeerEntity) {
                onBeerLongClicked(beer)
            }

            override fun onItemFavoriteBadgeClick(beer: BeerEntity, itemView: View) {
                onFavoriteBadgeClicked(beer, itemView)
            }
        })

        return view
    }

    override fun initView(view: View) {
        binding = FragmentBeersBinding.bind(view)
        binding.beersRecyclerView.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }
    }

    override fun onDestroyView() {
        binding.beersRecyclerView.adapter = null
        super.onDestroyView()
    }

    override fun onBeerUpdated() {
        viewModel.beers.value?.dataSource?.addInvalidatedCallback {
            adapter.notifyDataSetChanged()
        }
        viewModel.beers.value?.dataSource?.invalidate()
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

    override fun onBeerLongClicked(beer: BeerEntity) {
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

        Log.d("HELLO", "Beer ${beer.id} long clicked")
    }
}
