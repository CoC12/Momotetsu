package dev.coc12.momotetsu.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface RealEstateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateOrCreate(realEstates: List<RealEstate>): List<Long>
}