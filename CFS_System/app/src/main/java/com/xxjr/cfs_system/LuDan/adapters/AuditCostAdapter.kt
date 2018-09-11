package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.xiaoxiao.ludan.R
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

/**
 * Created by Administrator on 2017/11/30.
 */
class AuditCostAdapter(context: Context, data: List<Any>) : MultiItemAdapter<Any>(context, data) {
    private var onDelItemClick: RecycleItemClickListener? = null

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TextDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnDelItemCheck(onItemClick: RecycleItemClickListener) {
        this.onDelItemClick = onItemClick
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            if (position == 7 || position == 21) {
                holder?.setBackgroundRes(R.id.v_blank, R.color.white)
            } else {
                holder?.setBackgroundRes(R.id.v_blank, R.color.blank_bg)
            }
        }
    }

    private inner class TitleDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_common_title

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setImageResource(R.id.iv_title, item!!.icon)
            holder?.setText(R.id.tv_title, item?.name)
            holder?.setVisible(R.id.tv_right, item?.isLineShow ?: false)
            holder?.setText(R.id.tv_right, "删除")
            holder?.setTextSize(R.id.tv_right, 15f)
            holder?.setTextColorRes(R.id.tv_right, R.color.font_home)
            holder?.setOnClickListener(R.id.tv_right, {
                onDelItemClick?.onItemClick(position)
            })
        }
    }

    private inner class TextDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_show

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setText(R.id.tv_content_name, item?.name)
            holder?.setText(R.id.tv_content, item?.content)
            if (position == 2 || position == 14) {
                holder?.setTextColorRes(R.id.tv_content, R.color.font_home)
            } else {
                holder?.setTextColorRes(R.id.tv_content, R.color.font_c3)
            }
            holder?.setVisible(R.id.tv_content_right, true)
            holder?.setText(R.id.tv_content_right, item?.hintContent)
            val param = holder?.convertView?.layoutParams
            if (item?.isClick == true) {
                holder?.getView<TextView>(R.id.tv_content)?.setSingleLine(false)
//                holder?.getView<TextView>(R.id.tv_content)?.maxLines = 5
            } else {
                holder?.getView<TextView>(R.id.tv_content)?.setSingleLine(true)
                holder?.getView<TextView>(R.id.tv_content)?.maxLines = 1
                holder?.getView<TextView>(R.id.tv_content)?.ellipsize = TextUtils.TruncateAt.END
            }
            if (item?.isLineShow == true) {
                holder?.convertView?.visibility = View.GONE
                param?.height = 0
                param?.width = 0
            } else {
                holder?.convertView?.visibility = View.VISIBLE
                param?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                param?.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            holder?.convertView?.layoutParams = param
        }
    }
}