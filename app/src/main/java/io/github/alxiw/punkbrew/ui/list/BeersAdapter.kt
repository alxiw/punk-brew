package io.github.alxiw.punkbrew.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.local.db.model.BeerEntity
import io.github.alxiw.punkbrew.data.loader.ImageLoader
import io.github.alxiw.punkbrew.databinding.ItemBeerBinding
import io.github.alxiw.punkbrew.util.DateFormatter
import io.github.alxiw.punkbrew.util.load

class BeersAdapter(
    private val imageLoader: ImageLoader
) : PagedListAdapter<BeerEntity, BeersAdapter.BeersViewHolder>(BEER_COMPARATOR) {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeersViewHolder {
        val binding = ItemBeerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BeersViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(holder: BeersViewHolder, position: Int) {
        getItem(position)?.let { beer ->
            holder.bind(beer, onItemClickListener)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    class BeersViewHolder(
        private val binding: ItemBeerBinding,
        private val imageLoader: ImageLoader
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(beer : BeerEntity, listener: OnItemClickListener?) {
            binding.itemId.text = String.format("#%s", beer.id)
            binding.itemName.text = beer.name
            binding.itemTagline.text = beer.tagline
            binding.itemAbv.text = String.format("%s%%", beer.abv)
            binding.itemDate.text = DateFormatter.formatDate(beer.firstBrewed, true)

            binding.itemImage.load(imageLoader, beer.image, R.drawable.bottle)

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
