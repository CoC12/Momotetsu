package dev.coc12.momotetsu.service

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

class DiagonalScrollView : HorizontalScrollView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        super.onInterceptTouchEvent(event)
        onTouchEvent(event)
        return false
    }
}
