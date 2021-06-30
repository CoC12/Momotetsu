package dev.coc12.momotetsu.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.json.JSONObject

@Entity(indices = [Index(value = ["code"], unique = true)])
class Station() {
    @PrimaryKey(autoGenerate = true)
    var stationId: Long = 0
    var name: String? = null
    var code: String? = null
    var positionX: Int = 0
    var positionY: Int = 0

    constructor(songJsonObject: JSONObject) : this() {
        name = songJsonObject.getString("name")
        code = songJsonObject.getString("code")
        positionX = songJsonObject.getInt("positionX")
        positionY = songJsonObject.getInt("positionY")
    }
}
