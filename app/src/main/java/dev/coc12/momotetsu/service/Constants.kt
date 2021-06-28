package dev.coc12.momotetsu.service

import dev.coc12.momotetsu.R

class Constants {
    companion object {
        const val DATABASE_NAME = "momotetsu_db"
        const val DEFAULT_POSITION_X = 25
        const val DEFAULT_POSITION_Y = 16

        val PLAYER_COLORS: List<Int> = listOf(
            R.color.player_color_red,
            R.color.player_color_blue,
            R.color.player_color_yellow,
            R.color.player_color_green,
        )
        val ASSET_PLAYER_CARS: List<String> = listOf(
            "car_red.png",
            "car_blue.png",
            "car_yellow.png",
            "car_green.png",
        )
        val ASSET_DICES: List<String> = listOf(
            "dice1.png",
            "dice2.png",
            "dice3.png",
            "dice4.png",
            "dice5.png",
            "dice6.png",
        )
        const val SQUARE_NONE = "-1"
        const val SQUARE_SEA = "0"
        const val SQUARE_LAND = "1"
        const val SQUARE_STATION = "2"
        const val SQUARE_BLUE = "3"
        const val SQUARE_RED = "4"
        const val SQUARE_CARD = "5"
        const val SQUARE_ROAD_VERTICAL = "6"
        const val SQUARE_ROAD_HORIZONTAL = "7"
        val LAND_AND_SEA_SQUARES: List<String> = listOf(
            SQUARE_SEA,
            SQUARE_LAND,
        )
        val EFFECT_SQUARES: List<String> = listOf(
            SQUARE_STATION,
            SQUARE_BLUE,
            SQUARE_RED,
            SQUARE_CARD,
        )
        val ROAD_SQUARES: List<String> = listOf(
            SQUARE_ROAD_VERTICAL,
            SQUARE_ROAD_HORIZONTAL,
        )
        val MONEY_SQUARES: List<String> = listOf(
            SQUARE_BLUE,
            SQUARE_RED,
        )
    }
}