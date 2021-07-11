package dev.coc12.momotetsu.core.drawer

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import dev.coc12.momotetsu.R

class DrumRollDrawer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private var zoomRate = 3f

    private val bgRect = RectF()
    private val textPaint = Paint()
    private val borderPaint = Paint()
    private val bgPaint = Paint()

    private var showDialog = false
    private var listItemIndex = 0
    private var listItem: List<String> = listOf()
    private var colorId: Int = R.color.dialog_color_default

    override fun onDraw(canvas: Canvas) {
        if (!showDialog) {
            return
        }

        val text = listItem[listItemIndex]
        val textSize = 30 * zoomRate
        val strokeWidth = 10 * zoomRate
        val bgRound = 15 * zoomRate
        val bgMargin = (width / 8).toFloat()

        bgRect.set(
            bgMargin,
            height / 2 - textSize,
            width - bgMargin,
            height / 2 + textSize,
        )
        bgPaint.color = ContextCompat.getColor(context, R.color.dialog_background)
        canvas.drawRoundRect(bgRect, bgRound, bgRound, bgPaint)

        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = ContextCompat.getColor(context, colorId)
        borderPaint.strokeWidth = strokeWidth
        canvas.drawRoundRect(bgRect, bgRound, bgRound, borderPaint)

        textPaint.textSize = textSize
        canvas.drawText(
            text,
            (width - textPaint.measureText(text)) / 2,
            (height - textPaint.descent() - textPaint.ascent()) / 2,
            textPaint,
        )
    }

    /**
     * ドラムロールダイアログを表示する。
     *
     * @param listItem List<String> ドラムロールの項目
     * @param colorId Int 枠の色
     */
    fun showDialog(listItem: List<String>, colorId: Int) {
        showDialog = true
        listItemIndex = 0
        this.listItem = listItem
        this.colorId = colorId
        loopDraw()
    }

    /**
     * ドラムロールダイアログを表示する。
     *
     * @return 現在のインデックス
     */
    fun stopRoll(): Int {
        showDialog = false
        return listItemIndex
    }

    /**
     * ドラムロールダイアログを非表示にする。
     */
    fun hideDialog() {
        showDialog = false
        invalidate()
    }

    /**
     * ドラムロールダイアログの描画を更新する。
     */
    private fun loopDraw() {
        if (!showDialog) {
            return
        }

        invalidate()
        Handler(Looper.getMainLooper()).postDelayed({
            listItemIndex++
            if (listItemIndex >= listItem.size) {
                listItemIndex = 0
            }
            loopDraw()
        }, 100)
    }
}
