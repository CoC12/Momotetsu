package dev.coc12.momotetsu.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateOrCreate(stations: List<Station>): List<Long>

    @Query("SELECT * FROM station")
    fun getAll(): List<Station>
}
