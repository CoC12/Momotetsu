package dev.coc12.momotetsu.core

import android.content.Context
import android.widget.ScrollView
import dev.coc12.momotetsu.room.DatabaseSingleton
import dev.coc12.momotetsu.room.Game
import dev.coc12.momotetsu.room.Player
import dev.coc12.momotetsu.service.Constants
import dev.coc12.momotetsu.service.DiagonalScrollView

class GameManager(
    context: Context,
    private var diagonalScrollView: DiagonalScrollView,
    private var scrollView: ScrollView,
    private var gameId: Long? = null,
) {
    private val gameDao = DatabaseSingleton().getInstance(context).gameDao()
    private val playerDao = DatabaseSingleton().getInstance(context).playerDao()
    private val mapManager = MapManager(context)
    private val mapDrawer = MapDrawer(context, mapManager = mapManager)

    private var game = Game()
    private var playerList = PlayerList()

    init {
        val getOrCreate = Thread {
            if (gameId == null) {
                gameId = gameDao.updateOrCreate(game)
                game.gameId = gameId as Long
            } else {
                game = gameDao.get(gameId!!)
                playerList.setPlayers(playerDao.get(gameId!!))
            }
        }
        getOrCreate.start()
        getOrCreate.join()
    }

    /**
     * プレイヤーを追加する。
     */
    fun addPlayer(player: Player) {
        val addPlayer = Thread {
            player.gameId = gameId
            playerList.addPlayer(player)
            playerDao.updateOrCreate(player)
        }
        addPlayer.start()
        addPlayer.join()
    }

    /**
     * ゲームを開始する。
     */
    fun startGame() {
        scrollView.addView(mapDrawer)

        mapDrawer.post {
            mapDrawer.setScroll(
                Constants.DEFAULT_POSITION_X,
                Constants.DEFAULT_POSITION_Y,
                diagonalScrollView,
                scrollView
            )
        }
    }
}