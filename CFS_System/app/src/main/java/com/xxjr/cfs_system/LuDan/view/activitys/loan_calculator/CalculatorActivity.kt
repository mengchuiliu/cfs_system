package com.xxjr.cfs_system.LuDan.view.activitys.loan_calculator

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.flyco.tablayout.listener.OnTabSelectListener
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.CalculatorAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.presenter.CalculatorPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.ReimburseFormula
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_calculator.*

class CalculatorActivity : BaseActivity<CalculatorPresenter, CalculatorActivity>(), BaseViewInter {
    private var commonItemData = mutableListOf<CommonItem<Any>>()
    private var loanType = 0//0,1->等额本金等额本息  2->到期还本
    private var amount = 0.0
    private var otherAmount = 0.0
    private var rate = 0.0
    private var months = 0
    private var startData: String = ""
    private var endData: String = ""
    private var reimburseType = -1// 0 ->"按月还息"  1 ->"按季还息" 2 ->"到期还本"
    private lateinit var adapter: CalculatorAdapter

    override fun getPresenter(): CalculatorPresenter = CalculatorPresenter()

    override fun getLayoutId(): Int = R.layout.activity_calculator

    fun setReimburseType(type: Int) {
        reimburseType = type
    }

    override fun initView(savedInstanceState: Bundle?) {
        commonItemData = presenter.getItemData(0, amount, months, rate, otherAmount)
        iv_back.setOnClickListener { finish() }
        tab_layout.setTabData(presenter.getTabTitle().toTypedArray())
        tab_layout.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                loanType = position
                months = 0
                startData = ""
                endData = ""
                reimburseType = -1
                commonItemData.clear()
                commonItemData = presenter.getItemData(position, amount, months, rate, otherAmount)
                refreshData(commonItemData)
            }

            override fun onTabReselect(position: Int) {//重复点击
            }
        })
        initRV()
        presenter.setDefaultValue()
    }

    private fun initRV() {
        rv_calculator.layoutManager = LinearLayoutManager(this@CalculatorActivity)
        adapter = CalculatorAdapter(this@CalculatorActivity, commonItemData)
        adapter.setOnItemClick(RecycleItemClickListener { position ->
            when (position) {
                14 -> startActivity(InterestListActivity::class.java) //贷款利率表
                15 -> {//开始计算
                    when {
                        amount == 0.0 -> {
                            showMsg("请正确的填写金额")
                            return@RecycleItemClickListener
                        }
                        rate == 0.0 || rate > 50 -> {
                            showMsg("请正确的填写利率(0~50)")
                            return@RecycleItemClickListener
                        }
                    }
                    when (loanType) {
                        0, 1 -> {
                            if (months < 1 || months > 360) {
                                showMsg("请正确的填写贷款期限（1~360）")
                            } else {
                                val list = when (loanType) {
                                    0 -> ReimburseFormula.getEqualInterest(amount * 10000, rate, months)
                                    1 -> ReimburseFormula.getEqualAmountData(amount * 10000, rate, months)
                                    else -> ReimburseFormula.getEqualInterest(amount * 10000, rate, months)
                                }
                                refreshData(presenter.getDetailsItem(commonItemData, list, otherAmount))
                            }
                        }
                        2 -> {
                            when {
                                reimburseType == -1 -> showMsg("请选择还息方式")
                                startData.isBlank() -> showMsg("请选择借款日期")
                                endData.isBlank() -> showMsg("请选择还款日期")
                                DateUtil.getTimeLong(endData) <= DateUtil.getTimeLong(startData) -> {
                                    showMsg("还款期限不能小于借款日期!")
                                }
                                else -> {
                                    val list = when (reimburseType) {
                                        0 -> ReimburseFormula.getMonthPriorityInterest(amount * 10000, rate, startData, endData, 0)//按月
                                        1 -> ReimburseFormula.getQuarterPriorityInterest(amount * 10000, rate, startData, endData, 0)//按季
                                        2 -> ReimburseFormula.getDuePriorityInterest(amount * 10000, rate, startData, endData)//到期
                                        else -> ReimburseFormula.getMonthPriorityInterest(amount * 10000, rate, startData, endData, 0)
                                    }
                                    refreshData(presenter.getDetailsItem(commonItemData, list, otherAmount))
                                }
                            }
                        }
                    }
                }
                16 -> {
                    hideSoftInput(rv_calculator)
                    presenter.timeShow(1)
                }//借款日期
                17 -> {
                    hideSoftInput(rv_calculator)
                    presenter.timeShow(2)
                } //还款日期
                18 -> presenter.showReimburseType(rv_calculator, 5)//还息方式
            }
        })

        adapter.setTextChangeListener(TextChangeListener { position, text ->
            if (!text.isNullOrBlank() && text.indexOf(".") != 0) {
                when (position) {
                    11 -> {//贷款金额
                        amount = text.toDouble()
                        commonItemData[0].content = text
                    }
                    12 -> {//还款月份
                        months = text.toInt()
                        commonItemData[1].content = text
                    }
                    13 -> {//年利率
                        rate = text.toDouble()
                        if (loanType == 2) commonItemData[3].content = text else commonItemData[2].content = text

                    }
                    19 -> {//其他月供
                        otherAmount = text.toDouble()
                        if (loanType == 2) commonItemData[4].content = text else commonItemData[3].content = text
                    }
                }
            } else {
                when (position) {
                    11 -> amount = 0.0
                    12 -> months = 0
                    13 -> rate = 0.0
                    19 -> {//其他月供
                        otherAmount = 0.0
                        if (loanType == 2) commonItemData[4].content = "0" else commonItemData[3].content = "0"
                    }
                }
            }
        })
        rv_calculator.adapter = adapter
    }

    fun hideSoftInput(v: View) {
        val imm = this@CalculatorActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    fun refreshData(items: MutableList<CommonItem<Any>>) {
        adapter.setNewData(items as List<Any>?)
        hideSoftInput(rv_calculator)
    }

    fun refreshItem(content: String, pos: Int) {
        when (pos) {
            1 -> startData = content
            2 -> endData = content
        }
        commonItemData[pos].content = content
        val commonItem: CommonItem<*> = adapter.datas[pos] as CommonItem<*>
        commonItem.content = content
        adapter.notifyItemChanged(pos, commonItem)
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }
}
