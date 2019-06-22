package io.github.alxiw.punkbrew.ui.base

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.model.Beer

import java.util.ArrayList

class BaseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var onItemClickListener: OnItemClickListener

    private var beers = ArrayList<Beer>()
    private var isLoadingAdded = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == ITEM) {
            val itemView = inflater.inflate(R.layout.cell_beer, parent, false)
            BeersViewHolder(itemView)
        } else {
            val itemView = inflater.inflate(R.layout.cell_loading, parent, false)
            LoadingViewHolder(itemView)
        }
    }

    override fun getItemCount(): Int {
        return beers.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                if (holder is BeersViewHolder) {
                    holder.bind(beer = beers[position])
                }
            }
            LOADING -> {}
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == beers.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    // HELPER METHODS

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun add(beer: Beer) {
        beers.add(beer)
        notifyItemInserted(beers.size - 1)
    }

    fun addAll(beers: List<Beer>) {
        for (beer in beers) {
            add(beer)
        }
    }

    fun remove(beer: Beer?) {
        val position = beers.indexOf(beer)
        if (position > -1) {
            beers.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Beer(0,
                "",
                "",
                "",
                "",
                "",
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                "",
                ""))
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = beers.size - 1

        if (position >=  0 && getItem(position) != null) {
            beers.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): Beer? {
        return beers[position]
    }

    // VIEW HOLDERS

    private inner class BeersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mName: TextView = itemView.findViewById(R.id.text_cell_name)
        private val mId: TextView = itemView.findViewById(R.id.text_cell_id)
        private val mAbv: TextView = itemView.findViewById(R.id.text_cell_abv)
        private val mDate: TextView = itemView.findViewById(R.id.text_cell_date)
        private val mImage: ImageView = itemView.findViewById(R.id.image_cell_photo)
        private val favoriteIconField: ImageView = itemView.findViewById(R.id.image_cell_like)
        private lateinit var beer: Beer

        init {
            itemView.setOnClickListener { onItemClickListener.onItemClick(beer) }

            val favoriteButton = itemView.findViewById<View>(R.id.image_cell_like)
            favoriteButton.setOnClickListener {
                beer.isFavourite = !beer.isFavourite
                updateFavoriteIcon(beer)
                onItemClickListener.onFavoritesButtonClick(beer)
            }
        }

        fun bind(beer: Beer) {
            this.beer = beer

            mName.text = beer.name
            mId.text = String.format("#%s", beer.id)
            mAbv.text = String.format("%s%%", beer.abv)
            mDate.text = beer.firstBrewed.toString()
            Picasso.get()
                    .load(beer.imageUrl)
                    .placeholder(R.drawable.bottle)
                    .error(R.drawable.bottle)
                    .into(mImage)

            updateFavoriteIcon(beer)
        }

        private fun updateFavoriteIcon(beer: Beer) {
            if (beer.isFavourite) {
                favoriteIconField.setImageResource(R.drawable.ic_favorite_black_24dp)
            } else {
                favoriteIconField.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            }
        }
    }

    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private const val LOADING = 0
        private const val ITEM = 1
    }
}
