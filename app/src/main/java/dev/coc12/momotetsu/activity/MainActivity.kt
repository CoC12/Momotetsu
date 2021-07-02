package dev.coc12.momotetsu.activity

import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.core.GameManager
import dev.coc12.momotetsu.room.DatabaseSingleton
import dev.coc12.momotetsu.room.Player
import dev.coc12.momotetsu.room.RealEstate
import dev.coc12.momotetsu.room.Station
import dev.coc12.momotetsu.service.DiagonalScrollView
import dev.coc12.momotetsu.service.Loader

class MainActivity : AppCompatActivity() {
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO 駅情報が存在しないときにのみ読み込むようにする。
        loadStations()
        loadRealEstate()

        val containerView: RelativeLayout = findViewById(R.id.view_container)
        val diagonalScrollView: DiagonalScrollView = findViewById(R.id.diagonal_scroll_view)
        val scrollView: ScrollView = findViewById(R.id.scroll_view)
        val gameManager = GameManager(context, containerView, diagonalScrollView, scrollView)

        val player1 = Player(0, "test1")
        val player2 = Player(0, "test2")
        gameManager.addPlayer(player1)
        gameManager.addPlayer(player2)
        gameManager.startGame()
    }

    /**
     * 駅情報を読み込む。
     */
    private fun loadStations() {
        val stationList: MutableList<Station> = mutableListOf()
        val stations = Loader(context).loadJsonArrayData(R.raw.station_data)

        for (i in 0 until stations.length()) {
            val station = Station(stations.getJSONObject(i))
            stationList.add(station)
        }
        val saveDatabase = Thread {
            val stationDao = DatabaseSingleton().getInstance(context).stationDao()
            stationDao.updateOrCreate(stationList)
        }
        saveDatabase.start()
        saveDatabase.join()
    }

    /**
     * 物件情報を読み込む。
     */
    private fun loadRealEstate() {
        val realEstateList: MutableList<RealEstate> = mutableListOf()
        val realEstates = Loader(context).loadJsonArrayData(R.raw.real_estate_data)

        for (i in 0 until realEstates.length()) {
            val realEstate = RealEstate(realEstates.getJSONObject(i))
            realEstateList.add(realEstate)
        }
        val saveDatabase = Thread {
            val realEstateDao = DatabaseSingleton().getInstance(context).realEstateDao()
            realEstateDao.updateOrCreate(realEstateList)
        }
        saveDatabase.start()
        saveDatabase.join()
    }
}