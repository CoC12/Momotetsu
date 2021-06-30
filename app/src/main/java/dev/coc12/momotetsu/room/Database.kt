package dev.coc12.momotetsu.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Game::class, Player::class, Station::class],
    version = 2,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun playerDao(): PlayerDao
    abstract fun stationDao(): StationDao
}
