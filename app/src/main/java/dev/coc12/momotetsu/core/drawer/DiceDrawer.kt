package dev.coc12.momotetsu.core.drawer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import dev.coc12.momotetsu.service.Constants
import dev.coc12.momotetsu.service.Loader

class DiceDrawer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private val diceBmpList = Loader(context).loadImageAssets(Constants.ASSET_DICES)
    private var diceList: List<Int> = listOf()
    private val srcRect = Rect(0, 0, diceBmpList[0].width, diceBmpList[0].height)
    private val diceRect = RectF()
    private val paint = Paint()

    override fun onDraw(canvas: Canvas) {
        if (diceList.isEmpty()) {
            return
        }

        val diceWidth = (width / 8).toFloat()
        val diceMargin = (width / 50).toFloat()
        for ((indexY, chunkedDices) in diceList.chunked(4).withIndex()) {
            for ((indexX, dice) in chunkedDices.withIndex()) {
                diceRect.set(
                    width / 2 + diceWidth * (indexX - chunkedDices.size / 2),
                    height / 2 + diceWidth * (indexY - 1),
                    width / 2 + diceWidth * (indexX + 1 - chunkedDices.size / 2) - diceMargin,
                    height / 2 + diceWidth * indexY - diceMargin,
                )
                canvas.drawBitmap(diceBmpList[dice - 1], srcRect, diceRect, paint)
            }
        }
    }

    /**
     * 指定した個数のサイコロを振り、出目の合計を返す。
     *
     * @param diceCount Int サイコロの個数
     * @return 出目の合計
     */
    fun roll(diceCount: Int): Int {
        diceList = (1..diceCount).map {
            (1..6).random()
        }
        invalidate()
        return diceList.sum()
    }

    /**
     * サイコロを非表示にする
     */
    fun hideDice() {
        diceList = listOf()
        invalidate()
    }
}