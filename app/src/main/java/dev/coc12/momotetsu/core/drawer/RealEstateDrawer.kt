package dev.coc12.momotetsu.core.drawer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.core.RealEstateListItem
import dev.coc12.momotetsu.core.Toolkit

class RealEstateDrawer @JvmOverloads constructor(
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
    private val itemBackgroundPaint = Paint()

    private var showDialog = false
    private var itemList: List<RealEstateListItem> = listOf()
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

        textPaint.textSize = textSize
        for ((index, item) in itemList.withIndex()) {
            itemBackgroundPaint.color = when {
                item.backgroundColor != null -> ContextCompat.getColor(
                    context,
                    item.backgroundColor
                )
                item.isSelected ->
                    ContextCompat.getColor(context, R.color.dialog_selected_background)
                else -> ContextCompat.getColor(context, R.color.dialog_background)
            }
            itemBackgroundRect.set(
                bgMargin.toInt(),
                (height / 2 + textSize * (index * 2 - itemList.size)).toInt(),
                (width - bgMargin).toInt(),
                (height / 2 + textSize * (index * 2 + 1.5 - itemList.size)).toInt(),
            )
            canvas.drawRect(itemBackgroundRect, itemBackgroundPaint)

            val coordinateY = height / 2 + textSize * (index * 2 + 1 - itemList.size)
            val price = Toolkit.getFormattedPrice(item.price)
            canvas.drawText(
                item.name,
                bgMargin + textMargin,
                coordinateY,
                textPaint
            )
            canvas.drawText(
                price,
                width * 3 / 4 - textPaint.measureText(price),
                coordinateY,
                textPaint
            )
            canvas.drawText(
                "${item.rate}%",
                width * 37 / 40 - textPaint.measureText("${item.rate}%"),
                coordinateY,
                textPaint
            )
        }

        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = ContextCompat.getColor(context, colorId)
        borderPaint.strokeWidth = strokeWidth
        canvas.drawRoundRect(bgRect, bgRound, bgRound, borderPaint)
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param itemList List<String> ??????????????????
     */
    fun showDialog(itemList: List<RealEstateListItem>) {
        showDialog = true
        this.itemList = itemList
        invalidate()
    }

    /**
     * ????????????????????????????????????????????????
     */
    fun hideDialog() {
        showDialog = false
        invalidate()
    }

    /**
     * ?????????????????????????????????????????????
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
     * ??????????????????????????????
     *
     * ???????????????
     *  * ?????????????????????
     */
    fun init(itemList: List<RealEstateListItem>) {
        this.itemList = itemList
        for (item in this.itemList) {
            item.isSelected = false
        }
        invalidate()
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @return ??????????????????????????????
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
     * ???????????????(GameManager)???setOnTouchListener???????????????????????????????????????
     */
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}