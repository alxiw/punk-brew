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

class BeersAdapter : PagedListAdapter<BeerEntity, BeersAdapter.BeersViewHolder>(BEER_COMPARATOR) {

    private var onItemClickListener: OnItemClickListener? = null

    private lateinit var binding: ItemBeerBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_beer, parent, false)
        binding = ItemBeerBinding.bind(view)
        return BeersViewHolder(view, binding)
    }

    override fun onBindViewHolder(holder: BeersViewHolder, position: Int) {
        getItem(position)?.let { beer ->
            holder.bind(beer)
            val itemView = holder.itemView
            itemView.setOnClickListener {
                onItemClickListener?.onItemClick(beer)
            }
            binding.itemFavorite.setOnClickListener {
                onItemClickListener?.onItemFavoriteBadgeClick(beer, itemView)
            }
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    class BeersViewHolder(itemView : View, private val binding: ItemBeerBinding) : RecyclerView.ViewHolder(itemView) {

        fun bind(beer : BeerEntity) {
            binding.itemId.text = String.format("#%s", beer.id)
            binding.itemName.text = beer.name
            binding.itemTagline.text = beer.tagline
            binding.itemAbv.text = String.format("%s%%", beer.abv)
            binding.itemDate.text = formatDate(beer.firstBrewed, true)

            Picasso.get()
                .load(fixImageUrl(beer.imageUrl))
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
        }
    }

    interface OnItemClickListener {
        fun onItemClick(beer: BeerEntity)

        fun onItemFavoriteBadgeClick(beer: BeerEntity, itemView: View)
    }

    companion object {
        private fun fixImageUrl(imageUrl: String?): String? {
            // local image server: http://localhost:3000/images/v2/202.png
            // default image server: https://images.punkapi.com/v2/132.png
            if (imageUrl.isNullOrEmpty()) return imageUrl

            return imageUrl.replace("https://images.punkapi.com/", "http://localhost:3000/images/")
        }

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
