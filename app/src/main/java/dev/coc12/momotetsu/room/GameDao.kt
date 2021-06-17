package dev.coc12.momotetsu.room

import androidx.room.*

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateOrCreate(game: Game): Long

    @Query("SELECT * FROM game WHERE gameId = :gameId")
    fun get(gameId: Long): Game
}
