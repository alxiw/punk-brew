package io.github.alxiw.punkbrew.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.github.alxiw.punkbrew.data.BeerItem
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.listeners.OnItemClickListener
import java.util.ArrayList

class BeerListAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var onItemClickListener: OnItemClickListener

    private var beers: MutableList<BeerItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BeerListViewHolder(buildView(parent, R.layout.list_beer_item))
    }

    override fun onBindViewHolder(holder:  RecyclerView.ViewHolder, position: Int) {
        bindBeerViewHolder(holder as BeerListViewHolder, position)
    }

    override fun getItemCount(): Int {
        return beers.size
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    private fun buildView(parent: ViewGroup, layoutResource: Int): View {
        val inflater = LayoutInflater.from(context)
        return inflater.inflate(layoutResource, parent, false)
    }

    private fun bindBeerViewHolder(beerViewHolder: BeerListViewHolder, position: Int) {
        val beer = beers[position]
        beerViewHolder.bind(beer)
    }

    private fun addItem(beer: BeerItem) {
        beers.add(beer)
        notifyItemInserted(beers.size - 1)
    }

    private fun getItem(position: Int): BeerItem? {
        return beers[position]
    }

    private fun removeItem(beer: BeerItem?) {
        val position = beers.indexOf(beer)
        if (position > -1) {
            beers.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addAll(beers: List<BeerItem>) {
        for (beer in beers) {
            addItem(beer)
        }
    }

    fun removeAll() {
        while (itemCount > 0) {
            removeItem(getItem(0))
        }
    }

    private inner class BeerListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var mImageView: ImageView? = null
        private var mNameView: TextView? = null
        private var mAlcoholView: TextView? = null
        private var mDateView: TextView? = null
        private var mIdView: TextView? = null

        private lateinit var favoriteIconField: ImageView

        private lateinit var beer: BeerItem

        fun bind(beer: BeerItem) {
            this.beer = beer
            fillViewHolderFields(beer)
        }

        private fun fillViewHolderFields(beer: BeerItem) {
            mNameView?.text = beer.name
            val alcoholValue = beer.abv.toString() + "%"
            mAlcoholView?.text = alcoholValue
            val dateValue = beer.date
            mDateView?.text = dateValue
            val idValue = "#" + beer.id.toString()
            mIdView?.text = idValue

            Picasso.get()
                .load(beer.image)
                .placeholder(R.drawable.bottle)
                .error(R.drawable.bottle)
                .into(mImageView)

            updateFavoriteIcon(beer)
        }

        init {
            getFieldReferences(itemView)
            setItemClick(itemView)
            setFavoriteButtonClick(itemView)
        }

        private fun getFieldReferences(itemView: View) {
            mImageView = itemView.findViewById(R.id.list_item_beer_image)
            mNameView = itemView.findViewById(R.id.list_item_beer_name)
            mAlcoholView = itemView.findViewById(R.id.list_item_beer_alcohol)
            mDateView = itemView.findViewById(R.id.list_item_beer_date)
            mIdView = itemView.findViewById(R.id.list_item_beer_id)

            favoriteIconField = itemView.findViewById(R.id.favourite_button)
        }

        private fun setItemClick(itemView: View) {
            itemView.setOnClickListener { onItemClickListener.onItemClick(beer) }
        }

        private fun setFavoriteButtonClick(itemView: View) {
            val favoriteButton = itemView.findViewById<View>(R.id.favourite_button)
            favoriteButton.setOnClickListener {
                beer.isFavorite = !beer.isFavorite
                updateFavoriteIcon(beer)
                onItemClickListener.onFavoritesButtonClick(beer)
            }
        }

        private fun updateFavoriteIcon(beer: BeerItem) {
            if (beer.isFavorite)
                favoriteIconField.setImageResource(R.drawable.ic_favorite_black_24dp)
            else
                favoriteIconField.setImageResource(R.drawable.ic_favorite_border_black_24dp)
        }
    }

}