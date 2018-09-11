package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xiaoxiao.widgets.SwipeMenuLayout
import com.xxjr.cfs_system.LuDan.presenter.CostListPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.CostVInter
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.CommonItem
import entity.Cost
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import rx.Subscription
import rx.functions.Action1
import java.math.BigDecimal
import java.util.ArrayList

class CostListActivity : BaseListActivity<CostListPresenter, CostVInter>(), CostVInter {
    private var companySubscription: Subscription? = null//门店
    private val loanInfos = ArrayList<Cost>()//贷款列表
    private var schedulePos = 0 //状态位置
    private var auditPos = 0 //审核状态位置

    override fun getCost(): MutableList<Cost> = loanInfos

    fun getAuditPos(): Int = auditPos

    fun getSchedulePos(): Int = schedulePos

    override fun getListPresenter(): CostListPresenter = CostListPresenter()

    override fun initAdapter() {
        companySubscription = RxBus.getInstance().toObservable(Constants.Company_Choose, ChooseType::class.java)
                .subscribe { chooseType ->
                    searchCompanyId = chooseType.ids
                    etPactSearch.setText(chooseType.content)
                    page = 0
                    isPull = false
                    refreshData(page, searchType)
                }
        val jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanBankType")))
        val jsonArrayLoan = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoansType"), ""))
        schedulePos = intent.getIntExtra("schedulePos", 0)
        auditPos = intent.getIntExtra("AuditPos", 0)
        adapter = object : CommonAdapter<Cost>(this@CostListActivity, ArrayList(), R.layout.item_cost) {
            override fun convert(holder: BaseViewHolder, cost: Cost, position: Int) {
                val swipeMenuLayout = (holder.convertView as SwipeMenuLayout).setIos(false).setLeftSwipe(true)
                swipeMenuLayout.isSwipeEnable = false
                holder.setText(R.id.tv_loan_numb, cost.loanCode)
                holder.setText(R.id.tv_date, cost.clerkName)
                holder.setText(R.id.tv_customer, "${cost.customerName}【${CacheProvide.getBank(jsonArray, cost.bankId
                        ?: 0)}·${CacheProvide.getLoanType(jsonArrayLoan, cost.loanType ?: 0)}】")
                holder.setText(R.id.tv_cost_type, "${CacheProvide.getCostType(cost.costType)}：")
                holder.setText(R.id.tv_amount, "${Utils.parseMoney(BigDecimal(cost.money))}元")
                holder.setText(R.id.tv_insert_date, Utils.getTime(cost.happenDate ?: ""))
                holder.setText(R.id.tv_audit_state, when (cost.auditStatus) {
                    0 -> "未审核"
                    1 -> "门店经理审核通过"
                    2 -> "门店经理审核拒绝"
                    3 -> "会计审核通过"
                    4 -> "按揭经理审核拒绝"
                    5 -> "总部财务添加"
                    6 -> "按揭经理审核通过"
                    7 -> "财务会计审核拒绝"
                    8 -> "特定人审核通过"
                    9 -> "特定人审核拒绝"
                    else -> ""
                })
                holder.setOnClickListener(R.id.ll_home) {
                    val intent = Intent(this@CostListActivity, AuditCostActivity::class.java)
                    intent.putExtra("loanInfo", cost)
                    startActivityForResult(intent, 99)
                }
            }
        }

        adapter0 = object : CommonAdapter<CommonItem<*>>(this@CostListActivity, presenter.getTitles(schedulePos), R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
                holder.setText(R.id.tv_title, item.name)
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

        adapter1 = object : CommonAdapter<CommonItem<*>>(this@CostListActivity, presenter.getTitles1(auditPos), R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
                holder.setText(R.id.tv_title, item.name)
                if (item.isClick) {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_home)
                    holder.setVisible(R.id.tv_line, true)
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_c6)
                    holder.setVisible(R.id.tv_line, false)
                }
                holder.convertView.setOnClickListener { refreshTitle1(position) }
            }
        }
    }

    fun showChooseItem() {
        if (auditPos > 2) {
            (rvTitle1.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(auditPos, 0)
        }
    }

    private fun refreshTitle0(position: Int) {
        schedulePos = position
        isPull = false
        page = 0
        searchType = 0
        for (i in 0 until adapter0.datas.size) {
            (adapter0.datas[i] as CommonItem<*>).isClick = i == position
        }
        adapter0.notifyDataSetChanged()
        presenter.costRefresh(page, searchType, schedulePos, auditPos)
    }

    private fun refreshTitle1(position: Int) {
        auditPos = position
        isPull = false
        page = 0
        searchType = 0
        for (i in 0 until adapter1.datas.size) {
            (adapter1.datas[i] as CommonItem<*>).isClick = i == position
        }
        adapter1.notifyDataSetChanged()
        presenter.costRefresh(page, searchType, schedulePos, auditPos)
    }

    override fun refreshChange() {
        adapter.setNewData(loanInfos)
        adapter.notifyDataSetChanged()
    }

    fun refreshTitleData0(commonItems: List<CommonItem<*>>) {
        adapter0.setNewData(commonItems)
    }

    fun refreshTitleData1(commonItems: List<CommonItem<*>>) {
        adapter1.setNewData(commonItems)
    }

    override fun refreshData(page: Int, searchType: Int) {
        presenter.costRefresh(page, searchType, schedulePos, auditPos)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 99 && requestCode == 99) {
            isPull = false
            refreshData(0, searchType)
        }
    }

    override fun onDestroy() {
        if (companySubscription != null && companySubscription!!.isUnsubscribed) {
            companySubscription!!.unsubscribe()
        }
        super.onDestroy()
    }
}
