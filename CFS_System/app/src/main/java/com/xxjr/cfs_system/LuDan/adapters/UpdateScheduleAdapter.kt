package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.Reimburse
import refresh_recyclerview.SimpleItemDecoration
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter
import java.math.BigDecimal

class UpdateScheduleAdapter(val context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onItemClick: RecycleItemClickListener? = null
    private var textChange: TextChangeListener? = null
    private var textChangeListener: TextChangeListener? = null
    private var otherAmount = 0.0

    init {
        addItemViewDelegate(EditNoteDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(DetailsDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(RecyclerDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(ShowReimburseDelegate() as ItemViewDelegate<Any>)
    }

    fun setOtherAmount(otherAmount: Double) {
        this.otherAmount = otherAmount
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    fun setTextChangeListener(textChange: TextChangeListener) {
        this.textChange = textChange
    }

    fun setTextEditChangeListener(textChange: TextChangeListener) {
        this.textChangeListener = textChange
    }

    private inner class EditNoteDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.item_common_note

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            holder.setText(R.id.tv_edit_title, item.name)
            holder.setText(R.id.tv_hint_nub, "0/" + item.position)
            val editText = holder.getView<EditText>(R.id.et_content).apply {
                removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
                setText(item.content)
            }
//            editText.setOnTouchListener { view, _ ->
//                view.parent.requestDisallowInterceptTouchEvent(true)
//                false
//            }
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

    private inner class DetailsDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>, position: Int) {
            val param = holder?.convertView?.layoutParams
            if (item.isEnable) {
                holder?.convertView?.visibility = View.VISIBLE
                param?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                param?.width = ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                holder?.convertView?.visibility = View.GONE
                param?.height = 0
                param?.width = 0
            }
            holder?.convertView?.layoutParams = param
            holder?.setText(R.id.tv_content_name, item.name)
            holder?.setText(R.id.tv_content, item.content)
            holder?.setVisible(R.id.iv_right, item.isClick)
            holder?.setVisible(R.id.v_line, !item.isLineShow)
            holder?.convertView?.setOnClickListener {
                if (item.isClick) {
                    onItemClick?.onItemClick(position)
                }
            }

        }
    }

    private inner class EditDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_edit_small

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val edittext: EditText = holder?.getView(R.id.et_content)!!
            edittext.removeTextChangedListener(holder.convertView.getTag(R.id.tag_first) as? TextWatcher)
            holder.setText(R.id.tv_content_name, item?.name)
            holder.setText(R.id.et_content, item?.content)
            holder.setHint(R.id.et_content, item?.hintContent)
            holder.setVisible(R.id.tv_yuan, !(item?.isEnable ?: true))
            if (item?.isEnable == false) {
                holder.setText(R.id.tv_yuan, "%")
            }
            if (item?.isClick!!) {
                edittext.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))
            } else if (item.isLineShow) {
                edittext.keyListener = DigitsKeyListener.getInstance("0123456789")
                edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
            } else if (!item.isEnable) {
                edittext.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
            } else {
                edittext.inputType = InputType.TYPE_CLASS_TEXT
                edittext.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
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
            edittext.addTextChangedListener(textWatcher)
            holder.convertView.setTag(R.id.tag_first, textWatcher)
        }
    }

    private inner class RecyclerDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_recycle

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>, position: Int) {
            val recyclerView = holder?.getView<RecyclerView>(R.id.recycle_item)
            recyclerView?.layoutManager = LinearLayoutManager(context)
            val param = recyclerView?.layoutParams as RecyclerView.LayoutParams
            if (item.list != null && item.list.isNotEmpty()) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT
                param.width = LinearLayout.LayoutParams.MATCH_PARENT
                recyclerView.setPadding(Utils.dip2px(context, 13f), 0, 0, 0)
                recyclerView.visibility = View.VISIBLE
                recyclerView.addItemDecoration(SimpleItemDecoration(context, 1))
                recyclerView.adapter = object : CommonAdapter<Reimburse>(context, item.list as MutableList<Reimburse>, R.layout.item_reimburse) {
                    override fun convert(holder: BaseViewHolder, item: Reimburse, position: Int) {
                        holder.setText(R.id.tv_date, item.reimburseDate)
                        holder.setText(R.id.tv_amount, Utils.parseTwoMoney(BigDecimal((item.reimburseAmount + otherAmount))))
                        holder.setVisible(R.id.tv_interest, item.interests != 0.0)
                        holder.setText(R.id.tv_interest, "含利息${Utils.parseTwoMoney(BigDecimal(item.interests))}\n含其他月供$otherAmount")
                    }
                }
            } else {
                param.height = 0
                param.width = 0
                recyclerView.visibility = View.GONE
            }
            recyclerView.layoutParams = param
        }
    }


    private inner class ShowReimburseDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 4

        override fun getItemViewLayoutId(): Int = R.layout.item_show_reimburse

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            holder.setOnClickListener(R.id.tv_show) { onItemClick?.onItemClick(position) }
        }
    }
}