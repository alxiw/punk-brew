package io.github.alxiw.punkbrew.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.base.BaseFragment
import io.github.alxiw.punkbrew.util.hide
import io.github.alxiw.punkbrew.util.show
import kotlinx.android.synthetic.main.fragment_beers.*

abstract class BeersFragment : BaseFragment<BeersViewModel>(), BeersView<BeersViewModel> {

    abstract override val viewModel: BeersViewModel

    protected val adapter = BeersAdapter()
    override val layoutId: Int = R.layout.fragment_beers

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

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

    override fun initView() {
        beers_recycler_view.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }
    }

    override fun onDestroyView() {
        beers_recycler_view.adapter = null
        super.onDestroyView()
    }

    override fun onBeerUpdated() {
        viewModel.beers.value?.dataSource?.addInvalidatedCallback {
            adapter.notifyDataSetChanged()
        }
        viewModel.beers.value?.dataSource?.invalidate()
    }

    override fun onLoading() {
        beers_progress_bar.show()
        beers_recycler_view.hide()
        beers_empty_list.hide()
    }

    override fun onContentReceived() {
        beers_recycler_view.show()
        beers_progress_bar.hide()
        beers_empty_list.hide()
    }

    override fun onEmptyContent() {
        beers_empty_list.show()
        beers_recycler_view.hide()
        beers_progress_bar.hide()
    }
}
