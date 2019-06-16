package io.github.alxiw.punkbrew.providers

import android.content.Context
import android.net.ConnectivityManager
import io.github.alxiw.punkbrew.data.BeerItem
import io.github.alxiw.punkbrew.listeners.LoadListListener
import io.github.alxiw.punkbrew.models.DatabaseModel
import io.github.alxiw.punkbrew.models.WebModel

class BeerProvider(val context: Context, private val loadListener: LoadListListener) {

    private var databaseModel: DatabaseModel = DatabaseModel(context, loadListener)
    private var webModel: WebModel = WebModel(loadListener, databaseModel)

    private val isOnline: Boolean
        get() {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }

    suspend fun getBeers(currentQuery: String?, currentPage: Int, pageSize: Int) {
        if (isOnline) {
            webModel.getBeers(currentQuery, currentPage, pageSize)
        } else {
            databaseModel.getBeers(currentQuery, currentPage, pageSize)
        }
    }

    suspend fun getFavouriteBeers() {
        webModel.getFavouriteBeers()
    }

    fun saveBeer(beer: BeerItem) {
        databaseModel.saveBeer(beer.id.toInt())
    }

    fun contains(beer: BeerItem): Boolean {
        return databaseModel.containsBeer(beer.id.toInt()) != null
    }

    fun removeBeer(beer: BeerItem) {
        databaseModel.removeBeer(beer.id.toInt())
    }

    fun close() {
        databaseModel.closeDatabase()
    }

}