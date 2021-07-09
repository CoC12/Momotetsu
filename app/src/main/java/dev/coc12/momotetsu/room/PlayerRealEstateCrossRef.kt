package dev.coc12.momotetsu.room

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["code", "playerId"])
class PlayerRealEstateCrossRef(
    @field:ColumnInfo(index = true)
    var code: String,

    @field:ColumnInfo(index = true)
    var playerId: Long
)