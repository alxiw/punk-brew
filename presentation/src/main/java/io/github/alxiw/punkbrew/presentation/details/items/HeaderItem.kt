package io.github.alxiw.punkbrew.presentation.details.items

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import io.github.alxiw.punkbrew.presentation.R
import io.github.alxiw.punkbrew.presentation.databinding.ItemDetailsHeaderBinding

internal class HeaderItem(private val text: String) : BindableItem<ItemDetailsHeaderBinding>() {

    override fun getLayout() = R.layout.item_details_header

    override fun bind(binding: ItemDetailsHeaderBinding, position: Int) {
        binding.itemDetailsHeaderTitle.text = text
    }

    override fun initializeViewBinding(view: View) = ItemDetailsHeaderBinding.bind(view)
}
