package io.github.alxiw.punkbrew.ui.details.items

import android.widget.TextView
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.github.alxiw.punkbrew.R

class TextItem(private val text: String) : Item() {

    override fun getLayout() = R.layout.item_details_text

    override fun bind(holder: GroupieViewHolder, position: Int) {
        val title: TextView = holder.containerView.findViewById(R.id.item_details_text_title)
        title.text = text
    }
}
