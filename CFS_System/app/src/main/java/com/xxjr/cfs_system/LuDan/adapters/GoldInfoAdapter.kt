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
class GoldInfoAdapter(val context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onItemClick: RecycleItemClickListener? = null

    init {
        addItemViewDelegate(BlankWhiteDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(DetailsDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(PswDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TextDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    private inner class BlankWhiteDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(context, 8f))
            holder?.convertView?.layoutParams = lp
        }
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setBackgroundRes(R.id.v_blank, R.color.blank_bg)
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(context, 10f))
            holder?.convertView?.layoutParams = lp
        }
    }

    private inner class DetailsDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>, position: Int) {
            holder?.setText(R.id.tv_content_name, item.name)
            holder?.setText(R.id.tv_content, item.content)
            holder?.setVisible(R.id.v_line, false)
            holder?.setVisible(R.id.iv_title, item.isLineShow)
            val textView = holder?.getView<TextView>(R.id.tv_content_name)
            val tp = textView?.paint
            if (item.isLineShow) {
                val imageView = holder?.getView<ImageView>(R.id.iv_title)
                val lp = LinearLayout.LayoutParams(Utils.dip2px(context, 20f), Utils.dip2px(context, 15f))
                imageView?.layoutParams = lp
                holder?.setImageResource(R.id.iv_title, item.icon)
                holder?.setTextColorRes(R.id.tv_content_name, R.color.font_c6)
                tp?.isFakeBoldText = false
            } else {
                holder?.setTextColorRes(R.id.tv_content_name, R.color.font_c3)
                tp?.isFakeBoldText = true
            }
            holder?.convertView?.isEnabled = false
            holder?.getView<LinearLayout>(R.id.ll_details)?.setPadding(Utils.dip2px(context, 30f),
                    Utils.dip2px(context, 12f), Utils.dip2px(context, 15f), 0)
        }
    }

    private inner class PswDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_details

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>, position: Int) {
            if (item.isEnable) holder?.convertView?.visibility = View.VISIBLE else holder?.convertView?.visibility = View.GONE
            holder?.setImageResource(R.id.iv_title, item.icon)
            holder?.setText(R.id.tv_content_name, item.name)
            holder?.setTextColorRes(R.id.tv_content_name, R.color.font_c3)
            val imageView = holder?.getView<ImageView>(R.id.iv_title)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            imageView?.layoutParams = lp
            holder?.setVisible(R.id.iv_title, true)
            holder?.setVisible(R.id.iv_right, true)
            holder?.setVisible(R.id.v_line, item.position == 1)
            holder?.convertView?.setOnClickListener({
                onItemClick?.onItemClick(item.position)
            })
        }
    }

    private inner class TextDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 4

        override fun getItemViewLayoutId(): Int = R.layout.item_common_show

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setText(R.id.tv_content_name, item?.name)
            holder?.setText(R.id.tv_content, item?.content)
            holder?.setTextColorRes(R.id.tv_content_name, R.color.font_c9)
            holder?.convertView?.setPadding(Utils.dip2px(context, 30f), Utils.dip2px(context, 12f), Utils.dip2px(context, 15f), 0)
        }
    }
}