package dev.coc12.momotetsu.core

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
    private val srcRect = Rect(0, 0, diceBmpList[0].width, diceBmpList[0].height)

    private val paint = Paint()
    private val diceRect = RectF()
    private var diceList: MutableList<Int> = mutableListOf()

    override fun onDraw(canvas: Canvas) {
        if (diceList.isEmpty()) {
            return
        }
        diceRect.set(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (width / 2 + width / 10).toFloat(),
            (height / 2 + width / 10).toFloat(),
        )
        for (num in diceList) {
            // TODO 複数サイコロの描画位置調整
            canvas.drawBitmap(diceBmpList[num - 1], srcRect, diceRect, paint)
        }
    }

    /**
     * 指定した個数のサイコロを振り、出目の合計を返す。
     *
     * @param diceCount Int サイコロの個数
     * @return 出目の合計
     */
    fun roll(diceCount: Int): Int {
        diceList = mutableListOf()
        for (i in (1..diceCount)) {
            diceList.add((1..6).random())
        }
        invalidate()
        return diceList.sum()
    }
}