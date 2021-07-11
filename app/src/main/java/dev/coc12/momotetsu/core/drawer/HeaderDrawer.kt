package dev.coc12.momotetsu.core.drawer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import dev.coc12.momotetsu.R
import dev.coc12.momotetsu.core.Toolkit
import dev.coc12.momotetsu.service.Constants

class HeaderDrawer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private var zoomRate = 3f

    private val borderRect = RectF()
    private val bgRect = RectF()
    private val remainingMoveCountRect = RectF()
    private val borderPaint = Paint()
    private val bgPaint = Paint()
    private val dividerPaint = Paint()
    private val textPaint = Paint()

    private var playerColor = Constants.PLAYER_COLORS[0]
    private var playerName: String? = null
    private var playerMoney: Int = 0
    private var destination: String? = null
    private var distance: Int = 0
    private var year: Int = 0
    private var month: Int = 0
    private var remainingMoveCount: Int = 0

    override fun onDraw(canvas: Canvas) {
        val headerHeight = 100 * zoomRate
        val headerMargin = 10 * zoomRate
        val bgRound = 15 * zoomRate
        val remainingMoveCountRound = 5 * zoomRate
        val borderWidthVertical = 5 * zoomRate
        val borderWidthHorizontal = 20 * zoomRate
        val dividerWidth = 2 * zoomRate
        val dividerMargin = 10 * zoomRate
        val textMargin = 10 * zoomRate

        borderRect.set(
            headerMargin,
            headerMargin,
            width - headerMargin,
            headerHeight + headerMargin,
        )
        borderPaint.color = ContextCompat.getColor(context, playerColor)
        canvas.drawRoundRect(borderRect, bgRound, bgRound, borderPaint)

        bgRect.set(
            headerMargin + borderWidthHorizontal,
            headerMargin + borderWidthVertical,
            width - headerMargin - borderWidthHorizontal,
            headerHeight + headerMargin - borderWidthVertical,
        )
        bgPaint.color = ContextCompat.getColor(context, R.color.dialog_background)
        canvas.drawRoundRect(bgRect, bgRound, bgRound, bgPaint)

        dividerPaint.strokeWidth = dividerWidth
        dividerPaint.color = Color.BLACK
        canvas.drawLine(
            headerMargin + borderWidthHorizontal + dividerMargin,
            headerHeight / 2 + headerMargin,
            width - headerMargin - borderWidthHorizontal - dividerMargin,
            headerHeight / 2 + headerMargin,
            dividerPaint,
        )

        textPaint.textSize = headerHeight / 2 - borderWidthVertical - 2 * textMargin
        canvas.drawText(
            "$playerName",
            headerMargin + borderWidthHorizontal + textMargin,
            headerHeight / 2 + headerMargin - textMargin,
            textPaint,
        )
        canvas.drawText(
            Toolkit.getFormattedPrice(playerMoney),
            (width / 2).toFloat(),
            headerHeight / 2 + headerMargin - textMargin,
            textPaint,
        )
        canvas.drawText(
            "$destination まで $distance ",
            headerMargin + borderWidthHorizontal + textMargin,
            headerHeight + headerMargin - borderWidthVertical - textMargin,
            textPaint,
        )
        canvas.drawText(
            "$year 年目 $month 月",
            (width / 2).toFloat(),
            headerHeight + headerMargin - borderWidthVertical - textMargin,
            textPaint,
        )

        if (remainingMoveCount > 0) {
            remainingMoveCountRect.set(
                (width / 2).toFloat(),
                headerHeight + borderWidthVertical + textMargin,
                width - headerMargin - borderWidthHorizontal,
                headerHeight * 3 / 2 + textMargin,
            )
            canvas.drawRoundRect(
                remainingMoveCountRect,
                remainingMoveCountRound,
                remainingMoveCountRound,
                bgPaint,
            )
            canvas.drawText(
                "あと $remainingMoveCount マス",
                width / 2 + textMargin,
                headerHeight * 3 / 2,
                textPaint,
            )
        }
    }

    fun set(
        playerColor: Int, playerName: String, playerMoney: Int, destination: String, distance: Int,
        year: Int, month: Int, remainingMoveCount: Int
    ) {
        this.playerColor = playerColor
        this.playerName = playerName
        this.playerMoney = playerMoney
        this.destination = destination
        this.distance = distance
        this.year = year
        this.month = month
        this.remainingMoveCount = remainingMoveCount
    }
}
