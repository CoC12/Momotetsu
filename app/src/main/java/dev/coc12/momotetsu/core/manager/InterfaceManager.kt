package dev.coc12.momotetsu.core.manager

import android.app.Activity
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import dev.coc12.momotetsu.R

class InterfaceManager(context: Activity) {
    private val diceButton: Button = context.findViewById(R.id.dice)
    private val backButton: Button = context.findViewById(R.id.back)
    private val moveButton: Button = context.findViewById(R.id.move)
    private val stopDrumrollButton: Button = context.findViewById(R.id.stop_drumroll)
    private val purchaseButton: Button = context.findViewById(R.id.purchase)
    private val finishButton: Button = context.findViewById(R.id.finish)
    private val dPad: RelativeLayout = context.findViewById(R.id.d_pad)
    private val arrowUpward: Button = context.findViewById(R.id.arrow_upward)
    private val arrowDownward: Button = context.findViewById(R.id.arrow_downward)
    private val arrowRight: Button = context.findViewById(R.id.arrow_right)
    private val arrowLeft: Button = context.findViewById(R.id.arrow_left)

    /**
     * 初期状態時に表示するボタン
     */
    private val defaultButtons = listOf(
        diceButton,
    )

    fun showDefaultButtons() {
        updateButtonsView(defaultButtons, View.VISIBLE)
    }

    fun hideDefaultButtons() {
        updateButtonsView(defaultButtons, View.GONE)
    }

    /**
     * 移動用ボタン
     */
    private val movementButtons = listOf(
        moveButton,
        backButton,
        dPad,
    )

    fun showMovementButtons() {
        updateButtonsView(movementButtons, View.VISIBLE)
    }

    fun hideMovementButtons() {
        updateButtonsView(movementButtons, View.GONE)
    }

    /**
     * ドラムロール用ボタン
     */
    private val drumrollButtons = listOf(
        stopDrumrollButton,
    )

    fun showDrumrollButtons() {
        updateButtonsView(drumrollButtons, View.VISIBLE)
    }

    fun hideDrumrollButtons() {
        updateButtonsView(drumrollButtons, View.GONE)
    }

    /**
     * 物件購入用ボタン
     */
    private val realEstateButtons = listOf(
        purchaseButton,
        finishButton,
    )

    fun showRealEstateButtons() {
        updateButtonsView(realEstateButtons, View.VISIBLE)
    }

    fun hideRealEstateButtons() {
        updateButtonsView(realEstateButtons, View.GONE)
    }

    /**
     * ボタンの表示。非表示を更新する
     */
    private fun updateButtonsView(buttons: List<View>, visibility: Int) {
        buttons.forEach {
            it.visibility = visibility
        }
    }

    fun setOnClickListeners(
        diceButtonClickListener: View.OnClickListener,
        moveButtonClickListener: View.OnClickListener,
        backButtonClickListener: View.OnClickListener,
        stopDrumrollButtonClickListener: View.OnClickListener,
        purchaseButtonClickListener: View.OnClickListener,
        finishButtonClickListener: View.OnClickListener,
        arrowUpwardButtonClickListener: View.OnClickListener,
        arrowDownwardButtonClickListener: View.OnClickListener,
        arrowRightButtonClickListener: View.OnClickListener,
        arrowLeftButtonClickListener: View.OnClickListener,
    ) {
        // さいころボタン
        diceButton.setOnClickListener(diceButtonClickListener)
        // 移動ボタン
        moveButton.setOnClickListener(moveButtonClickListener)
        // もどるボタン
        backButton.setOnClickListener(backButtonClickListener)
        // ストップボタン
        stopDrumrollButton.setOnClickListener(stopDrumrollButtonClickListener)
        // 買うボタン
        purchaseButton.setOnClickListener(purchaseButtonClickListener)
        // やめるボタン
        finishButton.setOnClickListener(finishButtonClickListener)
        // 矢印ボタン
        arrowUpward.setOnClickListener(arrowUpwardButtonClickListener)
        arrowDownward.setOnClickListener(arrowDownwardButtonClickListener)
        arrowRight.setOnClickListener(arrowRightButtonClickListener)
        arrowLeft.setOnClickListener(arrowLeftButtonClickListener)
    }
}
