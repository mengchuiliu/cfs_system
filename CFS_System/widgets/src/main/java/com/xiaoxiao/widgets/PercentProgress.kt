package com.xiaoxiao.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ProgressBar
import android.annotation.SuppressLint
import android.graphics.Rect


/**
 * Created by Administrator on 2017/12/18.
 */
class PercentProgress : ProgressBar {
    private val percent = 5
    private var mPaint = Paint()

    constructor(context: Context) : super(context) {
        initPaint()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initPaint()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initPaint()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rect = Rect()
        rect.top = 0
        rect.bottom = height
        for (i in 1 until percent) {
            rect.right = width * i / percent
            rect.left = rect.right - 2
            canvas.drawRect(rect, mPaint)
        }
    }

    /**
     *
     * description: 初始化画笔
     * Create by lll on 2013-8-13 下午1:41:49
     */
    private fun initPaint() {
        this.mPaint.isAntiAlias = true
        this.mPaint.color = Color.WHITE
    }
}