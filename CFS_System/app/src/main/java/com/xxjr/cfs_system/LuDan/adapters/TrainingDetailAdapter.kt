package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.StaffInfo
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

class TrainingDetailAdapter(val context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onItemClick: RecycleItemClickListener? = null
    private var onDetailClick: RecycleItemClickListener? = null

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TextDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(StaffDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    fun setOnDetailClick(onItemClick: RecycleItemClickListener) {
        onDetailClick = onItemClick
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            if (item?.isLineShow == false) holder?.setBackgroundRes(R.id.v_blank, R.color.blank_bg)
            else holder?.setBackgroundRes(R.id.v_blank, R.color.white)
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(context, 8f))
            holder?.convertView?.layoutParams = lp
        }
    }

    private inner class TitleDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            holder.setVisible(R.id.iv_title, true)
            holder.setImageResource(R.id.iv_title, item.icon)

            holder.setText(R.id.tv_content_name, item.name)
            holder.setTextColorRes(R.id.tv_content_name, R.color.font_home)
            holder.getView<TextView>(R.id.tv_content_name).setSingleLine(false)
            holder.getView<TextView>(R.id.tv_content_name).maxEms = 12

            holder.setText(R.id.tv_content, item.content)
            holder.setTextSize(R.id.tv_content, 11f)
            holder.setTextColorRes(R.id.tv_content, R.color.font_c6)
            holder.getView<TextView>(R.id.tv_content).gravity = Gravity.LEFT

            holder.setVisible(R.id.tv_yuan, item.position != 0)
            holder.setText(R.id.tv_yuan, item.hintContent)
            if (item.position != 0) holder.setTextColorRes(R.id.tv_yuan, item.position)
            holder.setOnClickListener(R.id.tv_yuan) { if (item.isClick) onItemClick?.onItemClick(position) }
        }
    }

    private inner class TextDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_show

        override fun convert(holder: BaseViewHolder?, item: CommonItem<Any>?, position: Int) {
            val param = holder?.convertView?.layoutParams
            if (item?.isClick == false) {
                holder?.convertView?.visibility = View.VISIBLE
                param?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                param?.width = ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                holder?.convertView?.visibility = View.GONE
                param?.height = 0
                param?.width = 0
            }
            holder?.convertView?.layoutParams = param
            holder?.setText(R.id.tv_content_name, item?.name)
            holder?.setText(R.id.tv_content, item?.content)
            holder?.getView<TextView>(R.id.tv_content)?.setSingleLine(false)
        }
    }

    private inner class StaffDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_staff

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            val staff = item.item as StaffInfo
            holder.setText(R.id.tv_name, "${staff.UserName}(${staff.UserPositionName})")
            holder.setText(R.id.tv_project_content, "${if (staff.ProjectNames.isNullOrBlank()) ""
            else staff.ProjectNames?.replace(",", "\n")}")
            holder.setText(R.id.tv_state, when (staff.IsPass) {
                0 -> "未设置"
                1 -> "是"
                2 -> "否"
                else -> ""
            })
            holder.setText(R.id.tv_integral, staff.Integral ?: "0")
            holder.setText(R.id.tv_grade, staff.Achievement ?: "0")
            holder.convertView.setOnClickListener { onDetailClick?.onItemClick(position) }
        }
    }

}