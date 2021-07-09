package dev.coc12.momotetsu.core.drawer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.core.RealEstateListItem

class RealEstateDrawer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private var zoomRate = 3f
    private val textSize = 20 * zoomRate
    private val bgRect = RectF()
    private var bgRound = 15 * zoomRate
    private val itemSelectedRect = Rect()

    private val textPaint = Paint()
    private val borderPaint = Paint()
    private val bgPaint = Paint()
    private val itemBackgroundPaint = Paint()

    private var showDialog = false
    private var itemList: List<RealEstateListItem> = listOf()
    private var colorId: Int = R.color.dialog_color_gray

    override fun onDraw(canvas: Canvas) {
        if (!showDialog) {
            return
        }

        bgPaint.color = Color.WHITE
        bgRect.set(
            (width / 20).toFloat(),
            (height / 2 - textSize * (itemList.size + 0.5)).toFloat(),
            (width * 19 / 20).toFloat(),
            height / 2 + textSize * itemList.size,
        )
        canvas.drawRoundRect(bgRect, bgRound, bgRound, bgPaint)

        textPaint.textSize = textSize
        for ((index, item) in itemList.withIndex()) {
            itemBackgroundPaint.color = when {
                item.backgroundColor != null -> ContextCompat.getColor(
                    context,
                    item.backgroundColor
                )
                item.isSelected -> Color.CYAN
                else -> Color.WHITE
            }
            itemSelectedRect.set(
                width * 1 / 20,
                (height / 2 - textSize * (itemList.size - index * 2)).toInt(),
                width * 19 / 20,
                (height / 2 - textSize * (itemList.size - index * 2 - 1.5)).toInt(),
            )
            canvas.drawRect(itemSelectedRect, itemBackgroundPaint)

            canvas.drawText(
                item.name,
                (width * 3 / 40).toFloat(),
                (height) / 2 - (itemList.size - index * 2 - 1) * textSize,
                textPaint
            )
            canvas.drawText(
                "${item.price} 万円",
                (width * 4 / 7).toFloat(),
                (height) / 2 - (itemList.size - index * 2 - 1) * textSize,
                textPaint
            )
            canvas.drawText(
                "${item.rate}%",
                width * 37 / 40 - textPaint.measureText("${item.rate}%"),
                (height) / 2 - (itemList.size - index * 2 - 1) * textSize,
                textPaint
            )
        }

        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = ContextCompat.getColor(context, colorId)
        borderPaint.strokeWidth = textSize / 3
        canvas.drawRoundRect(bgRect, bgRound, bgRound, borderPaint)
    }

    /**
     * リストダイアログを表示する。
     *
     * @param itemList List<String> リストの項目
     */
    fun showDialog(itemList: List<RealEstateListItem>) {
        showDialog = true
        this.itemList = itemList
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
    fun init(itemList: List<RealEstateListItem>) {
        this.itemList = itemList
        for (item in this.itemList) {
            item.isSelected = false
        }
        invalidate()
    }

    /**
     * 選択された項目のインデックスのリスト。
     *
     * @return インデックスのリスト
     */
    fun getSelectedItem(): List<Int> {
        val indexList = mutableListOf<Int>()
        for ((index, item) in itemList.withIndex()) {
            if (item.isSelected) {
                indexList.add(index)
            }
        }
        return indexList
    }

    /**
     * 呼び出し元(GameManager)でsetOnTouchListenerを使うためオーバーライド。
     */
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}