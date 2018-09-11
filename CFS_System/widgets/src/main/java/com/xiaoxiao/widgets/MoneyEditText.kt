package com.xiaoxiao.widgets

import android.content.Context
import android.text.*
import android.widget.EditText
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.widget.Toast

/**
 * @author MengChuiLiu
 * 金额输入框，保留两位小数
 */
class MoneyEditText(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : EditText(context, attrs, defStyleAttr) {
    private var ctx: Context? = null
    private var flag = true
    private var max: Double = 0.0 //最大输入值
    private var isFormat = false //是否格式化带,显示
    private var listener: OnTextChangeListener? = null

    fun setOnTextChangeListener(listener: OnTextChangeListener) {
        this.listener = listener
    }

    init {
        ctx = context
        setListener()
    }

    constructor(context: Context) : this(context, null) {
        ctx = context
        setListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.editTextStyle) {
        ctx = context
        setListener()
    }

    fun getValue(): Int {
        var str = text.toString()
        if (str == "")
            return -1
        if (str.contains(".")) {
            var a = 0
            for (i in str.length - 1 downTo 0) {
                val c = str[i]
                if ('.' == c) {
                    if (a == 0) {
                        str = str.substring(0, i) + "00"
                    } else if (a == 1) {
                        str = str.substring(0, i) + str.substring(i + 1) + "0"
                    } else if (a == 2) {
                        str = str.substring(0, i) + str.substring(i + 1)
                    }
                }
                a++
            }
        } else {
            str += "00"
        }
        return Integer.parseInt(str)
    }

    private fun setListener() {
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                val str = editable.toString().trim { it <= ' ' }.replace(",".toRegex(), "")
                if (listener != null) listener?.onTextChange(str)
                if (!flag) return
                filterText(str)
            }
        })
    }

    /**
     * 设置输入最大值
     * @param max 最大值
     */
    fun setMax(max: Double) {
        this.max = max
    }

    /**
     * 设置千分位输入格式
     */
    fun setIsFormat(isFormat: Boolean) {
        this.isFormat = isFormat
    }

    /**
     * 过滤输入的数据是否符合
     */
    private fun filterText(str: String) {
        flag = false
        if (str.contains(".")) {
            val i = str.indexOf(".")
            if (i == 0) {
                setText("0.")
                setSelection(2)
            } else if (str.length - i > 3) {
                val str3 = str.substring(0, i + 3)
                if (isFormat) formatText(str3)
                else {
                    setText(str3)
                    setSelection(str3.length)
                }
            } else if (max != 0.0 && str.toDouble() > max) {
                Toast.makeText(ctx, "您时输入的金额过大，请重新输入", Toast.LENGTH_SHORT).show()
                setText("")
                flag = true
                return
            } else {
                if (isFormat) formatText(str)
            }
        } else {
            if (TextUtils.isEmpty(str)) {
                flag = true
                return
            }
            if (max != 0.0 && str.toDouble() > max) {
                Toast.makeText(ctx, "您时输入的金额过大，请重新输入", Toast.LENGTH_SHORT).show()
                setText("")
                flag = true
                return
            }
            if (isFormat) formatText(str)
            else {
//                setText(str)
//                setSelection(str.length)
            }
        }
        flag = true
    }

    private fun formatText(text: String) {
        var str = text
        if (text.contains(".")) {
            str = text.substring(0, str.indexOf("."))
        }
        val len = str.length
        val courPos: Int
        val builder = StringBuilder()
        for (i in 0 until len) {
            builder.append(str[i])
            if (i % 3 == 2) {
                if (i != len - 1)
                    builder.append(",")
            }
        }
        if (text.contains(".")) {
            builder.append(text.substring(text.indexOf(".")))
        }
        courPos = builder.length
        setText(builder.toString())
        setSelection(courPos)
    }

    interface OnTextChangeListener {
        fun onTextChange(text: String)
    }
}