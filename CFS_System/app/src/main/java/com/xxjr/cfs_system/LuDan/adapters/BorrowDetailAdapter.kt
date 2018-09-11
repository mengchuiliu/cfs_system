package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.view.activitys.borrow.view.BorrowDetailActivity
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.Schedule
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

/**
 * Created by Administrator on 2017/10/31.
 */
class BorrowDetailAdapter(context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private val activity = context as BorrowDetailActivity
    private var onItemClick: RecycleItemClickListener? = null

    init {
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TextDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(DetailsDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(RecycleDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {

        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            if (item.icon == 0) {
                holder.setBackgroundRes(R.id.v_blank, R.color.blank_bg)
            } else {
                holder.setBackgroundRes(R.id.v_blank, item.icon)
            }
        }
    }

    private inner class TitleDelegate : ItemViewDelegate<CommonItem<*>> {

        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_common_title

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            holder.setImageResource(R.id.iv_title, item.icon)
            holder.setText(R.id.tv_title, item.name)
            holder.setVisible(R.id.tv_right, item.isClick)
            holder.setTextColorRes(R.id.tv_right, R.color.font_home)
            holder.setTextSize(R.id.tv_right, 15f)
            holder.setOnClickListener(R.id.tv_right, {
                onItemClick?.onItemClick(position)
            })
        }
    }

    private inner class TextDelegate : ItemViewDelegate<CommonItem<*>> {

        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_show

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            holder.setText(R.id.tv_content_name, item.name)
            if (item.isLineShow) {
                holder.setTextColorRes(R.id.tv_content, R.color.font_home)
            } else {
                holder.setTextColorRes(R.id.tv_content, R.color.font_c3)
            }
            holder.setText(R.id.tv_content, item.content)
        }
    }

    private inner class DetailsDelegate : ItemViewDelegate<CommonItem<*>> {

        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_details_show

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            holder.setText(R.id.tv_content, item.name)
            if (item.isClick) {
                holder.setImageResource(R.id.iv_show, R.mipmap.icon_pack_up)
            } else {
                holder.setImageResource(R.id.iv_show, R.mipmap.icon_look_over)
            }
            holder.convertView.setOnClickListener { onItemClick?.onItemClick(position) }
        }
    }


    private inner class RecycleDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>, position: Int): Boolean = item.type == 4

        override fun getItemViewLayoutId(): Int = R.layout.item_recycle

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            val recyclerView = holder.getView<RecyclerView>(R.id.recycle_item)
            val param = recyclerView.layoutParams as RecyclerView.LayoutParams
            if (item.isClick) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT
                param.width = LinearLayout.LayoutParams.MATCH_PARENT
                recyclerView.setPadding(Utils.dip2px(activity, 15f), 0, 0, 5)
                recyclerView.visibility = View.VISIBLE
            } else {
                param.height = 0
                param.width = 0
                recyclerView.visibility = View.GONE
            }
            recyclerView.layoutParams = param
            recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.white))
            recyclerView.layoutManager = LinearLayoutManager(activity)
            if (item.list != null && item.list.size > 0) {
                val adapter = object : CommonAdapter<Schedule>(activity, item.list as MutableList<Schedule>?, R.layout.item_schedule) {
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

                        if (position == item.list.size - 1) {
                            holder.setINVISIBLE(R.id.tv_line_1, true)
                        } else {
                            holder.setINVISIBLE(R.id.tv_line_1, false)
                        }
                    }
                }
                recyclerView.adapter = adapter
            }
        }
    }

}