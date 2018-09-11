package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import bankcardformat.BandCardEditText
import com.xiaoxiao.ludan.R
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

/**
 * Created by Administrator on 2018/1/16.
 */
class AgreementAdapter(context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onItemClick: RecycleItemClickListener? = null
    private var textChange: TextChangeListener? = null
    private var shouldStopChange = false

    init {
        addItemViewDelegate(DetailsDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditVisitDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(BankDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    fun setTextChangeListener(textChange: TextChangeListener) {
        this.textChange = textChange
    }

    private inner class DetailsDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>, position: Int) {
            holder?.setText(R.id.tv_content_name, item.name)
            holder?.setText(R.id.tv_content, item.content)
            holder?.setVisible(R.id.iv_right, true)
            holder?.convertView?.isClickable = item.isEnable
            holder?.convertView?.isEnabled = item.isEnable
            holder?.convertView?.setOnClickListener {
                if (item.isEnable) {
                    onItemClick?.onItemClick(position)
                }
            }

        }
    }

    private inner class EditDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_common_edit_small

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val edittext: EditText = holder?.getView(R.id.et_content)!!
            edittext.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            holder.setText(R.id.tv_content_name, item?.name)
            holder.setText(R.id.et_content, item?.content)
            holder.setHint(R.id.et_content, item?.hintContent)
            edittext.isEnabled = item?.isEnable ?: true
            if (item?.isEnable == false) {
                holder.setTextColorRes(R.id.et_content, R.color.font_c9)
            } else {
                holder.setTextColorRes(R.id.et_content, R.color.font_c5)
            }
            if (item?.isLineShow == true) {
                edittext.inputType = InputType.TYPE_CLASS_TEXT
                edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(60))
            } else {
                if (item?.isClick == true) {
                    edittext.keyListener = DigitsKeyListener.getInstance(" 0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`¬!\"£\$%^*()~=#{}[];':,.?*-_+;@&<>")
                    edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(20))
                } else {
                    edittext.keyListener = DigitsKeyListener.getInstance("0123456789")
                    edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))
                }
            }
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    textChange?.setTextChage(position, p0?.toString()?.trim())
                }
            }
            edittext.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)
        }
    }

    private inner class BankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_bank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val edittext: BandCardEditText = holder?.getView(R.id.et_content)!!
            holder.setText(R.id.tv_content_name, item?.name)
            holder.setHint(R.id.et_content, item?.hintContent)
            holder.setText(R.id.et_content, item?.content)
            holder.setVisible(R.id.iv_scan, true)
            holder.setImageResource(R.id.iv_scan, R.mipmap.icon_scanning)
            edittext.keyListener = DigitsKeyListener.getInstance("0123456789")
            edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(28))
            holder.setOnClickListener(R.id.iv_scan, {
                onItemClick?.onItemClick(position)
            })
            edittext.setBankCardListener { textChange?.setTextChage(position, edittext.bankCardText) }
        }
    }

    private inner class EditVisitDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_common_edit_small

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            shouldStopChange = false
            val edittext: EditText = holder?.getView(R.id.et_content)!!
            edittext.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            holder.setText(R.id.tv_content_name, item?.name)
            holder.setText(R.id.et_content, item?.content)
            holder.setHint(R.id.et_content, item?.hintContent)
            holder.setVisible(R.id.tv_yuan, true)
            edittext.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(14))
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(editable: Editable) {
                    if (shouldStopChange) {
                        shouldStopChange = false
                        return
                    }
                    shouldStopChange = true
                    if (editable.toString().isNotBlank()) {
                        if (editable.toString().trim().substring(0, 1) == ".") {
                            edittext.setText("0" + edittext.text.toString().trim())
                            edittext.setSelection(2)
                            return
                        }
                    }
                    // 判断小数点后只能输入两位
                    if (editable.toString().contains(".")) {
                        if (editable.length - 1 - editable.toString().indexOf(".") > 2) {
                            val s = editable.toString().subSequence(0, editable.toString().indexOf(".") + 3)
                            edittext.setText(s)
                            edittext.setSelection(s.length)
                            return
                        }
                    }
                    //如果第一个数字为0，第二个不为点，就不允许输入
                    if (editable.toString().startsWith("0") && editable.toString().trim().length > 1) {
                        if (editable.toString().substring(1, 2) != ".") {
                            edittext.setText(editable.subSequence(0, 1))
                            edittext.setSelection(1)
                            return
                        }
                    }
                    val str = editable.toString().trim({ it <= ' ' }).replace(",".toRegex(), "")
                    textChange?.setTextChage(position, editable.toString())
                    var str1 = ""
                    val len: Int
                    if (str.contains(".")) {
                        if (str.length - str.indexOf(".") > 1) {
                            str1 = str.substring(str.indexOf("."), str.length)
                        } else {
                            str1 = "."
                        }
                        len = str.indexOf(".")
                    } else {
                        len = str.length
                    }
                    val courPos: Int
                    val builder = StringBuilder()
                    for (i in 0 until len) {
                        builder.append(str[i])
                        if (i == 2 || i == 5 || i == 8) {
                            if (i != len - 1)
                                builder.append(",")
                        }
                    }
                    builder.append(str1)
                    courPos = builder.length
                    edittext.setText(builder.toString())
                    edittext.setSelection(courPos)
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            }
            edittext.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)
        }
    }
}