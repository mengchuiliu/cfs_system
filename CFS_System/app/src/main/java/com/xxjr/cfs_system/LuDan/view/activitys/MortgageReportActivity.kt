package com.xxjr.cfs_system.LuDan.view.activitys

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.MortagageRPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.MortagageRVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_mortgage_report.*
import refresh_recyclerview.DividerItemDecoration
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import java.util.*

class MortgageReportActivity : BaseActivity<MortagageRPresenter, MortagageRVInter>(), MortagageRVInter, View.OnClickListener {
    private var adapter: CommonAdapter<CommonItem<Any>>? = null

    override fun getPresenter(): MortagageRPresenter = MortagageRPresenter()

    override fun getLayoutId(): Int = R.layout.activity_mortgage_report

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "按揭统计"
        tv_date.text = Utils.FormatTime(DateUtil.getFormatDate(Date()), "yyyy-MM-dd", "yyyy/MM/dd")
        iv_date.setOnClickListener(this)
        tv_date.setOnClickListener(this)
        tv_store.setOnClickListener(this)
        tv_mortgage.setOnClickListener(this)
        if (Hawk.get<String>("UserType") == "22") {
            tv_amount.visibility = View.VISIBLE
            ll_manager.visibility = View.GONE
            tv_amount.text = "放款总金额：0"
        } else {
            tv_amount.visibility = View.GONE
            ll_manager.visibility = View.VISIBLE
            subTitle.text = "地区"
            subTitle.setOnClickListener { presenter.showZone(subTitle) }
        }
        initRv()
        presenter.setDefaultValue()
    }

    override fun setZoneText(zoneName: String) {
        subTitle.text = zoneName
    }

    override fun initRv() {
        rv_data.layoutManager = LinearLayoutManager(this@MortgageReportActivity)
        rv_data.addItemDecoration(DividerItemDecoration(this@MortgageReportActivity, DividerItemDecoration.VERTICAL_LIST))
        adapter = object : CommonAdapter<CommonItem<Any>>(this@MortgageReportActivity, ArrayList(), R.layout.item_mortgage_report) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.setText(R.id.tv_name, item.name)
                if (Hawk.get<String>("UserType") == "22") {
                    holder.setVisible(R.id.tv_customer, true)
                    holder.setText(R.id.tv_customer, item.remark)
                }
                holder.setText(R.id.apply_amount, item.content)
                holder.setText(R.id.lend_amount, item.hintContent)
            }
        }
        rv_data.adapter = adapter
    }

    override fun refreshData(commonItems: MutableList<CommonItem<Any>>) {
        if (commonItems.isNotEmpty()) {
            rv_data.visibility = View.VISIBLE
            rl_nodata.visibility = View.GONE
        } else {
            rv_data.visibility = View.GONE
            rl_nodata.visibility = View.VISIBLE
        }
        if (commonItems.isNotEmpty()) {
            var sum = 0.0
            for (common in commonItems) {
                sum += common.percent
            }
            tv_amount.text = "放款总金额：${Utils.div(sum)}万"
            rv_data.visibility = View.VISIBLE
            rl_nodata.visibility = View.GONE
        } else {
            tv_amount.text = "放款总金额：0"
            rv_data.visibility = View.GONE
            rl_nodata.visibility = View.VISIBLE
        }
        adapter?.setNewData(commonItems)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            iv_date, tv_date -> presenter.timeShow(tv_date)
            tv_store -> {
                presenter.setType(0)
                tv_store.setTextColor(resources.getColor(R.color.font_home))
                tv_mortgage.setTextColor(resources.getColor(R.color.font_c6))
                tv_store_line.visibility = View.VISIBLE
                tv_mortgage_line.visibility = View.GONE
            }
            tv_mortgage -> {
                presenter.setType(1)
                tv_mortgage.setTextColor(resources.getColor(R.color.font_home))
                tv_store.setTextColor(resources.getColor(R.color.font_c6))
                tv_mortgage_line.visibility = View.VISIBLE
                tv_store_line.visibility = View.GONE
            }
        }
    }

    override fun isShowBacking(): Boolean = true

    override fun showMsg(msg: String?) = ToastShow.showShort(application, msg)

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }
}
