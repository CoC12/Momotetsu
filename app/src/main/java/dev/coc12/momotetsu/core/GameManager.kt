package dev.coc12.momotetsu.core

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
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
    private val containerView: RelativeLayout,
    diagonalScrollView: DiagonalScrollView,
    private val scrollView: ScrollView,
    private var gameId: Long? = null,
) {
    // データアクセスオブジェクト
    private val gameDao = DatabaseSingleton().getInstance(context).gameDao()
    private val playerDao = DatabaseSingleton().getInstance(context).playerDao()

    // 画面描画クラス
    private val mapDrawer = MapDrawer(context, diagonalScrollView, scrollView)
    private val headerDrawer = HeaderDrawer(context)
    private val diceDrawer = DiceDrawer(context)

    // フッターボタン
    private val diceButton: Button = context.findViewById(R.id.dice)
    private val moveButton: Button = context.findViewById(R.id.move)

    private val playerList = PlayerList()

    private var game = Game()
    private var moveCount: Int = 0
    private var selectedPosX: Int = 0
    private var selectedPosY: Int = 0

    init {
        // ゲームの取得もしくは新規作成
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

        mapDrawer.setOnTouchListener { _, event ->
            mapDrawer.performClick()
            clickMap(event)
            true
        }
        diceButton.setOnClickListener {
            clickDice()
        }
        moveButton.setOnClickListener {
            clickMove()
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
        mapDrawer.playerList = playerList
        containerView.addView(headerDrawer)
        containerView.addView(diceDrawer)
        scrollView.addView(mapDrawer)

        containerView.post {
            mapDrawer.setScroll(
                Constants.DEFAULT_POSITION_X,
                Constants.DEFAULT_POSITION_Y,
            )
            updateHeader()
        }
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

    /**
     * 「さいころ」のクリックイベント処理
     *
     * 1. サイコロをふる
     * 2. 1s後にサイコロ描画をリセットする
     * 3. 移動用UIを表示する
     */
    private fun clickDice() {
        diceButton.visibility = View.GONE
        moveCount = diceDrawer.roll(1)

        Handler(Looper.getMainLooper()).postDelayed({
            diceDrawer.roll(0)
            moveButton.visibility = View.VISIBLE
        }, 1000)
    }

    /**
     * 「移動」のクリックイベント処理
     * TODO 移動可能な場所にのみ移動する。
     *
     * 1. 移動用UIを非表示
     * 2. プレイヤーの移動
     * 3. 1s後に次のターンに変更
     */
    private fun clickMove() {
        moveButton.visibility = View.GONE
        movePlayer(playerList.getTurnPlayer(), selectedPosX, selectedPosY)
        Handler(Looper.getMainLooper()).postDelayed({
            changeTurn()
        }, 1000)
    }

    /**
     * マップのクリックイベント処理
     */
    private fun clickMap(event: MotionEvent?) {
        if (event == null || event.action != MotionEvent.ACTION_UP) {
            return
        }

        val position = mapDrawer.getPosition(event.x, event.y)
        selectedPosX = position.first
        selectedPosY = position.second
    }

    /**
     * プレイヤーの位置を変更する処理
     *
     * @param player Player 移動するプレイヤー
     * @param posX Int 移動後のX座標
     * @param posY Int 移動後のY座標
     */
    private fun movePlayer(player: Player, posX: Int, posY: Int) {
        player.positionX = posX
        player.positionY = posY

        mapDrawer.invalidate()
        mapDrawer.setScroll(
            posX,
            posY,
        )
    }

    /**
     * 次のターンにする。
     *
     * 1. PlayerList::changeTurn() をコール
     * 2. PlayerListからturnIndexを取得
     * 3. 選択位置をリセットする
     * 4. ヘッダー描画を更新
     * 5. 行動選択用UIを表示する
     * 6. 次のプレイヤーの位置までスクロール
     */
    private fun changeTurn() {
        if (playerList.changeTurn()) {
            game.months++
        }
        game.turnIndex = playerList.turnIndex
        selectedPosX = 0
        selectedPosY = 0

        updateHeader()
        diceButton.visibility = View.VISIBLE
        mapDrawer.setScroll(
            playerList.getTurnPlayer().positionX,
            playerList.getTurnPlayer().positionY,
        )
    }
}