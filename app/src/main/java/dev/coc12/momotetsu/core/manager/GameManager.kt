package dev.coc12.momotetsu.core.manager

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.RelativeLayout
import android.widget.ScrollView
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.core.PlayerList
import dev.coc12.momotetsu.core.RealEstateListItem
import dev.coc12.momotetsu.core.Toolkit
import dev.coc12.momotetsu.core.drawer.*
import dev.coc12.momotetsu.room.*
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
    private val stationDao = DatabaseSingleton().getInstance(context).stationDao()
    private val realEstateDao = DatabaseSingleton().getInstance(context).realEstateDao()

    // 画面描画クラス
    private val mapDrawer = MapDrawer(context, diagonalScrollView, scrollView)
    private val headerDrawer = HeaderDrawer(context)
    private val diceDrawer = DiceDrawer(context)
    private val drumRollDrawer = DrumRollDrawer(context)
    private val listDrawer = ListDrawer(context)
    private val realEstateDrawer = RealEstateDrawer(context)

    private val playerList = PlayerList()
    private val interfaceManager: InterfaceManager = InterfaceManager(context)

    private var game = Game()
    private var destination: Station
    private var moveCount: Int = 0
    private val moveStack = ArrayDeque<Pair<Int, Int>>()
    private var selectedPosX: Int = 0
    private var selectedPosY: Int = 0
    private var stopDrumrollCallback: (Int) -> Unit = {}
    private var finishButtonCallback: () -> Unit = {}
    private var purchaseButtonCallback: (List<Int>) -> Unit = {}

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

        destination = getStation(game.destinationStationCode)!!
        mapDrawer.setOnTouchListener { _, event ->
            mapDrawer.performClick()
            clickMap(event)
            false
        }
        listDrawer.setOnTouchListener { _, event ->
            listDrawer.performClick()
            listDrawer.toggleItemSelected(event.x, event.y)
            false
        }
        realEstateDrawer.setOnTouchListener { _, event ->
            realEstateDrawer.performClick()
            realEstateDrawer.toggleItemSelected(event.x, event.y)
            false
        }
        interfaceManager.setOnClickListeners(
            { clickDice() },
            { clickMove() },
            { clickBack() },
            { stopDrumroll() },
            { clickPurchase() },
            { clickFinish() },
            { clickArrow(0, -1) },
            { clickArrow(0, +1) },
            { clickArrow(+1, 0) },
            { clickArrow(-1, 0) },
        )
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
        containerView.addView(drumRollDrawer)
        containerView.addView(listDrawer)
        containerView.addView(realEstateDrawer)
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
        val turnPlayer = playerList.getTurnPlayer()
        headerDrawer.set(
            Constants.PLAYER_COLORS[game.turnIndex],
            playerList.getTurnPlayer().name,
            playerList.getTurnPlayer().money,
            destination.name!!,
            mapDrawer.mapManager.getDistance(
                Pair(turnPlayer.positionX, turnPlayer.positionY),
                Pair(destination.positionX, destination.positionY),
            ),
            game.getYearMonth().first,
            game.getYearMonth().second,
            moveCount - moveStack.size,
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
        interfaceManager.hideDefaultButtons()
        moveCount = diceDrawer.roll(1)

        Handler(Looper.getMainLooper()).postDelayed({
            diceDrawer.hideDice()
            interfaceManager.showMovementButtons()
            updateHeader()
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
        val squareInfo = mapDrawer.mapManager.getSquareInfo(selectedPosX, selectedPosY)
        if (!Constants.EFFECT_SQUARES.contains(squareInfo)) {
            return
        }

        interfaceManager.hideMovementButtons()
        movePlayer(playerList.getTurnPlayer(), selectedPosX, selectedPosY)
        Handler(Looper.getMainLooper()).postDelayed({
            changeTurn()
        }, 1000)
    }

    /**
     * 「もどる」のクリックイベント処理
     *
     * 1. 最後の移動履歴の場所へ移動
     * 2. 移動履歴の最後を削除
     */
    private fun clickBack() {
        val lastPosition = moveStack.lastOrNull() ?: return
        moveStack.removeLast()
        movePlayer(playerList.getTurnPlayer(), lastPosition.first, lastPosition.second)
    }

    /**
     * 「矢印」のクリックイベント処理
     *
     * 1. 矢印方向に駅がない場合は何もせず終了
     * 2. 最後の移動履歴と移動先が同じ場合はclickBack()をコールして終了
     * 3. 移動履歴を残す
     * 4. 矢印方向にプレイヤーを移動する
     * 5. サイコロの出目と移動履歴の大きさが等しくない場合はここで終了
     * 6. 移動用UIを非表示
     * 7. マス目の効果を発動する
     */
    private fun clickArrow(directionX: Int, directionY: Int) {
        val currentPosX = playerList.getTurnPlayer().positionX
        val currentPosY = playerList.getTurnPlayer().positionY
        val landAndSeaSquares = Constants.LAND_AND_SEA_SQUARES
        val roadSquares = Constants.ROAD_SQUARES

        for (i in 1..mapDrawer.mapManager.getMaxSize()) {
            val targetPosX = currentPosX + directionX * i
            val targetPosY = currentPosY + directionY * i
            val squareInfo = mapDrawer.mapManager.getSquareInfo(targetPosX, targetPosY)

            if (landAndSeaSquares.contains(squareInfo)) {
                return
            }
            if (roadSquares.contains(squareInfo)) {
                continue
            }
            if (moveStack.lastOrNull() == Pair(targetPosX, targetPosY)) {
                clickBack()
                return
            }
            moveStack.add(Pair(currentPosX, currentPosY))
            movePlayer(playerList.getTurnPlayer(), targetPosX, targetPosY)
            if (moveCount == moveStack.size) {
                interfaceManager.hideMovementButtons()
                activateSquareEffect()
            }
            return
        }
    }

    /**
     * マップのクリックイベント処理
     */
    private fun clickMap(event: MotionEvent) {
        if (event.action != MotionEvent.ACTION_UP) {
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
        val currentPosX = player.positionX
        val currentPosY = player.positionY
        player.positionX = posX
        player.positionY = posY
        player.direction = when {
            currentPosX == posX -> if (posY < currentPosY) 2 else 4
            currentPosY == posY -> if (posX < currentPosX) 1 else 3
            else -> mapDrawer.mapManager.getSquareDirections(posX, posY).first()
        }

        mapDrawer.invalidate()
        mapDrawer.setScroll(
            posX,
            posY,
        )
        updateHeader()
    }

    /**
     * ドラムロールを止める。
     *
     * 1. ドラムロールストップボタンを非表示
     * 2. ドラムロール停止時のコールバック関数を実行
     */
    private fun stopDrumroll() {
        interfaceManager.hideDrumrollButtons()
        stopDrumrollCallback(drumRollDrawer.stopRoll())
    }

    /**
     * 買うボタン押下時の処理。
     */
    private fun clickPurchase() {
        purchaseButtonCallback(realEstateDrawer.getSelectedItem())
    }

    /**
     * やめるボタン押下時の処理。
     */
    private fun clickFinish() {
        finishButtonCallback()
    }

    /**
     * マス目の効果を発動する。
     */
    private fun activateSquareEffect() {
        val posX = playerList.getTurnPlayer().positionX
        val posY = playerList.getTurnPlayer().positionY
        val squareInfo = mapDrawer.mapManager.getSquareInfo(posX, posY)

        // TODO カードマスに止まった場合の処理
        if (squareInfo == Constants.SQUARE_STATION) {
            activateStationEffect(posX, posY)
            return
        }
        if (Constants.MONEY_SQUARES.contains(squareInfo)) {
            val moneyList = getMoneyList(squareInfo)
            drumRollDrawer.showDialog(
                moneyList.map { Toolkit.getFormattedPrice(it) },
                when (squareInfo) {
                    Constants.SQUARE_BLUE -> {
                        R.color.dialog_color_blue
                    }
                    Constants.SQUARE_RED -> {
                        R.color.dialog_color_red

                    }
                    else -> {
                        R.color.dialog_color_blue
                    }
                }
            )
            interfaceManager.showDrumrollButtons()

            stopDrumrollCallback = { index ->
                playerList.getTurnPlayer().money += moneyList[index]
                updateHeader()
                Handler(Looper.getMainLooper()).postDelayed({
                    drumRollDrawer.hideDialog()
                    changeTurn()
                }, 2000)
            }
            return
        }
        Handler(Looper.getMainLooper()).postDelayed({
            changeTurn()
        }, 2000)
    }

    /**
     * 次のターンにする。
     *
     * 1. PlayerList::changeTurn() をコール
     * 2. PlayerListからturnIndexを取得
     * 3. 選択位置をリセットする
     * 4. サイコロの出目、移動履歴をリセット
     * 5. ヘッダー描画を更新
     * 6. 行動選択用UIを表示する
     * 7. 次のプレイヤーの位置までスクロール
     */
    private fun changeTurn() {
        if (playerList.changeTurn()) {
            game.months++
        }
        game.turnIndex = playerList.turnIndex
        selectedPosX = 0
        selectedPosY = 0
        moveCount = 0
        moveStack.clear()

        updateHeader()
        interfaceManager.showDefaultButtons()
        mapDrawer.setScroll(
            playerList.getTurnPlayer().positionX,
            playerList.getTurnPlayer().positionY,
        )
    }

    /**
     * 駅に止まったときの効果発動する
     *
     * @param posX Int 物件駅のX座標
     * @param posY Int 物件駅のY座標
     */
    private fun activateStationEffect(posX: Int, posY: Int) {
        val station = getStation(posX, posY)
        if (station == null) {
            Handler(Looper.getMainLooper()).postDelayed({
                changeTurn()
            }, 2000)
            return
        }

        val realEstates = getRealEstates(station.code!!)
        if (realEstates.isEmpty()) {
            Handler(Looper.getMainLooper()).postDelayed({
                changeTurn()
            }, 2000)
            return
        }

        realEstateDrawer.showDialog(buildRealEstateListItem(realEstates))
        interfaceManager.showRealEstateButtons()

        finishButtonCallback = {
            interfaceManager.hideRealEstateButtons()
            realEstateDrawer.hideDialog()
            Handler(Looper.getMainLooper()).postDelayed({
                changeTurn()
            }, 1000)
        }
        purchaseButtonCallback = {
            purchaseRealEstate(it.map { index ->
                realEstates[index]
            }.filter { realEstate ->
                realEstate.players.isEmpty()
            })
            realEstateDrawer.init(buildRealEstateListItem(getRealEstates(station.code!!)))
        }
        return
    }

    /**
     * 青マス・赤マスに止まったときの金額のリストを取得します。
     *
     * TODO 年目、月などを考慮したリストを作る。
     */
    private fun getMoneyList(squareInfo: String): List<Int> {
        val moneyList: MutableList<Int> = mutableListOf()
        val baseMoney = 1000

        for (i in (0..10)) {
            val money = (baseMoney * 3 + (0..baseMoney * 4).random()) / 5
            if (squareInfo == Constants.SQUARE_BLUE) {
                moneyList.add(money)
            } else if (squareInfo == Constants.SQUARE_RED) {
                moneyList.add(-money)
            }
        }
        return moneyList
    }

    /**
     * 物件購入処理
     *
     * 1. 購入対象物件の総額を取得
     * 2. 購入対象物件の総額がプレイヤーの持ち金を超えていたら終了
     * 3. 購入情報をDBへ保存
     * 4. プレイヤーの持ち金を購入物件の総額分差し引く
     * 5. ヘッダー描画を更新
     *
     * @param selectedRealEstates List<RealEstateWithPlayers> 購入対象の物件のリスト
     */
    private fun purchaseRealEstate(selectedRealEstates: List<RealEstateWithPlayers>) {
        val turnPlayer = playerList.getTurnPlayer()

        // 購入資金が足りない場合は購入処理をしない
        val totalPrice = selectedRealEstates.sumOf {
            it.realEstate.price
        }
        if (turnPlayer.money < totalPrice) {
            return
        }

        // 購入確定処理
        val purchaseRealEstate = Thread {
            realEstateDao.updateOrCreatePlayerRealEstate(selectedRealEstates.map {
                PlayerRealEstateCrossRef(
                    it.realEstate.code!!,
                    turnPlayer.playerId,
                )
            })
        }
        purchaseRealEstate.start()
        purchaseRealEstate.join()

        turnPlayer.money -= totalPrice
        updateHeader()
    }

    /**
     * 物件ダイアログの項目のリストを生成する
     *
     * @param realEstates List<RealEstateWithPlayers>
     * @return 物件ダイアログの項目リスト List<RealEstateListItem>
     */
    private fun buildRealEstateListItem(realEstates: List<RealEstateWithPlayers>): List<RealEstateListItem> {
        return realEstates.map { realEstate ->
            RealEstateListItem(
                realEstate.realEstate.name!!,
                realEstate.realEstate.price,
                realEstate.realEstate.rate,
                if (realEstate.players.isNotEmpty()) Constants.PLAYER_COLORS[playerList.getPlayerIndex(
                    realEstate.players[0]
                )] else null,
            )
        }
    }

    /**
     * 駅情報を取得する。
     *
     * @param posX Int 物件駅のX座標
     * @param posY Int 物件駅のY座標
     * @return 駅情報 Station
     */
    private fun getStation(posX: Int, posY: Int): Station? {
        var station: Station? = null
        val loadStation = Thread {
            station = stationDao.getStation(posX, posY)
        }
        loadStation.start()
        loadStation.join()
        return if (station == null) null else station!!
    }

    /**
     * 駅情報を取得する。
     *
     * @param stationCode String 物件駅コード
     * @return 駅情報 Station
     */
    private fun getStation(stationCode: String): Station? {
        var station: Station? = null
        val loadStation = Thread {
            station = stationDao.getStation(stationCode)
        }
        loadStation.start()
        loadStation.join()
        return station
    }

    /**
     * 該当物件駅の物件一覧を取得する。
     *
     * @param stationCode String 駅コード
     * @return 物件一覧 List<RealEstateWithPlayers>
     */
    private fun getRealEstates(stationCode: String): List<RealEstateWithPlayers> {
        var realEstates: List<RealEstateWithPlayers> = listOf()
        val loadRealEstate = Thread {
            realEstates = realEstateDao.getRealEstateWithPlayersFilterByStationCode(stationCode)
            realEstates.map { realEstate ->
                realEstate.players = realEstate.players.filter {
                    playerList.getPlayerIds().contains(it.playerId)
                }
            }
        }
        loadRealEstate.start()
        loadRealEstate.join()
        return realEstates
    }
}