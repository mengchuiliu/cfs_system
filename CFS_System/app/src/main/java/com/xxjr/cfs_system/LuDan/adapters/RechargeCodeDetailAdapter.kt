package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.SwipeMenuLayout
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.LoanInfo
import refresh_recyclerview.DividerItemDecoration
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter
import java.math.BigDecimal

/**
 * Created by Administrator on 2017/10/17.
 */
class RechargeCodeDetailAdapter(val context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onItemClick: RecycleItemLongClickListener? = null

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(BlankWhiteDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TextDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(GoldDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemLongClickListener) {
        this.onItemClick = onItemClick
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setBackgroundRes(R.id.v_blank, R.color.blank_bg)
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(context, 10f))
            holder?.convertView?.layoutParams = lp
        }
    }

    private inner class BlankWhiteDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(context, 10f))
            holder?.convertView?.layoutParams = lp
        }
    }

    private inner class TitleDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
            holder.setText(R.id.tv_content_name, item.name)
            if (item.isClick) {
                holder.setTextColorRes(R.id.tv_content_name, R.color.font_c6)
                holder.setTextSize(R.id.tv_content_name, 14f)
                holder.setText(R.id.tv_content, item.content)
                holder.setTextSize(R.id.tv_content, 14f)
                holder.getView<TextView>(R.id.tv_content).gravity = Gravity.LEFT
                val textView = holder.getView<TextView>(R.id.tv_content_name)
                val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(0, 0, 0, 0)
                textView.layoutParams = layoutParams
            } else {
                holder.setTextColorRes(R.id.tv_content_name, R.color.font_home)
                holder.setTextSize(R.id.tv_content_name, 15f)
                holder.setText(R.id.tv_content, "")
            }
        }
    }

    private inner class TextDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_recharge

        override fun convert(holder: BaseViewHolder?, item: CommonItem<Any>?, position: Int) {
            holder?.setText(R.id.tv_content_name, item?.name)
            holder?.setTextColorRes(R.id.tv_content_name, R.color.font_c6)
            holder?.setTextSize(R.id.tv_content_name, 14f)
            holder?.setText(R.id.tv_content, item?.content)
            holder?.getView<TextView>(R.id.tv_content)?.setSingleLine(false)

            holder?.setVisible(R.id.tv_copy, item?.isLineShow ?: false)
            if (item?.icon ?: 0 == 0) {
                holder?.setTextColorRes(R.id.tv_content, R.color.font_c3)
                holder?.setTextSize(R.id.tv_content, 14f)
            } else {
                holder?.setTextColorRes(R.id.tv_content, item?.icon ?: 0)
                if (item?.isClick == false) holder?.setTextSize(R.id.tv_content, 17f) else holder?.setTextSize(R.id.tv_content, 14f)
            }
            holder?.setOnClickListener(R.id.tv_copy) {
                if (item?.isEnable == false) {
                    onItemClick?.onItemLongClick(it, position, item.content)
                }
            }
        }
    }

    private inner class GoldDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 4

        override fun getItemViewLayoutId(): Int = R.layout.item_gold_info

        override fun convert(holder: BaseViewHolder?, item: CommonItem<Any>?, position: Int) {
            holder?.setText(R.id.tv_gold_name, item?.name)
            holder?.setText(R.id.tv_gold_bank, item?.content)
            holder?.setText(R.id.tv_gold_card, item?.remark)
        }
    }
}