package io.github.alxiw.punkbrew.ui.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

import io.github.alxiw.punkbrew.R
import android.view.MenuItem
import android.widget.Toast
import io.github.alxiw.punkbrew.PunkBrewApplication
import io.github.alxiw.punkbrew.model.Beer
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DetailsActivity : AppCompatActivity(), DetailsView {

    private lateinit var beer: Beer

    @Inject
    lateinit var detailsPresenter: DetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        (application as PunkBrewApplication).appComponent.inject(this)

        beer = intent.getSerializableExtra(BEER_KEY) as Beer

        val mImage = findViewById<ImageView>(R.id.image_detail_photo)
        Picasso.get()
                .load(beer.imageUrl)
                .placeholder(R.drawable.bottle)
                .error(R.drawable.bottle)
                .into(mImage)

        val mName = findViewById<TextView>(R.id.text_detail_name)
        mName.text = beer.name

        val mTagline = findViewById<TextView>(R.id.text_detail_tagline)
        mTagline.text = beer.tagline

        val mDescription = findViewById<TextView>(R.id.text_detail_description)
        mDescription.text = beer.description

        val mAbv = findViewById<TextView>(R.id.text_detail_abv)
        val abvValue = "ABV: " + beer.abv.toString()
        mAbv.text = abvValue

        val mIbu = findViewById<TextView>(R.id.text_detail_ibu)
        val ibuValue = "IBU: " + beer.ibu.toString()
        mIbu.text = ibuValue

        val mEbc = findViewById<TextView>(R.id.text_detail_ebc)
        val ebcValue = "EBC: " + beer.ebc.toString()
        mEbc.text = ebcValue

        val mSrm = findViewById<TextView>(R.id.text_detail_srm)
        val srmValue = "SRM: " + beer.srm.toString()
        mSrm.text = srmValue

        val mId = findViewById<TextView>(R.id.text_detail_id)
        val idValue = "#" + beer.id.toString()
        mId.text = idValue

        val mDate = findViewById<TextView>(R.id.text_detail_date)
        mDate.text = beer.firstBrewed

        val mFavourite = findViewById<ImageView>(R.id.image_detail_like)
        mFavourite.let {view ->
            view.setImageResource(getFavouriteImage(beer))
            view.setOnClickListener {
                runBlocking {
                    val response = detailsPresenter.checkIsBeerExistsInFavourites(beer)
                    if (response) {
                        detailsPresenter.deleteFavourite(beer)
                        beer.isFavourite = false
                        Toast.makeText(applicationContext, getString(R.string.dislike), Toast.LENGTH_SHORT).show()
                    } else {
                        beer.isFavourite = true
                        detailsPresenter.saveFavourite(beer)
                        Toast.makeText(applicationContext, getString(R.string.like), Toast.LENGTH_SHORT).show()
                    }
                }
                view.setImageResource(getFavouriteImage(beer))
            }
        }

        title = beer.name
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            this@DetailsActivity.overridePendingTransition(R.anim.animate_slide_in_left, R.anim.animate_slide_out_right)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this@DetailsActivity.overridePendingTransition(R.anim.animate_slide_in_left, R.anim.animate_slide_out_right)
    }

    private fun getFavouriteImage(beer: Beer) : Int {
        return if (beer.isFavourite) {
            R.drawable.ic_favorite_black_24dp
        } else {
            R.drawable.ic_favorite_border_black_24dp
        }
    }

    companion object {
        private const val BEER_KEY = "beer"
    }
}
