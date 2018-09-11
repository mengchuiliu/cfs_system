package com.xiaoxiao.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridView

/**
 * Created by Administrator on 2017/3/31.
 *
 * @author mengchuiliu
 * 解决gridview显示不全问题
 */

class MyGridView : GridView {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val expandSpec = View.MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}