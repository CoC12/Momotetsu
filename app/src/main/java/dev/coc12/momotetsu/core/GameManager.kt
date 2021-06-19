package dev.coc12.momotetsu.core

import android.app.Activity
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.ScrollView
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.room.DatabaseSingleton
import dev.coc12.momotetsu.room.Game
import dev.coc12.momotetsu.room.Player
import dev.coc12.momotetsu.service.Constants
import dev.coc12.momotetsu.service.DiagonalScrollView

class GameManager(
    context: Activity,
    private var containerView: RelativeLayout,
    private var diagonalScrollView: DiagonalScrollView,
    private var scrollView: ScrollView,
    private var gameId: Long? = null,
) {
    private val gameDao = DatabaseSingleton().getInstance(context).gameDao()
    private val playerDao = DatabaseSingleton().getInstance(context).playerDao()
    private val mapManager = MapManager(context)
    private val mapDrawer = MapDrawer(context, mapManager = mapManager)
    private val headerDrawer = HeaderDrawer(context)
    private val diceButton: Button = context.findViewById(R.id.dice)

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
                playerList.turnIndex = game.turnIndex
            }
        }
        getOrCreate.start()
        getOrCreate.join()

        diceButton.setOnClickListener {
            clickDice()
        }
    }

    /**
     * プレイヤーを追加する。
     */
    fun addPlayer(player: Player) {
        val addPlayer = Thread {
            player.gameId = gameId
            playerList.addPlayer(player)
            player.playerId = playerDao.updateOrCreate(player)
        }
        addPlayer.start()
        addPlayer.join()
    }

    /**
     * ゲームを開始する。
     */
    fun startGame() {
        containerView.addView(headerDrawer)
        scrollView.addView(mapDrawer)

        containerView.post {
            mapDrawer.setScroll(
                Constants.DEFAULT_POSITION_X,
                Constants.DEFAULT_POSITION_Y,
                diagonalScrollView,
                scrollView
            )
            updateHeader()
        }
    }

    /**
     * サイコロのクリックイベント処理
     */
    private fun clickDice() {
        if (playerList.changeTurn()) {
            game.months++
        }
        game.turnIndex = playerList.turnIndex
        updateHeader()
    }

    /**
     * ヘッダ表示を更新します。
     */
    private fun updateHeader() {
        headerDrawer.set(
            Constants.PLAYER_COLORS[game.turnIndex],
            playerList.getTurnPlayer().name,
            playerList.getTurnPlayer().money,
            "稚内",
            49,
            game.getYearMonth().first,
            game.getYearMonth().second
        )
        headerDrawer.invalidate()
    }
}