package io.github.alxiw.punkbrew.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.github.alxiw.punkbrew.R
import io.github.alxiw.punkbrew.data.BeerItem

class BeerDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beer_detail)

        val intent = intent
        val beer = intent.getSerializableExtra(BEER_KEY) as BeerItem

        val mImage = findViewById<ImageView>(R.id.beer_detail_image)
        Picasso.get()
            .load(beer.image)
            .placeholder(R.drawable.bottle)
            .error(R.drawable.bottle)
            .into(mImage)

        val mName = findViewById<TextView>(R.id.beer_detail_name)
        mName.text = beer.name

        val mTagline = findViewById<TextView>(R.id.beer_detail_tagline)
        mTagline.text = beer.tagline

        val mDescription = findViewById<TextView>(R.id.beer_detail_description)
        mDescription.text = beer.description

        val mAbv = findViewById<TextView>(R.id.beer_detail_abv)
        val abvValue = "ABV: " + beer.abv.toString()
        mAbv.text = abvValue

        val mIbu = findViewById<TextView>(R.id.beer_detail_ibu)
        val ibuValue = "IBU: " + beer.ibu.toString()
        mIbu.text = ibuValue

        val mEbc = findViewById<TextView>(R.id.beer_detail_ebc)
        val ebcValue = "EBC: " + beer.ebc.toString()
        mEbc.text = ebcValue

        val mSrm = findViewById<TextView>(R.id.beer_detail_srm)
        val srmValue = "SRM: " + beer.srm.toString()
        mSrm.text = srmValue

        val mId = findViewById<TextView>(R.id.beer_detail_id)
        val idValue = "#" + beer.id.toString()
        mId.text = idValue

        val mDate = findViewById<TextView>(R.id.beer_detail_date)
        mDate.text = beer.date

        val mFavourite = findViewById<ImageView>(R.id.favourite_button)
        if (beer.isFavorite)
            mFavourite.setImageResource(R.drawable.ic_favorite_black_24dp)
        else
            mFavourite.setImageResource(R.drawable.ic_favorite_border_black_24dp)

        title = beer.name
    }

    companion object {
        private const val BEER_KEY = "BEER"
    }
}
