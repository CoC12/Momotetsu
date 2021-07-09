package dev.coc12.momotetsu.room

import androidx.room.*

@Dao
interface RealEstateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateOrCreate(realEstates: List<RealEstate>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateOrCreatePlayerRealEstate(playerRealEstate: List<PlayerRealEstateCrossRef>)

    @Transaction
    @Query("SELECT * FROM RealEstate WHERE RealEstate.stationCode = :stationCode")
    fun getRealEstateWithPlayersFilterByStationCode(stationCode: String): List<RealEstateWithPlayers>
}