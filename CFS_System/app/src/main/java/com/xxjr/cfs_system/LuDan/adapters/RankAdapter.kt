package com.xxjr.cfs_system.LuDan.adapters

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import rvadapter.BaseViewHolder
import rvadapter.ItemViewDelegate
import rvadapter.MultiItemAdapter
import java.util.Collections.max

class RankAdapter(val context: Context, list: List<CommonItem<*>>, val doubles: MutableList<Double>, val maxN: Int) : MultiItemAdapter<Any>(context, list) {
    private var onItemClick: RecycleItemClickListener? = null
    private var onItemStoreClick: RecycleItemClickListener? = null
    private var onItemRankClick: RecycleItemClickListener? = null
    private val permits = CFSUtils.getPostPermitValue(Hawk.get("PermitValue", ""), "808")
    private val companys = (Hawk.get<String>("CompanyPowers") ?: "").split(",")
    private val zoneIds = (Hawk.get<String>("ZonePowers") ?: "").split(",")

    init {
        addItemViewDelegate(RankDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(ChooseDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(DataShowDelegate() as ItemViewDelegate<Any>)
        addItemViewDelegate(RankMoreDelegate() as ItemViewDelegate<Any>)
    }

    fun setOnItemClick(onItemClick: RecycleItemClickListener) {
        this.onItemClick = onItemClick
    }

    fun setOnItemStoreClick(onItemClick: RecycleItemClickListener) {
        this.onItemStoreClick = onItemClick
    }

    fun setOnItemRankClick(onItemClick: RecycleItemClickListener) {
        this.onItemRankClick = onItemClick
    }

    private inner class RankDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 0

        override fun getItemViewLayoutId(): Int = R.layout.view_rank

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setText(R.id.tv_first_name, if (TextUtils.isEmpty(item?.name)) "无" else item?.name)
            holder?.setText(R.id.tv_second_name, if (TextUtils.isEmpty(item?.content)) "无" else item?.content)
            holder?.setText(R.id.tv_third_name, if (TextUtils.isEmpty(item?.hintContent)) "无" else item?.hintContent)
        }
    }

    private inner class ChooseDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 1

        override fun getItemViewLayoutId(): Int = R.layout.item_rank_choose

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setINVISIBLE(R.id.tv_store_name, !(item?.isEnable ?: true))
            holder?.setINVISIBLE(R.id.iv_store, !(item?.isEnable ?: true))
            holder?.setText(R.id.tv_store_name, item?.name)
            holder?.setVisible(R.id.tv_type, true)
            holder?.setVisible(R.id.line_type, true)
            if (item?.icon == 4) {
                holder?.setText(R.id.tv_type, "年")
            } else {
                holder?.setText(R.id.tv_type, "月")
            }
            val month1 = holder?.getView<TextView>(R.id.tv_month_1)
            val month2 = holder?.getView<TextView>(R.id.tv_month_2)
            val month3 = holder?.getView<TextView>(R.id.tv_month_3)
            month3?.text = item?.content
            month2?.text = item?.hintContent
            month1?.text = item?.date
            when (item?.position) {
                1 -> showMonth(month1!!, month2!!, month3!!)
                2 -> showMonth(month2!!, month1!!, month3!!)
                3 -> showMonth(month3!!, month2!!, month1!!)
            }
            holder?.setOnClickListener(R.id.tv_type) { onItemClick?.onItemClick(0) }
            holder?.setOnClickListener(R.id.tv_month_1) { onItemClick?.onItemClick(1) }
            holder?.setOnClickListener(R.id.tv_month_2) { onItemClick?.onItemClick(2) }
            holder?.setOnClickListener(R.id.tv_month_3) { onItemClick?.onItemClick(3) }
            if (item?.isLineShow == true) {
                holder?.setImageResource(R.id.iv_store, R.mipmap.qiehuan)
                if (item.isClick) {
                    holder?.setOnClickListener(R.id.tv_store_name) { onItemStoreClick?.onItemClick(1) }
                } else {
                    holder?.setOnClickListener(R.id.tv_store_name) { onItemStoreClick?.onItemClick(2) }
                }
            } else holder?.setOnClickListener(R.id.tv_store_name) { onItemStoreClick?.onItemClick(-1) }
        }
    }

    private inner class DataShowDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 2

        override fun getItemViewLayoutId(): Int = R.layout.item_progress_show

        override fun convert(holder: BaseViewHolder?, item: CommonItem<*>?, position: Int) {
            holder?.setText(R.id.tv_rank_nub, String.format("%02d", position - 1))
            holder?.setText(R.id.tv_rank_name, item?.name)
            when (item?.position) {
                4 -> {
                    holder?.setText(R.id.tv_amount, "${Utils.div(item.percent)}万")
                    if (permits != null && permits.contains("E6")) holder?.setVisible(R.id.tv_amount, true)
                    else holder?.setVisible(R.id.tv_amount, isShowAmount(item.remark
                            ?: "", item.date ?: ""))
                }
                5, 6 -> {
                    holder?.setText(R.id.tv_amount, "${item.percent.toInt()}笔")
                    if (permits != null && permits.contains("E6")) holder?.setVisible(R.id.tv_amount, true)
                    else holder?.setVisible(R.id.tv_amount, isShowAmount(item.remark
                            ?: "", item.date ?: ""))
                }
            }
            val bar = holder?.getView<ProgressBar>(R.id.progress)
            val v = max<Double>(doubles)
            bar?.max = v.toInt()
            bar?.progress = item?.percent!!.toInt()
            when ((position - 1) % 5) {
                1 -> bar?.progressDrawable = context.resources.getDrawable(R.drawable.progress_color_1)
                2 -> bar?.progressDrawable = context.resources.getDrawable(R.drawable.progress_color_2)
                3 -> bar?.progressDrawable = context.resources.getDrawable(R.drawable.progress_color_3)
                4 -> bar?.progressDrawable = context.resources.getDrawable(R.drawable.progress_color_4)
                0 -> bar?.progressDrawable = context.resources.getDrawable(R.drawable.progress_color_5)
            }
        }
    }

    private inner class RankMoreDelegate : ItemViewDelegate<CommonItem<*>> {
        override fun isForViewType(item: CommonItem<*>?, position: Int): Boolean = item?.type == 3

        override fun getItemViewLayoutId(): Int = R.layout.item_progress_more

        override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
            holder.setText(R.id.tv_rank_nub, String.format("%02d", position - 1))
            holder.setText(R.id.tv_rank_name, item.name)
            val bar = holder.getView<ProgressBar>(R.id.progress_return)
            bar?.max = maxN
            val bar2 = holder.getView<ProgressBar>(R.id.progress_signature)
            bar2?.max = max<Double>(doubles).toInt()
            if (item.position == 2) {
                holder.setText(R.id.tv_type_1, if (item.isClick) "回款量（" else "签单量（")
                holder.setText(R.id.tv_type_2, if (item.isClick) "签单量（" else "回款量（")
                holder.setText(R.id.tv_InTimeReceiveRate, "回款及时率${item.payType ?: ""}%")
                holder.setVisible(R.id.tv_InTimeReceiveRate, true)
            } else {
                holder.setText(R.id.tv_type_1, if (item.isClick) "放款金额（" else "放款笔数（")
                holder.setText(R.id.tv_type_2, if (item.isClick) "放款笔数（" else "放款金额（")
                holder.setVisible(R.id.tv_InTimeReceiveRate, false)
            }
            if (permits != null && permits.contains("E6") ||
                    isShowAmount(item.remark ?: "", item.date ?: "")) {
                holder.setText(R.id.tv_return, if (item.isClick) "${Utils.div(item.percent)}万" else "${item.icon}笔")
                holder.setText(R.id.tv_signature, if (item.isClick) "${item.icon}笔" else "${Utils.div(item.percent)}万")
            } else {
                holder.setText(R.id.tv_return, if (item.isClick) "***万" else "***笔")
                holder.setText(R.id.tv_signature, if (item.isClick) "***笔" else "***万")
            }
            bar?.progress = item.percent.toInt()
            bar2?.progress = item.icon
            holder.convertView.setOnClickListener {
                if ((Hawk.get<String>("UserType").toInt()) >= 81 ||
                        isShowAmount(item.remark ?: "", item.date ?: "")) {
                    onItemRankClick?.onItemClick(position)
                }
            }
        }
    }

    private fun showMonth(select: TextView, tv1: TextView, tv2: TextView) {
        select.run {
            setBackgroundResource(R.color.font_home)
            setTextColor(resources.getColor(R.color.white))
        }
        tv1.run {
            setBackgroundResource(R.color.transparent)
            setTextColor(resources.getColor(R.color.font_c3))
        }
        tv2.run {
            setBackgroundResource(R.color.transparent)
            setTextColor(resources.getColor(R.color.font_c3))
        }
    }

    //是否显示金额和笔数
    private fun isShowAmount(companyId: String, zoneId: String): Boolean {
        var isShow = false
        for (myCompany in companys) {
            if (companyId.isNotBlank() && companyId == myCompany) {
                isShow = true
                break
            }
        }
        if (!isShow) {
            for (myZone in zoneIds) {
                if (zoneId.isNotBlank() && myZone == zoneId) {
                    isShow = true
                    break
                }
            }
        }
        return isShow
    }
}