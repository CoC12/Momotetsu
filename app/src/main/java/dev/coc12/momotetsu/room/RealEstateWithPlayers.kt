package dev.coc12.momotetsu.room

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RealEstateWithPlayers(
    @Embedded
    val realEstate: RealEstate,
    @Relation(
        parentColumn = "code",
        entityColumn = "playerId",
        associateBy = Junction(PlayerRealEstateCrossRef::class),
    )
    var players: List<Player>
)