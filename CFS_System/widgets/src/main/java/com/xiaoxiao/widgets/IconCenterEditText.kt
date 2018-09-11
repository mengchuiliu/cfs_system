package com.xiaoxiao.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class IconCenterEditText(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : EditText(context, attrs, defStyleAttr),
        View.OnFocusChangeListener, View.OnKeyListener {
    /**
     * 是否是默认图标再左边的样式
     */
    private var isLeft = false
    /**
     * 是否点击软键盘搜索
     */
    private var pressSearch = false
    /**
     * 软键盘搜索键监听
     */
    private var listener: OnSearchClickListener? = null

    fun setOnSearchClickListener(listener: OnSearchClickListener) {
        this.listener = listener
    }

    constructor(context: Context) : this(context, null) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.editTextStyle) {
        init()
    }

    init {
        init()
    }

    private fun init() {
        onFocusChangeListener = this
        setOnKeyListener(this)
    }

    override fun onDraw(canvas: Canvas) {
        if (isLeft) { // 如果是默认样式，则直接绘制
            super.onDraw(canvas)
        } else { // 如果不是默认样式，需要将图标绘制在中间
            val drawables = compoundDrawables
            val drawableLeft = drawables[0]
            if (drawableLeft != null) {
                val textWidth = paint.measureText(hint.toString())
                val drawablePadding = compoundDrawablePadding
                val drawableWidth = drawableLeft.intrinsicWidth
                val bodyWidth = textWidth + drawableWidth.toFloat() + drawablePadding.toFloat()
                canvas.translate((width.toFloat() - bodyWidth - paddingLeft.toFloat() - paddingRight.toFloat()) / 2, 0f)
            }
            super.onDraw(canvas)
        }
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        Log.d(TAG, "onFocusChange execute")
        // 恢复EditText默认的样式
        if (!pressSearch && TextUtils.isEmpty(text.toString())) {
            isLeft = hasFocus
        }
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        pressSearch = keyCode == KeyEvent.KEYCODE_ENTER
        if (pressSearch && listener != null) {
            /*隐藏软键盘*/
            val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
            }
            listener!!.onSearchClick(v)
        }
        return false
    }

    interface OnSearchClickListener {
        fun onSearchClick(view: View)
    }

    fun setDefaultLeft(isLeft: Boolean) {
        this.isLeft = isLeft
    }

    companion object {
        private val TAG = IconCenterEditText::class.java.simpleName
    }
}
