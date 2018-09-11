package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.text.*
import android.text.InputFilter.LengthFilter
import android.text.method.DigitsKeyListener
import android.widget.EditText
import android.widget.LinearLayout
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.view.activitys.borrow.view.BorrowActivity
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter


class BorrowAdapter(context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private val activity = context as BorrowActivity
    private var shouldStopChange = false
    private var onItemClick: RecycleItemClickListener? = null
    private var textChange: TextChangeListener? = null

    init {
        addItemViewDelegate(ChooseDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TextShowDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(Edit1Delegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditNoteDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    fun setTextChangeListener(textChange: TextChangeListener) {
        this.textChange = textChange
    }

    private inner class ChooseDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.item_common_choose

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            holder.setText(R.id.tv_content_name, item.name)
            if (item.content.isNullOrBlank()) {
                holder.setTextColorRes(R.id.tv_content, R.color.font_c9)
                holder.setText(R.id.tv_content, "请选择")
            } else {
                holder.setTextColorRes(R.id.tv_content, R.color.font_c3)
                holder.setText(R.id.tv_content, item.content)
            }
            val layout = holder.getView<LinearLayout>(R.id.ll_content)
            layout.setOnClickListener { onItemClick?.onItemClick(position) }
        }
    }

    private inner class TextShowDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_common_text

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setText(R.id.tv_content_name, item?.name)
            holder?.setText(R.id.tv_content, item?.content ?: "")
        }
    }

    private inner class EditDelegate : ItemViewDelegate<CommonItem<*>> {

        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_edit

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            shouldStopChange = false
            holder.setText(R.id.tv_content_name, item.name)
            holder.setVisible(R.id.tv_yuan, true)
            val editText = holder.getView<EditText>(R.id.et_content)
            editText.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            editText.setText(item.content)
            editText.hint = item.hintContent
            editText.keyListener = DigitsKeyListener.getInstance("0123456789")
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun afterTextChanged(editable: Editable) {
                    if (shouldStopChange) {
                        shouldStopChange = false
                        return
                    }
                    shouldStopChange = true

                    val str = editable.toString().trim().replace(",".toRegex(), "")
                    val len = str.length
                    if (len > 9) {
                        activity.showMsg("拆借金额不能大于1亿!")
                        editText.setText("")
                        textChange?.setTextChage(position, "")
                        return
                    }
                    textChange?.setTextChage(position, str)
                    val courPos: Int
                    val builder = StringBuilder()
                    for (i in 0 until len) {
                        builder.append(str[i])
                        if (i == 2 || i == 5 || i == 8) {
                            if (i != len - 1)
                                builder.append(",")
                        }
                    }
                    courPos = builder.length
                    editText.setText(builder.toString())
                    editText.setSelection(courPos)
                }
            }
            editText.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)
        }
    }

    private inner class Edit1Delegate : ItemViewDelegate<CommonItem<*>> {

        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_common_edit

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            holder.setText(R.id.tv_content_name, item.name)
            holder.setVisible(R.id.tv_yuan, true)
            holder.setText(R.id.tv_yuan, "%")
            val editText = holder.getView<EditText>(R.id.et_content)
            editText.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            editText.setText(item.content)
            editText.hint = item.hintContent
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            val INPUT_FILTER_ARRAY = arrayOfNulls<InputFilter>(1)
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                }

                override fun onTextChanged(text: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (text.isNotEmpty()) {
                        val inputContent = text.toString()
                        if (inputContent.contains(".")) {
                            val maxLength = inputContent.indexOf(".") + 4 + 1
                            INPUT_FILTER_ARRAY[0] = LengthFilter(maxLength)
                        } else {
                            INPUT_FILTER_ARRAY[0] = LengthFilter(5)
                        }
                        editText.filters = INPUT_FILTER_ARRAY
                    }
                }

                override fun afterTextChanged(editable: Editable) {
                    val str = editable.toString().trim()
                    textChange?.setTextChage(position, str)
                }
            }
            editText.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)
        }
    }

    private inner class EditNoteDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 4

        override fun getItemViewLayoutId(): Int = R.layout.item_common_note

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            holder.setText(R.id.tv_edit_title, item.name)
            holder.setText(R.id.tv_hint_nub, "0/" + item.position)
            val editText = holder.getView<EditText>(R.id.et_content)
            editText.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            editText.setText(item.content)
            editText.setOnTouchListener { view, motionEvent ->
                view.parent.requestDisallowInterceptTouchEvent(true)
                false
            }
            editText.filters = arrayOf<InputFilter>(LengthFilter(item.position))
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