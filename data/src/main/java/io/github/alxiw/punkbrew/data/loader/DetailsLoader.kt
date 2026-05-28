package io.github.alxiw.punkbrew.data.loader

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.alxiw.punkbrew.data.remote.api.model.BeerResponse

class DetailsLoader(private val gson: Gson) {
    fun getVolume(volume: String): Pair<Double, String> {
        val result: BeerResponse.Value = gson.fromJson(
            volume,
            BeerResponse.Value::class.java
        )

        return result.value to result.unit
    }

    fun getFoodPairing(foodPairingJson: String): List<String> {
        val result: List<String> = gson.fromJson(
            foodPairingJson,
            object : TypeToken<List<String>>() {}.type
        )

        return result
    }
}