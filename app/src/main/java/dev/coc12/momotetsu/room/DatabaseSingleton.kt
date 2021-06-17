package dev.coc12.momotetsu.room

import android.content.Context
import androidx.room.Room
import dev.coc12.momotetsu.service.Constants

class DatabaseSingleton {
    private var database: Database? = null

    fun getInstance(context: Context): Database {
        if (database == null) {
            database = Room.databaseBuilder(
                context,
                Database::class.java,
                Constants.DATABASE_NAME
            ).build()
        }
        return database as Database
    }
}
