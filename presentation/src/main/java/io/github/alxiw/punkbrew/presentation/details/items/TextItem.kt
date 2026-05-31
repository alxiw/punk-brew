package io.github.alxiw.punkbrew.presentation.details.items

import android.widget.TextView
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import io.github.alxiw.punkbrew.presentation.R

class TextItem(private val text: String) : Item<GroupieViewHolder>() {

    override fun getLayout() = R.layout.item_details_text

    override fun bind(holder: GroupieViewHolder, position: Int) {
        val title: TextView = holder.itemView.findViewById(R.id.item_details_text_title)
        title.text = text
    }
}
