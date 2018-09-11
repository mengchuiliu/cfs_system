package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.widget.EditText
import com.xiaoxiao.ludan.R
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

/**
 * Created by Administrator on 2017/12/1.
 */
class AddBankManagerAdapter(context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onItemClick: RecycleItemClickListener? = null
    private var textChange: TextChangeListener? = null

    init {
        addItemViewDelegate(DetailsDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditNoteDelegate() as ItemViewDelegate<Any>)
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
            holder?.convertView?.setOnClickListener({
                onItemClick?.onItemClick(position)
            })
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
            if (item?.isClick!!) {
                edittext.keyListener = DigitsKeyListener.getInstance("0123456789")
//                edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(7))
            } else {
                edittext.inputType = InputType.TYPE_CLASS_TEXT
                edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
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

    private inner class EditNoteDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_note

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            holder.setText(R.id.tv_edit_title, item.name)
            holder.setText(R.id.tv_hint_nub, "0/" + item.position)
            val editText = holder.getView<EditText>(R.id.et_content)
            editText.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            editText.setText(item.content)
            editText.hint = item.hintContent
            editText.setOnTouchListener { view, motionEvent ->
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
                    textChange?.setTextChage(position, editable.toString().trim())
                }
            }
            editText.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)
        }
    }
}