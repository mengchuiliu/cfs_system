package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

class CalculatorAdapter(val context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onItemClick: RecycleItemClickListener? = null
    private var textChange: TextChangeListener? = null

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(ButtonDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(DetailsDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TextDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(DetailDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    fun setTextChangeListener(textChange: TextChangeListener) {
        this.textChange = textChange
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {

        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            if (item.isLineShow) {
                holder.setBackgroundRes(R.id.v_blank, R.color.white)
            } else {
                holder.setBackgroundRes(R.id.v_blank, R.color.transparent)
            }
        }
    }

    private inner class EditDelegate : ItemViewDelegate<CommonItem<*>> {

        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_calculator_edit

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            val editText: EditText = holder.getView(R.id.et_content)
            editText.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    textChange?.setTextChage(item.icon, p0?.toString()?.trim())
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            }
            editText.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)

            if (item.isClick) {
                editText.isFocusable = false
                editText.isFocusableInTouchMode = false
            } else {
                editText.isFocusable = true
                editText.isFocusableInTouchMode = true
            }
            when {
                item.isLineShow -> {
                    editText.inputType = InputType.TYPE_CLASS_NUMBER
                    editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
                }
                item.isClick -> {
                    editText.inputType = InputType.TYPE_NULL
                    editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
                }
                else -> {
                    editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8))
                }
            }
            editText.setOnClickListener({ if (item.isClick) onItemClick?.onItemClick(item.icon) })
            holder.setText(R.id.tv_content_name, item.name)
            holder.setText(R.id.et_content, item.content)
            holder.setHint(R.id.et_content, item.hintContent)
        }
    }

    private inner class TitleDelegate : ItemViewDelegate<CommonItem<*>> {

        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_title

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            if (item.isLineShow) {
                holder.setTextSize(R.id.tv_title, 13f)
                holder.setTextColorRes(R.id.tv_title, R.color.font_c9)
                holder.setBackgroundRes(R.id.ll_details, R.color.transparent)
            } else {
                holder.setBackgroundRes(R.id.ll_details, R.color.white)
            }
            holder.setVisible(R.id.iv_title, false)
            holder.setVisible(R.id.line, false)
            holder.setText(R.id.tv_title, item.content)
            holder.setVisible(R.id.tv_right, true)
            val textview = holder.getView<TextView>(R.id.tv_right)
            textview.setPadding(0, 0, 0, 0)
            holder.setText(R.id.tv_right, item.name)
            holder.setTextColorRes(R.id.tv_right, R.color.font_home)
            holder.setOnClickListener(R.id.tv_right, { onItemClick?.onItemClick(item.icon) })
        }
    }

    private inner class ButtonDelegate : ItemViewDelegate<CommonItem<*>> {

        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_choose_type

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            holder.convertView.setPadding(Utils.dip2px(context, 12f), 0, Utils.dip2px(context, 12f), Utils.dip2px(context, 10f))
            val textView = holder.getView<TextView>(R.id.tv_type)
            textView.setPadding(0, Utils.dip2px(context, 8f), 0, Utils.dip2px(context, 8f))
            textView.text = item.name
            textView.setBackgroundResource(R.drawable.theme_click)
            holder.setTextColorRes(R.id.tv_type, R.color.white)
            holder.convertView.setBackgroundResource(R.color.white)
            holder.convertView.setOnClickListener { onItemClick?.onItemClick(item.icon) }
        }
    }

    private inner class DetailsDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 4

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>, position: Int) {
            holder?.setText(R.id.tv_content_name, item.name)
            holder?.setText(R.id.tv_content, item.content)
            holder?.setVisible(R.id.iv_right, true)
            holder?.setBackgroundRes(R.id.v_line, R.color.font_dd)
            holder?.convertView?.setOnClickListener({ onItemClick?.onItemClick(item.icon) })
        }
    }

    private inner class TextDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 5

        override fun getItemViewLayoutId(): Int = R.layout.item_common_show

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>, position: Int) {
            holder?.setText(R.id.tv_content_name, item.name)
            holder?.setText(R.id.tv_content, item.content)
        }
    }

    private inner class DetailDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 6

        override fun getItemViewLayoutId(): Int = R.layout.item_reimburse_detail

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            holder.setText(R.id.tv_count, item.name)
            holder.setText(R.id.tv_amount, item.content)
            holder.setText(R.id.tv_interest, item.hintContent)
            holder.setText(R.id.tv_other, item.remark)
            holder.setText(R.id.tv_remain_amount, item.payType)
            holder.setVisible(R.id.tv_interest, item.isLineShow)
            if (item.isLineShow) holder.setBackgroundRes(R.id.ll_details, R.color.white) else holder.setBackgroundRes(R.id.ll_details, R.color.transparent)
        }
    }
}