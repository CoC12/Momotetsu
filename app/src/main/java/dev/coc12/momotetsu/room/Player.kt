package dev.coc12.momotetsu.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.coc12.momotetsu.service.Constants

@Entity
data class Player(
    @PrimaryKey(autoGenerate = true)
    var playerId: Long = 0,
    var name: String,
    var positionX: Int = Constants.DEFAULT_POSITION_X,
    var positionY: Int = Constants.DEFAULT_POSITION_Y,
    var direction: Int = 1,
    var money: Int = 1000,
    var gameId: Long? = null,
)
