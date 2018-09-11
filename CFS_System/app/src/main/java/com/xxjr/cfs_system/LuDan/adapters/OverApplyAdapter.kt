package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.content.DialogInterface
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.view.activitys.OverApplyActivity
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.Schedule
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

class OverApplyAdapter(context: Context, list: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, list) {
    private var onItemClick: RecycleItemClickListener? = null
    private var onCostItemClick: RecycleItemClickListener? = null
    private var delItemClick: RecycleItemClickListener? = null
    private var textChange: TextChangeListener? = null
    private val activity: OverApplyActivity = context as OverApplyActivity

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TextDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(CostDetailsDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(RecyclerDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(DetailsDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(EditNoteDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    fun setOnCostItemClick(onItemClick: RecycleItemClickListener) {
        this.onCostItemClick = onItemClick
    }

    fun setDelItemClick(onItemClick: RecycleItemClickListener) {
        this.delItemClick = onItemClick
    }

    fun setTextChangeListener(textChange: TextChangeListener) {
        this.textChange = textChange
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            if (item?.isClick!!) {
                holder?.setBackgroundRes(R.id.v_blank, R.color.white)
            } else {
                holder?.setBackgroundRes(R.id.v_blank, R.color.blank_bg)
            }
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

    private inner class TextDelegate : ItemViewDelegate<CommonItem<*>> {

        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_show

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            val param = holder.convertView.layoutParams
            if (item.name.isNullOrBlank()) {
                holder.convertView.visibility = View.GONE
                param.height = 0
                param.width = 0
            } else {
                holder.convertView.visibility = View.VISIBLE
                param.height = ViewGroup.LayoutParams.WRAP_CONTENT
                param.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            holder.convertView.layoutParams = param
            holder.setText(R.id.tv_content_name, item.name)
            holder.setText(R.id.tv_content, item.content)
            holder.setVisible(R.id.tv_content_right, item.isLineShow)
            if (item.isLineShow) {
                holder.setText(R.id.tv_content_right, item.hintContent)
            }
        }
    }

    private inner class CostDetailsDelegate : ItemViewDelegate<CommonItem<*>> {

        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_cost_details

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            if (item.isClick) {
                holder.setImageResource(R.id.iv_cost_show, R.mipmap.icon_pack_up)
            } else {
                holder.setImageResource(R.id.iv_cost_show, R.mipmap.icon_look_over)
            }
            holder.setText(R.id.tv_content_name, item.name)
            holder.setText(R.id.tv_content, item.content)
            holder.convertView.setOnClickListener({
                onCostItemClick?.onItemClick(position)
            })
        }
    }

    private inner class RecyclerDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 4

        override fun getItemViewLayoutId(): Int = R.layout.item_recycle

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val recyclerView = holder?.getView<RecyclerView>(R.id.recycle_item)
            recyclerView?.layoutManager = LinearLayoutManager(activity)
            val param = recyclerView?.getLayoutParams() as RecyclerView.LayoutParams
            if (item?.isClick!! && item.list != null && item.list.isNotEmpty()) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT
                param.width = LinearLayout.LayoutParams.MATCH_PARENT
                recyclerView.setPadding(Utils.dip2px(activity, 12f), 0, 0, 0)
                recyclerView.visibility = View.VISIBLE
                when (position) {
                    20 -> showCost(recyclerView, item.list as MutableList<Schedule>)
                    24 -> showBooks(recyclerView, item.list as MutableList<CommonItem<*>>)
                    else -> {
                        recyclerView.setPadding(0, 0, 0, 0)
                        showOver(recyclerView, item.list as MutableList<CommonItem<*>>)
                    }
                }
            } else {
                param.height = 0
                param.width = 0
                recyclerView.visibility = View.GONE
            }
            recyclerView.layoutParams = param
            recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.white))
        }

    }

    private inner class DetailsDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 5

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>, position: Int) {
            holder?.setText(R.id.tv_content_name, item.name)
            holder?.setText(R.id.tv_content, item.content)
            holder?.setVisible(R.id.iv_right, item.isClick)
            holder?.setVisible(R.id.v_line, !item.isLineShow)
            holder?.convertView?.isEnabled = item.isEnable
            if (item.isEnable) {
                holder?.convertView?.setOnClickListener({
                    onItemClick?.onItemClick(position)
                })
            }
        }
    }

    private inner class EditNoteDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 6

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

    //累计成本
    private fun showCost(recyclerView: RecyclerView, list: MutableList<Schedule>) {
        val adapter = object : CommonAdapter<Schedule>(activity, list, R.layout.item_schedule) {
            override fun convert(holder: BaseViewHolder, schedule: Schedule, position: Int) {
                holder.setText(R.id.tv_status, schedule.status)
                holder.setText(R.id.tv_name_date, schedule.name + "  " + schedule.date)
                if (position == 0) {
                    holder.setINVISIBLE(R.id.tv_line, true)
                    holder.setImageResource(R.id.iv_dot, R.mipmap.icon_dot_new)
                } else {
                    holder.setINVISIBLE(R.id.tv_line, false)
                    holder.setImageResource(R.id.iv_dot, R.mipmap.icon_dot)
                }

                if (position == list.size - 1) {
                    holder.setINVISIBLE(R.id.tv_line_1, true)
                } else {
                    holder.setINVISIBLE(R.id.tv_line_1, false)
                }
            }
        }
        recyclerView.setAdapter(adapter)
    }

    //出入账
    private fun showBooks(recyclerView: RecyclerView, list: MutableList<CommonItem<*>>) {
        val lendAdapter = object : CommonAdapter<CommonItem<*>>(activity, list, R.layout.item_chu_ru) {
            override fun convert(holder: BaseViewHolder, commonItem: CommonItem<*>, position: Int) {
                holder.setText(R.id.tv_pay, commonItem.hintContent)
                holder.setText(R.id.tv_pay_type, commonItem.payType)
                holder.setText(R.id.tv_return_amount, commonItem.name)
                holder.setText(R.id.tv_people, commonItem.content)
                holder.setText(R.id.tv_date, commonItem.date)
                if (commonItem.remark == "备  注 ：") {
                    holder.setVisible(R.id.tv_remark, false)
                } else {
                    holder.setVisible(R.id.tv_remark, true)
                    holder.setText(R.id.tv_remark, commonItem.remark)
                }
                when (activity.getLoanInfo().scheduleId) {
                    109, 5, -3, -4, -5 -> {
                        if (activity.getPermits().contains("CE")) {
                            if (!commonItem.isClick)
                                holder.setVisible(R.id.tv_delete, true)
                        }
                    }
                    else -> holder.setVisible(R.id.tv_delete, false)
                }
                holder.setOnClickListener(R.id.tv_delete) {
                    CustomDialog.showTwoButtonDialog(activity, "确定删除该账单？", "确定", "取消") { dialogInterface, i ->
                        dialogInterface.dismiss()
                        delItemClick?.onItemClick(commonItem.type)
                    }
                }
            }
        }
        recyclerView.adapter = lendAdapter
    }

    //结案信息
    private fun showOver(recyclerView: RecyclerView, list: MutableList<CommonItem<*>>) {
        val adapter = object : CommonAdapter<CommonItem<*>>(activity, list, R.layout.item_over_apply) {
            override fun convert(holder: BaseViewHolder, commonItem: CommonItem<*>, position: Int) {
                holder.setText(R.id.tv_over_title, commonItem.hintContent)
                holder.setText(R.id.tv_name, commonItem.name)
                holder.setText(R.id.tv_content, commonItem.content)
                holder.setText(R.id.tv_date, commonItem.date)
                if (position == list.size - 1) {
                    holder.setVisible(R.id.tv_line1, false)
                }
            }
        }
        recyclerView.adapter = adapter
    }
}