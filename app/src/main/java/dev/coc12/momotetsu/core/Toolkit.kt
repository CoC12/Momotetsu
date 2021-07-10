package dev.coc12.momotetsu.core

import java.text.DecimalFormat

class Toolkit {
    companion object {
        fun getFormattedPrice(price: Int): String {
            val formattedPrices = DecimalFormat("#,####").format(price).split(",")
            return when {
                formattedPrices.size == 1 -> "${formattedPrices[0]}万円"
                formattedPrices[1] == "0000" -> "${formattedPrices[0]}億円"
                formattedPrices.size == 2 -> "${formattedPrices[0]}億 ${formattedPrices[1]}万円"
                else -> "${price}万円"
            }
        }
    }
}