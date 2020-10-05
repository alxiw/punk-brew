package io.github.alxiw.punkbrew.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.util.DateFormatter.formatDate
import kotlinx.android.synthetic.main.item_beer.view.*

class BeersAdapter : PagedListAdapter<BeerEntity, BeersAdapter.BeersViewHolder>(BEER_COMPARATOR) {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_beer,
            parent,
            false
        )
        return BeersViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeersViewHolder, position: Int) {
        getItem(position)?.let { beer ->
            holder.bind(beer)
            val itemView = holder.itemView
            itemView.setOnClickListener {
                onItemClickListener?.onItemClick(beer)
            }
            itemView.item_favorite.setOnClickListener {
                onItemClickListener?.onItemFavoriteBadgeClick(beer, itemView)
            }
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    class BeersViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bind(beer : BeerEntity) {
            itemView.item_id.text = String.format("%s", beer.id)
            itemView.item_name.text = beer.name
            itemView.item_tagline.text = beer.tagline
            itemView.item_abv.text = String.format("%s%%", beer.abv)
            itemView.item_date.text = formatDate(beer.firstBrewed, true)

            Picasso.get()
                .load(beer.imageUrl)
                .placeholder(R.drawable.bottle)
                .error(R.drawable.bottle)
                .fit().centerInside()
                .into(itemView.item_image)

            itemView.item_favorite.setImageResource(
                if (beer.favorite) {
                    R.drawable.badge_favorite_true
                } else {
                    R.drawable.badge_favorite_false
                }
            )
        }
    }

    interface OnItemClickListener {
        fun onItemClick(beer: BeerEntity)

        fun onItemFavoriteBadgeClick(beer: BeerEntity, itemView: View)
    }

    companion object {
        private val BEER_COMPARATOR =
            object : DiffUtil.ItemCallback<BeerEntity>() {
                override fun areItemsTheSame(oldItem: BeerEntity, newItem: BeerEntity): Boolean {
                    return oldItem == newItem && oldItem.favorite == newItem.favorite
                }

                override fun areContentsTheSame(oldItem: BeerEntity, newItem: BeerEntity): Boolean {
                    return oldItem.name == newItem.name && oldItem.favorite == newItem.favorite
                }
            }
    }
}
