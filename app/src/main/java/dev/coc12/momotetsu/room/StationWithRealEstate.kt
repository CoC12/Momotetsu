package dev.coc12.momotetsu.room

import androidx.room.Embedded
import androidx.room.Relation

data class StationWithRealEstates(
    @Embedded val station: Station,
    @Relation(
        parentColumn = "code",
        entityColumn = "stationCode"
    )
    val realEstate: List<RealEstate>
)