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
import io.github.alxiw.punkbrew.databinding.ItemBeerBinding
import io.github.alxiw.punkbrew.util.DateFormatter.formatDate
import io.github.alxiw.punkbrew.util.makeImageUrl

class BeersAdapter : PagedListAdapter<BeerEntity, BeersAdapter.BeersViewHolder>(BEER_COMPARATOR) {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeersViewHolder {
        val binding = ItemBeerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BeersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeersViewHolder, position: Int) {
        getItem(position)?.let { beer ->
            holder.bind(beer, onItemClickListener)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    class BeersViewHolder(private val binding: ItemBeerBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(beer : BeerEntity, listener: OnItemClickListener?) {
            binding.itemId.text = String.format("#%s", beer.id)
            binding.itemName.text = beer.name
            binding.itemTagline.text = beer.tagline
            binding.itemAbv.text = String.format("%s%%", beer.abv)
            binding.itemDate.text = formatDate(beer.firstBrewed, true)

            Picasso.get()
                .load(makeImageUrl(beer.image))
                .placeholder(R.drawable.bottle)
                .error(R.drawable.bottle)
                .fit().centerInside()
                .into(binding.itemImage)

            binding.itemFavorite.setImageResource(
                if (beer.favorite) {
                    R.drawable.badge_favorite_true
                } else {
                    R.drawable.badge_favorite_false
                }
            )

            itemView.setOnClickListener {
                listener?.onItemClick(beer)
            }

            itemView.setOnLongClickListener {
                listener?.onItemLongClick(beer)
                true
            }

            binding.itemFavorite.setOnClickListener {
                listener?.onItemFavoriteBadgeClick(beer, itemView)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(beer: BeerEntity)

        fun onItemLongClick(beer: BeerEntity)

        fun onItemFavoriteBadgeClick(beer: BeerEntity, itemView: View)
    }

    companion object {

        private val BEER_COMPARATOR =
            object : DiffUtil.ItemCallback<BeerEntity>() {
                override fun areItemsTheSame(oldItem: BeerEntity, newItem: BeerEntity): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: BeerEntity, newItem: BeerEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
