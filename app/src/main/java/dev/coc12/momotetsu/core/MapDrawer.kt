package dev.coc12.momotetsu.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.room.DatabaseSingleton
import dev.coc12.momotetsu.room.Station
import dev.coc12.momotetsu.service.Constants
import dev.coc12.momotetsu.service.DiagonalScrollView
import dev.coc12.momotetsu.service.Loader

class MapDrawer @JvmOverloads constructor(
    context: Context,
    private val scrollViewX: DiagonalScrollView? = null,
    private val scrollViewY: ScrollView? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    val mapManager: MapManager = MapManager(context)
    private val chipSize = 32
    private val dp = resources.displayMetrics.density
    private val mapChip = Loader(context).loadChipSet(R.drawable.chipset, chipSize)
    private val playerBmpList = Loader(context).loadImageAssets(Constants.ASSET_PLAYER_CARS)
    private val srcRect = Rect(0, 0, playerBmpList[0].width, playerBmpList[0].height)
    private val stations = getStations()

    var playerList: PlayerList? = null
    private val paint = Paint()
    private val textPaint = Paint()
    private val placeNameBgPaint = Paint()
    private val placeNameBgRect = Rect()
    private val placeNameBorderPaint = Paint()
    private val placeNameBorderRect = Rect()
    private var zoomRate = 1.5
    private var realChipSize: Int = 0
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        realChipSize = (chipSize * zoomRate * dp).toInt()
        screenWidth = widthMeasureSpec
        screenHeight = heightMeasureSpec

        setMeasuredDimension(
            mapManager.getWidth() * realChipSize,
            mapManager.getHeight() * realChipSize
        )
    }

    override fun onDraw(canvas: Canvas) {
        drawMap(canvas)
        drawPlaceName(canvas)
        drawPlayer(canvas)
    }

    /**
     * マップを描画する。
     *
     * @param canvas Canvas
     */
    private fun drawMap(canvas: Canvas) {
        for ((y, mapRow) in mapManager.withIndex()) {
            for ((x, chip) in mapRow.withIndex()) {
                canvas.drawBitmap(
                    mapChip[Integer.parseInt(chip)],
                    Rect(0, 0, chipSize, chipSize),
                    Rect(
                        x * realChipSize,
                        y * realChipSize,
                        (x + 1) * realChipSize,
                        (y + 1) * realChipSize
                    ),
                    paint
                )
            }
        }
    }

    /**
     * プレイヤーを描画する。
     *
     * @param canvas Canvas
     */
    private fun drawPlayer(canvas: Canvas) {
        for ((index, player) in playerList!!.withIndex()) {
            val playerRect = Rect(
                ((player.positionX + 0.5) * realChipSize).toInt(),
                ((player.positionY + 1.5) * realChipSize - realChipSize * 2).toInt(),
                ((player.positionX + 0.5) * realChipSize + realChipSize * 2).toInt(),
                ((player.positionY + 1.5) * realChipSize - realChipSize * 2 + realChipSize).toInt()
            )
            canvas.drawBitmap(playerBmpList[index], srcRect, playerRect, paint)
        }
    }

    /**
     * 地名を描画する。
     *
     * @param canvas Canvas
     */
    private fun drawPlaceName(canvas: Canvas) {
        val textSize = (realChipSize * 5 / 12).toFloat()
        val placeNameBorderWidth = realChipSize / 20
        textPaint.textSize = textSize


        for (station in stations) {
            val bgLeft = (station.positionX + 0.9) * realChipSize
            val bgTop = (station.positionY + 1.1) * realChipSize
            val bgRight =
                (station.positionX + 1.1) * realChipSize + textPaint.measureText(station.name)
            val bgBottom = (station.positionY + 1.3) * realChipSize + textSize

            placeNameBorderPaint.color = Color.DKGRAY
            placeNameBorderRect.set(
                bgLeft.toInt(),
                bgTop.toInt(),
                bgRight.toInt(),
                bgBottom.toInt(),
            )
            canvas.drawRect(placeNameBorderRect, placeNameBorderPaint)
            placeNameBgPaint.color = Color.WHITE
            placeNameBgRect.set(
                bgLeft.toInt() + placeNameBorderWidth,
                bgTop.toInt() + placeNameBorderWidth,
                bgRight.toInt() - placeNameBorderWidth,
                bgBottom.toInt() - placeNameBorderWidth,
            )
            canvas.drawRect(placeNameBgRect, placeNameBgPaint)
            canvas.drawText(
                station.name!!,
                ((station.positionX + 1) * realChipSize).toFloat(),
                ((station.positionY + 1.1) * realChipSize + textSize).toFloat(),
                textPaint
            )
        }
    }

    /**
     * DBから駅情報を取得する。
     *
     * return stationList List<Station>
     */
    private fun getStations(): List<Station> {
        val stationDao = DatabaseSingleton().getInstance(context).stationDao()
        var stationList: List<Station> = listOf()
        val loadDatabase = Thread {
            stationList = stationDao.getAll()
        }
        loadDatabase.start()
        loadDatabase.join()
        return stationList
    }

    /**
     * マップを指定した位置までスクロールする。
     *
     * @param x スクロール先のx座標 (マス)
     * @param y スクロール先のy座標 (マス)
     */
    fun setScroll(x: Int, y: Int) {
        if (scrollViewX == null || scrollViewY == null) {
            return
        }
        scrollViewX.scrollX = ((x + 0.5) * realChipSize).toInt() - screenWidth / 2
        scrollViewY.scrollY = ((y + 0.5) * realChipSize).toInt() - screenHeight / 2
    }

    /**
     * px単位の座標からマス単位の座標に変換する。
     *
     * @param x Int x座標 (px)
     * @param x Int y座標 (px)
     * @return Pair<Int, Int>
     */
    fun getPosition(x: Float, y: Float): Pair<Int, Int> {
        return Pair((x / realChipSize).toInt(), (y / realChipSize).toInt())
    }

    /**
     * 呼び出し元(GameManager)でsetOnTouchListenerを使うためオーバーライド。
     */
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
