package com.xxjr.cfs_system.LuDan.view.fragments

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.QuantityPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.QuantityVInter
import com.xxjr.cfs_system.ViewsHolder.Popup7
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.ReportCompare
import kotlinx.android.synthetic.main.fragment_quantity.*
import kotlinx.android.synthetic.main.item_rank_choose.*
import refresh_recyclerview.SimpleItemDecoration
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import java.util.*

/**
 * Created by Administrator on 2017/9/26.
 * 计量
 */
class QuantityFragment : BaseFragment(), QuantityVInter, View.OnClickListener {
    private val presenter = QuantityPresenter(this)
    private var type = 0
    private var curYear = StringBuilder("${DateUtil.getYear()}")
    private var companyID: String? = null
    private var companyType = ""//门店类型
    private var companyZone_id = ""//区域类型
    private var isSelect1 = false //时间选择
    private var isSelect2 = false
    private var isSelect3 = true
    private var doubles: MutableList<Double> = mutableListOf()
    private lateinit var adapter: CommonAdapter<ReportCompare>

    override fun getFrgContext(): Context = context

    override fun getType(): Int = type

    override fun showMsg(msg: String?) = ToastShow.showShort(activity.applicationContext, msg)

    override fun initView(inflater: LayoutInflater, savedInstanceState: Bundle?, arguments: Bundle): View {
        type = arguments.getInt("position")
        companyID = Hawk.get("CompanyID", "")
        return inflater.inflate(R.layout.fragment_quantity, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (type == 0) {
            initData(companyType, companyZone_id)
        }
        initRv()
    }

    fun initData(companyT: String, CompanyZone_id: String) {
        firstUp = false
        companyType = companyT
        companyZone_id = CompanyZone_id
//        ll_month.visibility = View.GONE
//        ll_year.visibility = View.VISIBLE
        tv_store_name.text = "${Hawk.get("CompanyID", "")}${Hawk.get("CompanyName", "")}"
        tv_month_3.text = "${DateUtil.getYear()}年"
        tv_month_2.text = "${DateUtil.getYear() - 1}年"
        tv_month_1.text = "${DateUtil.getYear() - 2}年"
        tv_store_name.setOnClickListener(this)
        tv_month_3.setOnClickListener(this)
        tv_month_2.setOnClickListener(this)
        tv_month_1.setOnClickListener(this)
        presenter.getData(curYear.toString(), companyID ?: "", companyType, companyZone_id)
    }

    private fun initRv() {
        val permits = CFSUtils.getPostPermitValue(Hawk.get("PermitValue", ""), "808")
        recycle_name.layoutManager = LinearLayoutManager(context)
        recycle_name.addItemDecoration(SimpleItemDecoration(context, 1))
        adapter = object : CommonAdapter<ReportCompare>(activity, mutableListOf(), R.layout.item_progress_compare) {
            override fun convert(holder: BaseViewHolder, item: ReportCompare, position: Int) {
                holder.setVisible(R.id.ll_show_1, item.show1)
                holder.setVisible(R.id.ll_show_2, item.show2)
                holder.setVisible(R.id.ll_show_3, item.show3)
                holder.setVisible(R.id.tv_amount_1, permits != null && permits.contains("E6"))
                holder.setVisible(R.id.tv_amount_2, permits != null && permits.contains("E6"))
                holder.setVisible(R.id.tv_amount_3, permits != null && permits.contains("E6"))
                holder.setText(R.id.tv_time_1, Utils.FormatTime(item.time1, "yyyy年M月", "yy年MM月"))
                holder.setText(R.id.tv_time_2, Utils.FormatTime(item.time2, "yyyy年M月", "yy年MM月"))
                holder.setText(R.id.tv_time_3, Utils.FormatTime(item.time3, "yyyy年M月", "yy年MM月"))
                if (type == 0) {
                    holder.setText(R.id.tv_amount_1, "${Utils.div(item.amount1)}万")
                    holder.setText(R.id.tv_amount_2, "${Utils.div(item.amount2)}万")
                    holder.setText(R.id.tv_amount_3, "${Utils.div(item.amount3)}万")
                } else if (type == 1) {
                    holder.setText(R.id.tv_amount_1, "${item.amount1.toInt()}笔")
                    holder.setText(R.id.tv_amount_2, "${item.amount2.toInt()}笔")
                    holder.setText(R.id.tv_amount_3, "${item.amount3.toInt()}笔")
                }
                val bar1 = holder.getView<ProgressBar>(R.id.progress_1)
                val bar2 = holder.getView<ProgressBar>(R.id.progress_2)
                val bar3 = holder.getView<ProgressBar>(R.id.progress_3)
                val v = Collections.max<Double>(doubles)
                bar1.max = v.toInt()
                bar1.progress = item.amount1.toInt()
                bar2.max = v.toInt()
                bar2.progress = item.amount2.toInt()
                bar3.max = v.toInt()
                bar3.progress = item.amount3.toInt()
            }
        }
        recycle_name.adapter = adapter
    }

    override fun refreshData(mutableList: MutableList<ReportCompare>, maxs: MutableList<Double>) {
        doubles.apply {
            clear()
            addAll(maxs)
        }
        adapter.setNewData(mutableList)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            tv_store_name -> {
                showChooseType(activity, presenter.getTypeDataList(companyType, companyZone_id))
            }
            tv_month_1 -> {
                if (isSelect1 && !isSelect2 && !isSelect3) {
                    showMsg("至少选择一个年份")
                    return
                }
                isSelect1 = !isSelect1
                showYear(tv_month_1, isSelect1)
            }
            tv_month_2 -> {
                if (!isSelect1 && isSelect2 && !isSelect3) {
                    showMsg("至少选择一个年份")
                    return
                }
                isSelect2 = !isSelect2
                showYear(tv_month_2, isSelect2)
            }
            tv_month_3 -> {
                if (!isSelect1 && !isSelect2 && isSelect3) {
                    showMsg("至少选择一个年份")
                    return
                }
                isSelect3 = !isSelect3
                showYear(tv_month_3, isSelect3)
            }
        }
    }

    private fun showYear(textView: TextView, isSelect: Boolean) {
        curYear.setLength(0)
        if (isSelect1) curYear.append(DateUtil.getYear() - 2)
        if (isSelect2) {
            if (curYear.isNotEmpty()) curYear.append(",")
            curYear.append(DateUtil.getYear() - 1)
        }
        if (isSelect3) {
            if (curYear.isNotEmpty()) curYear.append(",")
            curYear.append(DateUtil.getYear())
        }
        presenter.getData(curYear.toString(), companyID ?: "", companyType, companyZone_id)
        textView.run {
            if (isSelect) {
                setBackgroundResource(R.color.font_home)
                setTextColor(resources.getColor(R.color.white))
            } else {
                setBackgroundResource(R.color.transparent)
                setTextColor(resources.getColor(R.color.font_c3))
            }
        }
    }

    private fun showChooseType(context: Context, stringList: List<ChooseType>) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_store, null)
        val popWindow = Popup7(view, Utils.dip2px(activity, 180f), ViewGroup.LayoutParams.WRAP_CONTENT)
        val adapter = object : CommonAdapter<ChooseType>(context, stringList, R.layout.item_store_list) {
            override fun convert(holder: BaseViewHolder, chooseType: ChooseType, position: Int) {
                holder.setText(R.id.tv_type, chooseType.content)
                holder.setBackgroundRes(R.id.tv_type, R.drawable.store_clicked)
                holder.setOnClickListener(R.id.tv_type) {
                    if (popWindow.isShowing) {
                        popWindow.dismiss()
                    }
                    tv_store_name.setText(chooseType.content)
                    companyID = chooseType.ids
                    presenter.getData(curYear.toString(), companyID
                            ?: "", companyType, companyZone_id)
                }
            }
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycle_store)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        popWindow.isFocusable = true
        popWindow.isOutsideTouchable = true
        popWindow.setBackgroundDrawable(BitmapDrawable())
        popWindow.showAsDropDown(rank_choose)
    }
}