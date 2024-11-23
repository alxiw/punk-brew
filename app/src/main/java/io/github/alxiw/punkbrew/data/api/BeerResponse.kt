package io.github.alxiw.punkbrew.data.api

import com.google.gson.annotations.SerializedName

/**
 * {
 *     "id": 192,
 *     "name": "Punk IPA 2007 - 2010",
 *     "tagline": "Post Modern Classic. Spiky. Tropical. Hoppy.",
 *     "first_brewed": "04/2007",
 *     "description": "Our flagship beer that kick started the craft beer revolution. This is
 *         James and Martin's original take on an American IPA, subverted with punchy New Zealand
 *         hops. Layered with new world hops to create an all-out riot of grapefruit, pineapple
 *         and lychee before a spiky, mouth-puckering bitter finish.",
 *     "image_url": "https://images.punkapi.com/v2/192.png",
 *     "abv": 6.0,
 *     "ibu": 60.0,
 *     "target_fg": 1010.0,
 *     "target_og": 1056.0,
 *     "ebc": 17.0,
 *     "srm": 8.5,
 *     "ph": 4.4,
 *     "attenuation_level": 82.14,
 *     "volume": {
 *         "value": 20,
 *         "unit": "liters"
 *      },
 *     "boil_volume": {
 *         "value": 25,
 *         "unit": "liters"
 *     },
 *     "method": {
 *         "mash_temp": [
 *             {
 *                 "temp": {
 *                     "value": 65,
 *                     "unit": "celsius"
 *                 },
 *                 "duration": 75
 *             }
 *         ],
 *         "fermentation": {
 *             "temp": {
 *                 "value": 19.0,
 *                 "unit": "celsius"
 *             }
 *         },
 *         "twist": null
 *     },
 *     "ingredients": {
 *         "malt": [
 *             {
 *                 "name": "Extra Pale",
 *                 "amount": {
 *                    "value": 5.3,
 *                    "unit": "kilograms"
 *                 }
 *             }
 *         ],
 *         "hops": [
 *             {
 *                 "name": "Ahtanum",
 *                 "amount": {
 *                     "value": 17.5,
 *                     "unit": "grams"
 *                 },
 *                 "add": "start",
 *                 "attribute": "bitter"
 *             },
 *             {
 *                 "name": "Chinook",
 *                 "amount": {
 *                     "value": 15,
 *                     "unit": "grams"
 *                 },
 *                 "add": "start",
 *                 "attribute": "bitter"
 *             },
 *             ...
 *         ],
 *         "yeast": "Wyeast 1056 - American Ale™"
 *     },
 *     "food_pairing": [
 *         "Spicy carne asada with a pico de gallo sauce",
 *         "Shredded chicken tacos with a mango chilli lime salsa",
 *         "Cheesecake with a passion fruit swirl sauce"
 *     ],
 *     "brewers_tips": "While it may surprise you, this version of Punk IPA isn't dry hopped
 *         but still packs a punch! To make the best of the aroma hops make sure they are
 *         fully submerged and add them just before knock out for an intense hop hit.",
 *     "contributed_by": "Sam Mason <samjbmason>"
 * }
 */

data class BeerResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("tagline")
    val tagline: String,
    @SerializedName("first_brewed")
    val firstBrewed: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("abv")
    val abv: Double,
    @SerializedName("ibu")
    val ibu: Double?,
    @SerializedName("target_fg")
    val targetFg: Double?,
    @SerializedName("target_og")
    val targetOg: Double?,
    @SerializedName("ebc")
    val ebc: Double?,
    @SerializedName("srm")
    val srm: Double?,
    @SerializedName("ph")
    val ph: Double?,
    @SerializedName("attenuation_level")
    val attenuationLevel: Double?,
    @SerializedName("volume")
    val volume: Value,
    @SerializedName("boil_volume")
    val boilVolume: Value,
    @SerializedName("method")
    val method: Method,
    @SerializedName("ingredients")
    val ingredients: Ingredients,
    @SerializedName("food_pairing")
    val foodPairing: List<String>,
    @SerializedName("brewers_tips")
    val brewersTips: String,
    @SerializedName("contributed_by")
    val contributedBy: String
) {
    data class Value(
        @SerializedName("value")
        val value: Double,
        @SerializedName("unit")
        val unit: String
    )

    data class Method(
        @SerializedName("mash_temp")
        val mashTemp: List<MashTemp>,
        @SerializedName("fermentation")
        val fermentation: Fermentation,
        @SerializedName("twist")
        val twist: String?
    )

    data class MashTemp(
        @SerializedName("temp")
        val temp: Temp,
        @SerializedName("duration")
        val duration: Int
    )

    data class Fermentation(
        @SerializedName("temp")
        val temp: Temp
    )

    data class Temp(
        @SerializedName("value")
        val value: Int,
        @SerializedName("unit")
        val unit: String
    )

    data class Ingredients(
        @SerializedName("malt")
        val malt: List<Malt>,
        @SerializedName("hops")
        val hops: List<Hop>,
        @SerializedName("yeast")
        val yeast: String
    )

    data class Malt(
        @SerializedName("name")
        val name: String,
        @SerializedName("amount")
        val amount: Value
    )

    data class Hop(
        @SerializedName("name")
        val name: String,
        @SerializedName("amount")
        val amount: Value,
        @SerializedName("add")
        val add: String,
        @SerializedName("attribute")
        val attribute: String
    )
}
