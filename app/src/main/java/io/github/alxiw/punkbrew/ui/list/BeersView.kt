package io.github.alxiw.punkbrew.ui.list

import android.view.View
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.ui.base.BaseView
import io.github.alxiw.punkbrew.ui.base.BaseViewModel

interface BeersView<VM : BeersViewModel> : BaseView<BaseViewModel> {

    fun onBeerClicked(beer: BeerEntity)

    fun onFavoriteBadgeClicked(beer: BeerEntity, itemView: View)

    fun onBeerUpdated()
}
