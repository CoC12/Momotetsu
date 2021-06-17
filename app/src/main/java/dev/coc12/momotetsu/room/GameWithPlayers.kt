package dev.coc12.momotetsu.room

import androidx.room.Embedded
import androidx.room.Relation

class GameWithPlayers(
    @Embedded
    val game: Game,
    @Relation(
        parentColumn = "gameId",
        entityColumn = "gameId",
        entity = Player::class
    )
    val players: List<Player>
)
