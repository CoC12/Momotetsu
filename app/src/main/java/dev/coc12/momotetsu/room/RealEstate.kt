package dev.coc12.momotetsu.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.json.JSONObject

@Entity(indices = [Index(value = ["code"], unique = true)])
class RealEstate() {
    @PrimaryKey(autoGenerate = true)
    var realEstateId: Long = 0
    var name: String? = null
    var code: String? = null
    var price: Int = 0
    var rate: Int = 0
    var type: Int = 0
    var stationCode: String? = null

    constructor(songJsonObject: JSONObject) : this() {
        name = songJsonObject.getString("name")
        code = songJsonObject.getString("code")
        price = songJsonObject.getInt("price")
        rate = songJsonObject.getInt("rate")
        type = songJsonObject.getInt("type")
        stationCode = songJsonObject.getString("stationCode")
    }
}