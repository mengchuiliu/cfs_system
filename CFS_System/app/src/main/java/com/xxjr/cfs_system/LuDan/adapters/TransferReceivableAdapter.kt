package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.LinearLayout
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
class TransferReceivableAdapter(val context: Context, data: List<CommonItem<Any>>) : MultiItemAdapter<Any>(context, data) {
    private var onTitleItemClick: RecycleItemClickListener? = null
    private var onItemClick: RecycleItemClickListener? = null

    init {
        addItemViewDelegate(BlankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(TitleDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(DetailsDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    fun setOnTitleItemClick(onItemClick: RecycleItemClickListener) {
        this.onTitleItemClick = onItemClick
    }

    private inner class BlankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.blank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setBackgroundRes(R.id.v_blank, R.color.blank_bg)
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(context, 6f))
            holder?.convertView?.layoutParams = lp
        }
    }

    private inner class TitleDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_tansfer_title

        override fun convert(holder: BaseViewHolder?, item: CommonItem<Any>?, position: Int) {
            holder?.setText(R.id.tv_title, item?.name)
            if (item?.isClick == true) {
                holder?.setImageResource(R.id.iv_check, R.mipmap.transfer_ok)
            } else {
                holder?.setImageResource(R.id.iv_check, R.mipmap.transfer_no)
            }
            holder?.setOnClickListener(R.id.iv_check, {
                onTitleItemClick?.onItemClick(position)
            })
        }
    }

    private inner class DetailsDelegate : ItemViewDelegate<CommonItem<Any>> {
        override fun isForViewType(item: CommonItem<Any>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_transfer_receivable

        override fun convert(holder: BaseViewHolder, detail: CommonItem<Any>, position: Int) {
            holder.setText(R.id.tv_loan_content, detail.content)
            holder.setText(R.id.tv_amount, String.format("￥%.2f", detail.percent))
            holder.setText(R.id.tv_pay_type, detail.payType)
            holder.setText(R.id.tv_customer, detail.name)
            holder.setText(R.id.tv_salesman, detail.hintContent)
            holder.setVisible(R.id.line, !detail.isLineShow)
            if (detail.isClick) {
                holder.setImageResource(R.id.iv_check, R.mipmap.transfer_ok)
            } else {
                holder.setImageResource(R.id.iv_check, R.mipmap.transfer_no)
            }
            holder.setOnClickListener(R.id.iv_check, {
                onItemClick?.onItemClick(position)
            })
        }
    }

}