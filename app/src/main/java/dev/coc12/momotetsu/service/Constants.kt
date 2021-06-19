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
    }
}