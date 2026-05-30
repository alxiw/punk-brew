package io.github.alxiw.punkbrew.domain.mapper

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.alxiw.punkbrew.data.remote.model.BeerResponse
import io.github.alxiw.punkbrew.domain.R
import io.github.alxiw.punkbrew.domain.utils.DateFormatter
import io.github.alxiw.punkbrew.domain.utils.Extensions.format

internal class DetailsFormatter(
    private val gson: Gson,
    private val context: Context
) {

    private val number = context.getString(R.string.number)
    private val bullet = context.getString(R.string.bullet)
    private val hollowBullet = context.getString(R.string.hollow_bullet)
    private val celsius = context.getString(R.string.celsius)
    private val degree = context.getString(R.string.degree)
    private val kilograms = context.getString(R.string.kilograms)
    private val grams = context.getString(R.string.grams)
    private val litres = context.getString(R.string.litres)

    fun getVolume(volumeJson: String): String {
        val result: BeerResponse.Value = try {
            gson.fromJson(volumeJson, BeerResponse.Value::class.java)
        } catch (e: Exception) {
            return DateFormatter.EMPTY_PLACEHOLDER
        } ?: return DateFormatter.EMPTY_PLACEHOLDER

        return "${result.value.format()}${formatVolume(result.unit)}"
    }

    fun getFoodPairing(foodPairingJson: String): List<String> {
        val result: List<String> = try {
            gson.fromJson(
                foodPairingJson,
                object : TypeToken<List<String>>() {}.type
            )
        } catch (e: Exception) {
            emptyList()
        }

        return result.map { "$bullet $it" }
    }

    fun getMethod(methodJson: String): List<String> {
        val method: BeerResponse.Method = try {
            gson.fromJson(methodJson, BeerResponse.Method::class.java)
        } catch (e: Exception) {
            return emptyList()
        } ?: return emptyList()

        val result = mutableListOf<String>()

        method.mashTemp.forEach {
            if (it.temp.value > 0 || it.duration > 0) {
                val tempStr = "${it.temp.value}${formatTemperature(it.temp.unit)}"
                result.add("Mash temp: $tempStr for ${it.duration} minutes")
            }
        }

        if (method.fermentation.temp.value > 0) {
            val tempStr = "${method.fermentation.temp.value}${formatTemperature(method.fermentation.temp.unit)}"
            result.add("Fermentation: $tempStr")
        }

        method.twist?.let {
            if (it.isNotBlank()) {
                result.add("Twist: $it")
            }
        }

        return result
    }

    fun getIngredients(ingredientsJson: String): List<String> {
        val ingredients: BeerResponse.Ingredients = try {
            gson.fromJson(ingredientsJson, BeerResponse.Ingredients::class.java)
        } catch (e: Exception) {
            return emptyList()
        } ?: return emptyList()

        val result = mutableListOf<String>()

        if (ingredients.malt.isNotEmpty()) {
            result.add("Malt:")
            ingredients.malt.forEach {
                result.add("$hollowBullet ${it.name}: ${it.amount.value.format()}${formatWeight(it.amount.unit)}")
            }
        }

        if (ingredients.hops.isNotEmpty()) {
            result.add("Hops (add, attribute):")
            ingredients.hops.forEach {
                result.add("$hollowBullet ${it.name}: ${it.amount.value.format()}${formatWeight(it.amount.unit)} (${it.add}, ${it.attribute})")
            }
        }

        if (ingredients.yeast.isNotBlank()) {
            result.add("Yeast: ${ingredients.yeast}")
        }

        return result
    }

    fun formatNumber(id: Int): String {
        return "$number$id"
    }

    fun formatNullableSimpleBeerValue(value: Double?): String {
        return value?.format() ?: DateFormatter.EMPTY_PLACEHOLDER
    }

    fun formatNullableDegreeBeerValue(value: Double?): String {
        return value?.let { "${it.format()}$degree" } ?: DateFormatter.EMPTY_PLACEHOLDER
    }

    private fun formatTemperature(unit: String): String {
        return when (unit) {
            "celsius" -> celsius
            else -> " $unit"
        }
    }

    private fun formatWeight(unit: String): String {
        return when (unit) {
            "grams" -> grams
            "kilograms" -> kilograms
            else -> " $unit"
        }
    }

    fun formatVolume(unit: String): String {
        return when (unit) {
            "litres" -> litres
            else -> " $unit"
        }
    }

    fun formatContributedBy(contributedBy: String): String {
        return context.getString(R.string.contributed_by, contributedBy)
    }
}
