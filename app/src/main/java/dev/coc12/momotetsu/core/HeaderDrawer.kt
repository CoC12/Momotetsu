package dev.coc12.momotetsu.core

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import dev.coc12.momotetsu.service.Constants

class HeaderDrawer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private var zoomRate = 3f
    private var height = 100 * zoomRate
    private var margin = 10 * zoomRate
    private var round = 15 * zoomRate
    private var borderVertical = 5 * zoomRate
    private var borderHorizontal = 20 * zoomRate
    private var dividerWidth = 2 * zoomRate
    private var dividerMargin = 10 * zoomRate
    private var textMargin = 10 * zoomRate

    private val borderPaint = Paint()
    private val borderRect = RectF(margin, margin, width - margin, height + margin)
    private val bgPaint = Paint()
    private val bgRect = RectF(
        margin + borderHorizontal,
        margin + borderVertical,
        width - margin - borderHorizontal,
        height + margin - borderVertical
    )
    private val dividerPaint = Paint()
    private val textPaint = Paint()

    private var playerColor = Constants.PLAYER_COLORS[0]
    private var playerName: String? = null
    private var playerMoney: Int = 0
    private var destination: String? = null
    private var distance: Int = 0
    private var year: Int = 0
    private var month: Int = 0

    override fun onDraw(canvas: Canvas) {
        borderPaint.color = ContextCompat.getColor(context, playerColor)
        borderRect.right = width - margin
        canvas.drawRoundRect(borderRect, round, round, borderPaint)

        bgPaint.color = Color.WHITE
        bgRect.right = width - margin - borderHorizontal
        canvas.drawRoundRect(bgRect, round, round, bgPaint)

        dividerPaint.strokeWidth = dividerWidth
        dividerPaint.color = Color.BLACK
        canvas.drawLine(
            margin + borderHorizontal + dividerMargin,
            height / 2 + margin,
            width - margin - borderHorizontal - dividerMargin,
            height / 2 + margin,
            dividerPaint
        )

        textPaint.textSize = height / 2 - borderVertical - 2 * textMargin
        canvas.drawText(
            "$playerName",
            margin + borderHorizontal + textMargin,
            height / 2 + margin - textMargin,
            textPaint
        )
        canvas.drawText(
            "$playerMoney 万円",
            (width / 2).toFloat(),
            height / 2 + margin - textMargin,
            textPaint
        )
        canvas.drawText(
            "$destination まで $distance ",
            margin + borderHorizontal + textMargin,
            height + margin - borderVertical - textMargin,
            textPaint
        )
        canvas.drawText(
            "$year 年目 $month 月",
            (width / 2).toFloat(),
            height + margin - borderVertical - textMargin,
            textPaint
        )
    }

    fun set(
        playerColor: Int, playerName: String, playerMoney: Int, destination: String, distance: Int,
        year: Int, month: Int
    ) {
        this.playerColor = playerColor
        this.playerName = playerName
        this.playerMoney = playerMoney
        this.destination = destination
        this.distance = distance
        this.year = year
        this.month = month
    }
}
