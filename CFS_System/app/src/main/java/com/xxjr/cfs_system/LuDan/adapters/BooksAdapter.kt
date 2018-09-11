package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.xiaoxiao.ludan.R
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

class BooksAdapter(context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onItemClick: RecycleItemClickListener? = null
    private var textChange: TextChangeListener? = null

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(DetailsDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    fun setTextChangeListener(textChange: TextChangeListener) {
        this.textChange = textChange
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, t: CommonItem<*>?, position: Int) {
            holder?.setBackgroundRes(R.id.v_blank, R.color.blank_bg)
        }
    }

    private inner class TitleDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_common_title

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            holder.setImageResource(R.id.iv_title, item.icon)
            holder.setText(R.id.tv_title, item.name)
        }
    }

    private inner class DetailsDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>, position: Int) {
            holder?.setText(R.id.tv_content_name, item.name)
            holder?.setText(R.id.tv_content, item.content)
            holder?.setVisible(R.id.iv_right, item.isClick)
            holder?.setVisible(R.id.v_line, !item.isLineShow)
            if (item.isClick) {
                holder?.convertView?.isEnabled = true
                holder?.convertView?.setOnClickListener({
                    onItemClick?.onItemClick(position)
                })
            } else {
                holder?.convertView?.isEnabled = false
            }
        }
    }

    private inner class EditDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_common_edit_small

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val edittext: EditText = holder?.getView(R.id.et_content)!!
            edittext.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            holder.setText(R.id.tv_content_name, item?.name)
            holder.setText(R.id.et_content, item?.content)
            holder.setHint(R.id.et_content, item?.hintContent)
            holder.setVisible(R.id.tv_yuan, item?.isClick ?: false)
            holder.setVisible(R.id.et_line, !(item?.isLineShow ?: false))
            edittext.isEnabled = item?.isEnable ?: true
            val param = holder.convertView.layoutParams
            holder.convertView.visibility = View.VISIBLE
            param.height = ViewGroup.LayoutParams.WRAP_CONTENT
            param.width = ViewGroup.LayoutParams.MATCH_PARENT
            if (item?.isClick!!) {
                if (position == 5 && item.position != 3) {
                    holder.convertView.visibility = View.GONE
                    param.height = 0
                    param.width = 0
                }
                edittext.keyListener = DigitsKeyListener.getInstance("0123456789")
                edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(7))
            } else {
                edittext.inputType = InputType.TYPE_CLASS_TEXT
                edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
            }
            holder.convertView.layoutParams = param
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

}