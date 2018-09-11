package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import com.xiaoxiao.ludan.R
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter

/**
 * Created by Administrator on 2017/11/16.
 */
class RankDetailsAdapter(context: Context, data: List<CommonItem<*>>) : MultiItemAdapter<Any>(context, data) {

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TextDelegate() as ItemViewDelegate<Any>)
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            if (position == 6) {
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
        }
    }

    private inner class TextDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_common_show

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setText(R.id.tv_content_name, item?.name)
            holder?.setText(R.id.tv_content, item?.content)
        }
    }


}