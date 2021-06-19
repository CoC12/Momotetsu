package dev.coc12.momotetsu.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game(
    @PrimaryKey(autoGenerate = true)
    var gameId: Long = 0,
    var months: Int = 0,
    var turnIndex: Int = 0,
) {

    /**
     * 年目と月を返す。
     *
     * @return Pair<Int, Int> first:年 second:月
     */
    fun getYearMonth(): Pair<Int, Int> {
        val year = this.months / 12 + 1
        var month = this.months % 12 + 4
        if (month > 12) {
            month -= 12
        }
        return Pair(year, month)
    }
}
