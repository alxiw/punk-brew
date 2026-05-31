package io.github.alxiw.punkbrew.presentation.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.alxiw.punkbrew.presentation.R
import io.github.alxiw.punkbrew.domain.model.Beer
import io.github.alxiw.punkbrew.presentation.databinding.ItemBeerBinding
import io.github.alxiw.punkbrew.domain.loader.ImageLoader
import io.github.alxiw.punkbrew.presentation.util.load

class BeersAdapter(
    private val imageLoader: ImageLoader,
    private val onItemClick: (Beer) -> Unit,
    private val onItemLongClick: (Beer) -> Boolean,
    private val onLikeClick: (Beer, View) -> Unit
) : PagedListAdapter<Beer, BeersAdapter.BeersViewHolder>(BEER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeersViewHolder {
        val binding = ItemBeerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BeersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeersViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class BeersViewHolder(
        private val binding: ItemBeerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { onItemClick(it) }
                }
            }

            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { onItemLongClick(it) } ?: false
                } else {
                    false
                }
            }

            binding.itemFavorite.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { onLikeClick(it, itemView) }
                }
            }
        }

        fun bind(beer: Beer) {
            binding.itemNumber.text = beer.number
            binding.itemName.text = beer.name
            binding.itemTagline.text = beer.tagline
            binding.itemAbv.text = beer.abv
            binding.itemDate.text = beer.date

            binding.itemImage.load(imageLoader, beer.image, R.drawable.bottle)

            binding.itemFavorite.setImageResource(
                if (beer.favorite) {
                    R.drawable.badge_favorite_true
                } else {
                    R.drawable.badge_favorite_false
                }
            )
        }
    }

    companion object {
        private val BEER_COMPARATOR =
            object : DiffUtil.ItemCallback<Beer>() {
                override fun areItemsTheSame(oldItem: Beer, newItem: Beer): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Beer, newItem: Beer): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
