package io.github.alxiw.punkbrew.presentation.details.items

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import io.github.alxiw.punkbrew.presentation.R
import io.github.alxiw.punkbrew.presentation.databinding.ItemDetailsTextBinding

internal class TextItem(private val text: String) : BindableItem<ItemDetailsTextBinding>() {

    override fun getLayout() = R.layout.item_details_text

    override fun bind(binding: ItemDetailsTextBinding, position: Int) {
        binding.itemDetailsTextTitle.text = text
    }

    override fun initializeViewBinding(view: View) = ItemDetailsTextBinding.bind(view)
}
