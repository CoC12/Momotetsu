package dev.coc12.momotetsu.room

import androidx.room.*

@Dao
interface StationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateOrCreate(stations: List<Station>): List<Long>

    @Transaction
    @Query("SELECT * FROM station")
    fun getAll(): List<Station>

    @Transaction
    @Query("SELECT * FROM station WHERE code = :stationCode")
    fun getStation(stationCode: String): Station

    @Transaction
    @Query("SELECT * FROM station WHERE positionX = :poX and positionY = :poY")
    fun getStation(poX: Int, poY: Int): Station
}
