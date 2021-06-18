package dev.coc12.momotetsu.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game(
    @PrimaryKey(autoGenerate = true)
    var gameId: Long = 0,
    var months: Int = 1,
    var turnIndex: Int = 0,
)
