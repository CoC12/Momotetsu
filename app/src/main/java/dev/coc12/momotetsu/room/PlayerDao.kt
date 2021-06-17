package dev.coc12.momotetsu.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlayerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateOrCreate(player: Player)

    @Query("SELECT * FROM player WHERE gameId = :gameId")
    fun get(gameId: Long): List<Player>
}
