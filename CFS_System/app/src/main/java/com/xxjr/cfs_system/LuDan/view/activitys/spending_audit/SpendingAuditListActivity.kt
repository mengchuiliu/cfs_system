package com.xxjr.cfs_system.LuDan.view.activitys.spending_audit

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.alibaba.fastjson.JSONObject
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.presenter.SpendingAuditLP
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.BaseListActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.CommonItem
import entity.SpendingInfo
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import rx.Subscription

class SpendingAuditListActivity : BaseListActivity<SpendingAuditLP, SpendingAuditListActivity>(), BaseViewInter {
    private var companySubscription: Subscription? = null//门店
    var auditPos = 0 //审核状态位置
    var spendingInfo = mutableListOf<SpendingInfo>()

    override fun getListPresenter(): SpendingAuditLP = SpendingAuditLP()

    @SuppressLint("ClickableViewAccessibility")
    override fun initAdapter() {
        companySubscription = RxBus.getInstance().toObservable(Constants.Company_Choose, ChooseType::class.java)
                .subscribe { chooseType ->
                    searchCompanyId = chooseType.ids
                    etPactSearch.setText(chooseType.content)
                    page = 0
                    isPull = false
                    refreshData(page, searchType)
                }
        tvSearchType.visibility = View.GONE
        etPactSearch.hint = "请输入门店名称查询"
        auditPos = intent.getIntExtra("AuditPos", 0)
        adapter = object : CommonAdapter<SpendingInfo>(this@SpendingAuditListActivity, arrayListOf(), R.layout.item_spending_audit) {
            override fun convert(holder: BaseViewHolder, item: SpendingInfo, position: Int) {
                holder.setText(R.id.tv_spend, when (item.ExpenseItem) {
                    0 -> "办公场地"
                    1 -> "设备机房"
                    2 -> "房屋综合税"
                    3 -> "印花税"
                    4 -> "物业费"
                    5 -> "水电费"
                    6 -> "物业费+水电费"
                    else -> ""
                })
                holder.setText(R.id.tv_company, item.Company ?: "")
                holder.setText(R.id.tv_period, "${Utils.FormatTime(item.StartDate
                        ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")}—${Utils.FormatTime(item.EndDate
                        ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")}")
                holder.setText(R.id.tv_amount, "${item.Amount}元")
                holder.setText(R.id.tv_schedule, when (item.AuditState) {
                    1 -> "待区域主管审核"
                    2 -> "待总部财务审核"
                    -1 -> "待出纳付款"
                    -3 -> "已付款"
                    -2 -> "已拒绝"
                    else -> ""
                })
                holder.setText(R.id.tv_pay_date, Utils.FormatTime(item.PayDate
                        ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd"))
                holder.convertView.setOnClickListener {
                    val intent = Intent(this@SpendingAuditListActivity, SpendingDetailActivity::class.java)
                    intent.putExtra("spendingDetail", item)
                    startActivityForResult(intent, 99)
                }
            }
        }

        adapter0 = object : CommonAdapter<CommonItem<*>>(this@SpendingAuditListActivity, presenter.getTitles(JSONObject(), auditPos), R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
                holder.setText(R.id.tv_title, item.name)
                holder.setTextSize(R.id.tv_title, 15f)
                val textview = holder.getView<TextView>(R.id.tv_title)
                textview.setPadding(Utils.dip2px(this@SpendingAuditListActivity, 10f),
                        Utils.dip2px(this@SpendingAuditListActivity, 8f),
                        Utils.dip2px(this@SpendingAuditListActivity, 10f),
                        Utils.dip2px(this@SpendingAuditListActivity, 8f))
                if (item.isClick) {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_home)
                    holder.setVisible(R.id.tv_line, true)
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_c6)
                    holder.setVisible(R.id.tv_line, false)
                }
                holder.convertView.setOnClickListener { refreshTitle0(position) }
            }
        }
    }


    fun refreshTitleData0(commonItems: List<CommonItem<*>>) {
        adapter0.setNewData(commonItems)
    }

    override fun refreshData(page: Int, searchType: Int) {
        presenter.refreshSpendingData(page, auditPos)
    }

    override fun refreshChange() {
        adapter.setNewData(spendingInfo)
    }

    private fun refreshTitle0(position: Int) {
        auditPos = position
        isPull = false
        page = 0
        for (i in 0 until adapter0.datas.size) {
            (adapter0.datas[i] as CommonItem<*>).isClick = i == position
        }
        adapter0.notifyDataSetChanged()
        presenter.refreshSpendingData(page, auditPos)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 99) {
            isPull = false
            refreshData(0, 0)
        }
    }

    override fun onDestroy() {
        if (companySubscription != null && !companySubscription!!.isUnsubscribed) {
            companySubscription!!.unsubscribe()
        }
        super.onDestroy()
    }
}
