package com.xxjr.cfs_system.LuDan.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.VisitFPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.VisitDetailActivity
import com.xxjr.cfs_system.LuDan.view.viewinter.VisitFVInter
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.fragment_visit.*
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

/**
 * Created by Administrator on 2017/12/12.
 */
@SuppressLint("SetTextI18n")
class VisitFragment : BaseFragment(), VisitFVInter, View.OnClickListener {
    val presenter = VisitFPresenter()
    private var curYear = DateUtil.getYear()
    private var curMonth = DateUtil.getMonth()
    private var companyType = ""
    private var companyZone_id = ""//区域类型
//    private var curMonth = 1

    override fun getFrgContext(): Context = context

    override fun showMsg(msg: String?) = ToastShow.showShort(activity.applicationContext, msg)

    override fun initView(inflater: LayoutInflater, savedInstanceState: Bundle?, arguments: Bundle): View {
        presenter.attach(this)
        return inflater.inflate(R.layout.fragment_visit, null)
    }

    fun initData(companyT: String, CompanyZone_id: String) {
        firstUp = false
        companyType = companyT
        companyZone_id = CompanyZone_id
        tv_month_3.text = "${DateUtil.getMonth()}月"
        tv_month_2.text = "${when (DateUtil.getMonth() - 1) {
            0 -> 12
            else -> DateUtil.getMonth() - 1
        }}月"
        tv_month_1.text = "${when (DateUtil.getMonth() - 2) {
            0 -> 12
            -1 -> 11
            else -> DateUtil.getMonth() - 2
        }}月"
        tv_month_1.setOnClickListener(this)
        tv_month_2.setOnClickListener(this)
        tv_month_3.setOnClickListener(this)
        presenter.getStoreVisitDatas(curYear, curMonth, companyType, companyZone_id)
    }

    override fun initRv(commonItems: MutableList<CommonItem<Any>>) {
        rv_visit.layoutManager = LinearLayoutManager(context)
        rv_visit.adapter = object : CommonAdapter<CommonItem<Any>>(context, commonItems, R.layout.item_progress_show) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.setVisible(R.id.progress, false)
                val bar = holder.getView<ProgressBar>(R.id.progress_visit)
                bar.visibility = View.VISIBLE
                if (commonItems.size > 1) {
                    bar.max = commonItems[1].position
                }
                holder.setText(R.id.tv_rank_name, "${item.remark ?: ""}${item.name}")
                val textview = holder.getView<TextView>(R.id.tv_amount)
                textview.gravity = Gravity.CENTER
                holder.setVisible(R.id.tv_amount, true)
                if (item.isLineShow) {
                    bar.visibility = View.INVISIBLE
                    holder.setText(R.id.tv_amount, item.content)
                } else {
                    bar.visibility = View.VISIBLE
                    bar.progress = item.position
                    holder.setText(R.id.tv_amount, "${item.position}")
                    holder.convertView.setOnClickListener {
                        val intent1 = Intent(context, VisitDetailActivity::class.java)
                        intent1.putExtra("CompanyId", item.remark)
                        intent1.putExtra("CompanyName", item.name)
                        intent1.putExtra("year", curYear)
                        intent1.putExtra("month", curMonth)
                        startActivity(intent1)
                    }
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            tv_month_1 -> {
                when (DateUtil.getMonth() - 2) {
                    0 -> {
                        curMonth = 12
                        curYear = DateUtil.getYear() - 1
                    }
                    -1 -> {
                        curMonth = 11
                        curYear = DateUtil.getYear() - 1
                    }
                    else -> {
                        curMonth = DateUtil.getMonth() - 2
                    }
                }
                showMonth(tv_month_1, tv_month_2, tv_month_3)
            }
            tv_month_2 -> {
                when (DateUtil.getMonth() - 1) {
                    0 -> {
                        curMonth = 12
                        curYear = DateUtil.getYear() - 1
                    }
                    else -> curMonth = DateUtil.getMonth() - 1
                }
                showMonth(tv_month_2, tv_month_1, tv_month_3)
            }
            tv_month_3 -> {
                curMonth = DateUtil.getMonth()
                curYear = DateUtil.getYear()
                showMonth(tv_month_3, tv_month_2, tv_month_1)
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
        presenter.getStoreVisitDatas(curYear, curMonth, companyType, companyZone_id)
    }

    override fun onDestroy() {
        presenter.deAttach()
        super.onDestroy()
    }
}