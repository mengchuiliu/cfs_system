package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

class ItemCommonAdapter(val context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onItemClick: RecycleItemClickListener? = null
    private var textChangeListener: TextChangeListener? = null
    private var noteChangeListener: TextChangeListener? = null

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TextDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditNoteDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    fun setTextChangeListener(textChange: TextChangeListener) {
        this.textChangeListener = textChange
    }

    fun setNoteChangeListener(noteChange: TextChangeListener) {
        this.noteChangeListener = noteChange
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>, position: Int): Boolean = item.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            holder.setBackgroundRes(R.id.v_blank, item.icon)
            val lp = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(context, item.position.toFloat()))
            if (item.isLineShow) lp.setMargins(Utils.dip2px(context, 12f), 0, 0, 0)
            holder.convertView?.layoutParams = lp
        }
    }

    private inner class TitleDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            holder.setText(R.id.tv_content_name, item.name)
            holder.setText(R.id.tv_content, item.content)
            holder.setVisible(R.id.iv_right, item.isClick)

            holder.convertView.setOnClickListener {
                if (item.isClick) onItemClick?.onItemClick(position)
            }
        }
    }

    private inner class TextDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>, position: Int): Boolean = item.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_show

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            val param = holder.convertView.layoutParams
            if (!item.isClick) {
                holder.convertView.visibility = View.VISIBLE
                param.height = ViewGroup.LayoutParams.WRAP_CONTENT
                param.width = ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                holder.convertView.visibility = View.GONE
                param.height = 0
                param.width = 0
            }
            holder.convertView.layoutParams = param
            holder.setText(R.id.tv_content_name, item.name)
            holder.setText(R.id.tv_content, item.content)
            if (item.icon != 0) holder.setTextColorRes(R.id.tv_content, item.icon)
            holder.getView<TextView>(R.id.tv_content)?.setSingleLine(false)
        }
    }

    private inner class EditDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>, position: Int): Boolean = item.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_common_edit_small

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            val editText: EditText = holder.getView(R.id.et_content)
            editText.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            holder.setText(R.id.tv_content_name, item.name)
            holder.setText(R.id.et_content, item.content)
            holder.setHint(R.id.et_content, item.hintContent)
            if (item.isClick) {
//                editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL//输入带小数数字
                editText.inputType = InputType.TYPE_CLASS_NUMBER //输入数字
                editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))
            } else {
                editText.inputType = InputType.TYPE_CLASS_TEXT
                editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
            }
            val textWatcher = object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    textChangeListener?.setTextChage(position, p0?.toString()?.trim())
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            }
            editText.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)
        }
    }

    private inner class EditNoteDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>, position: Int): Boolean = item.type == 4

        override fun getItemViewLayoutId(): Int = R.layout.item_common_note

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            holder.setText(R.id.tv_edit_title, item.name)
            holder.setText(R.id.tv_hint_nub, "0/" + item.position)
            val editText = holder.getView<EditText>(R.id.et_content).apply {
                removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
                setText(item.content)
            }
            editText.setOnTouchListener { view, _ ->
                view.parent.requestDisallowInterceptTouchEvent(true)
                false
            }
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(item.position))
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (charSequence.length <= item.position) {
                        holder.setText(R.id.tv_hint_nub, charSequence.length.toString() + "/" + item.position)
                    }
                }

                override fun afterTextChanged(editable: Editable) {
                    noteChangeListener?.setTextChage(position, editable.toString().trim())
                }
            }
            editText.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)
        }
    }
}