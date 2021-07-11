package dev.coc12.momotetsu.core.drawer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.core.ListItem

class ListDrawer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private var zoomRate = 3f
    private var textSize = 20 * zoomRate

    private val bgRect = RectF()
    private val itemBackgroundRect = Rect()
    private val textPaint = Paint()
    private val borderPaint = Paint()
    private val bgPaint = Paint()
    private val itemSelectedPaint = Paint()

    private var showDialog = false
    private var itemList: List<ListItem> = listOf()
    private var colorId: Int = R.color.dialog_color_default

    override fun onDraw(canvas: Canvas) {
        if (!showDialog) {
            return
        }

        textSize = 20 * zoomRate
        val strokeWidth = 5 * zoomRate
        val bgRound = 15 * zoomRate
        val bgMargin = (width / 20).toFloat()
        val textMargin = (width / 40).toFloat()

        bgRect.set(
            bgMargin,
            (height / 2 - textSize * (itemList.size + 0.5)).toFloat(),
            width - bgMargin,
            height / 2 + textSize * itemList.size,
        )
        bgPaint.color = ContextCompat.getColor(context, R.color.dialog_background)
        canvas.drawRoundRect(bgRect, bgRound, bgRound, bgPaint)

        itemSelectedPaint.color =
            ContextCompat.getColor(context, R.color.dialog_selected_background)
        textPaint.textSize = textSize
        for ((index, item) in itemList.withIndex()) {
            if (item.isSelected) {
                itemBackgroundRect.set(
                    bgMargin.toInt(),
                    (height / 2 + textSize * (index * 2 - itemList.size)).toInt(),
                    (width - bgMargin).toInt(),
                    (height / 2 + textSize * (index * 2 + 1.5 - itemList.size)).toInt(),
                )
                canvas.drawRect(itemBackgroundRect, itemSelectedPaint)
            }
            canvas.drawText(
                item.name,
                bgMargin + textMargin,
                height / 2 + textSize * (index * 2 + 1 - itemList.size),
                textPaint
            )
        }

        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = ContextCompat.getColor(context, colorId)
        borderPaint.strokeWidth = strokeWidth
        canvas.drawRoundRect(bgRect, bgRound, bgRound, borderPaint)
    }

    /**
     * リストダイアログを表示する。
     *
     * @param itemList List<String> リストの項目
     * @param colorId Int 枠の色
     */
    fun showDialog(itemList: List<ListItem>, colorId: Int) {
        showDialog = true
        this.itemList = itemList
        this.colorId = colorId
        invalidate()
    }

    /**
     * リストダイアログを非表示にする。
     */
    fun hideDialog() {
        showDialog = false
        invalidate()
    }

    /**
     * リストの項目を選択状態にする。
     */
    fun toggleItemSelected(posX: Float, posY: Float) {
        if (!showDialog) {
            return
        }
        if (posX < width / 20 || posX > width * 19 / 20) {
            return
        }

        val selectedIndex =
            ((posY - height / 2 + textSize * itemList.size) / (2 * textSize)).toInt()
        if (selectedIndex < 0 || selectedIndex > itemList.size - 1) {
            return
        }
        itemList[selectedIndex].toggleIsSelected()
        invalidate()
    }

    /**
     * リストを初期化する。
     *
     * 初期化内容
     *  * 項目の選択状態
     */
    fun init() {
        for (item in itemList) {
            item.isSelected = false
        }
        invalidate()
    }

    /**
     * 呼び出し元(GameManager)でsetOnTouchListenerを使うためオーバーライド。
     */
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}