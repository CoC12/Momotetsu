package dev.coc12.momotetsu.core

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.service.DiagonalScrollView
import dev.coc12.momotetsu.service.Loader

class MapDrawer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val mapManager: MapManager? = null,
) : View(context, attrs, defStyleAttr) {
    private val chipSize = 32
    private val dp = resources.displayMetrics.density
    private val mapChip = Loader().loadChipSet(resources, R.drawable.chipset, chipSize)

    private var paint: Paint = Paint()
    private var zoomRate = 1.5
    private var realChipSize: Int = 0
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        realChipSize = (chipSize * zoomRate * dp).toInt()
        screenWidth = widthMeasureSpec
        screenHeight = heightMeasureSpec

        if (mapManager == null) {
            return
        }
        setMeasuredDimension(
            mapManager.getWidth() * realChipSize,
            mapManager.getHeight() * realChipSize
        )
    }

    override fun onDraw(canvas: Canvas) {
        drawMap(canvas)
    }

    /**
     * マップを描画する。
     *
     * @param canvas Canvas
     */
    private fun drawMap(canvas: Canvas) {
        if (mapManager == null) {
            return
        }
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
     * マップを指定した位置までスクロールする。
     *
     * @param x スクロール先のx座標
     * @param y スクロール先のy座標
     * @param scrollViewX X方向のScrollView
     * @param scrollViewY Y方向のScrollView
     */
    fun setScroll(x: Int, y: Int, scrollViewX: DiagonalScrollView, scrollViewY: ScrollView) {
        scrollViewX.scrollX = ((x + 0.5) * realChipSize).toInt() - screenWidth / 2
        scrollViewY.scrollY = ((y + 0.5) * realChipSize).toInt() - screenHeight / 2
    }
}
