package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

/**
 * Created by Administrator on 2018/3/26.
 */
class TradeDetailAdapter(val context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(DetailsDelegate() as ItemViewDelegate<Any>)
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            if (item?.isClick == true) {
                holder?.setBackgroundRes(R.id.v_blank, R.color.white)
            } else {
                holder?.setBackgroundRes(R.id.v_blank, R.color.blank_bg)
            }
            if (item?.isLineShow == true) {
                val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(context, 1f))
                holder?.convertView?.layoutParams = lp
            } else {
                val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(context, 10f))
                holder?.convertView?.layoutParams = lp
            }
        }
    }

    private inner class TitleDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_common_text

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setVisible(R.id.line, false)
            holder?.setText(R.id.tv_content_name, item?.name)
            holder?.setTextColorRes(R.id.tv_content_name, R.color.font_c3)
            holder?.setText(R.id.tv_content, item?.content)
            holder?.setTextColorRes(R.id.tv_content, item?.icon ?: R.color.white)
        }
    }


    private inner class DetailsDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>, position: Int) {
            holder?.setText(R.id.tv_content_name, item.name)
            holder?.setText(R.id.tv_content, item.content)
            holder?.getView<TextView>(R.id.tv_content)?.setSingleLine(false)
            holder?.setVisible(R.id.v_line, false)
            holder?.convertView?.isEnabled = false
            holder?.getView<LinearLayout>(R.id.ll_details)?.setPadding(Utils.dip2px(context, 15f),
                    Utils.dip2px(context, 13f), Utils.dip2px(context, 15f), 0)
        }
    }
}