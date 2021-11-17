package com.bearddr.calatour.offers

import android.content.Context
import org.json.JSONObject
import java.io.Serializable

data class Offer (
    val imageUrl: String,
    val city: String,
    val period: String,
    val price: Int,
    val currency: String,
    val description: String,
    var isFavorite: Boolean = false,
    var counter: Int = 0
): Serializable {

    companion object {
        fun getOffersFromFile(
            fileName: String,
            context: Context
        ) : List<Offer> {

            val offersList = ArrayList<Offer>()

            try {
                val jsonString = loadJsonFromFile(fileName, context)
                val json = JSONObject(jsonString)
                val offers = json.getJSONArray("offers")

                (0 until offers.length())
                    .mapTo(offersList) { index ->
                        Offer(
                            imageUrl = offers.getJSONObject(index).getString("image"),
                            city = offers.getJSONObject(index).getString("city"),
                            period = offers.getJSONObject(index).getString("period"),
                            price = offers.getJSONObject(index).getInt("price"),
                            currency = offers.getJSONObject(index).getString("currency"),
                            description = offers.getJSONObject(index).getString("description"),
                        )
                    }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return offersList
        }

        private fun loadJsonFromFile(
            fileName: String,
            context: Context
        ) : String {
            var json = ""
            try {
                val inputStream = context.assets.open(fileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)

                inputStream.read(buffer)
                inputStream.close()

                json = String(buffer, Charsets.UTF_8)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return json
        }

    }

}