package com.xxjr.cfs_system.LuDan.view.activitys

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.adapters.UpdateScheduleAdapter
import com.xxjr.cfs_system.LuDan.presenter.ManyLendPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.ManyLendVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ReimburseFormula
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.LoanInfo
import entity.Reimburse
import kotlinx.android.synthetic.main.activity_add_loan.*
import java.math.BigDecimal

class ManyLendActivity : BaseActivity<ManyLendPresenter, ManyLendVInter>(), ManyLendVInter {
    private lateinit var loanInfo: LoanInfo
    private var adapter: UpdateScheduleAdapter? = null
    private var refresh1 = true
    private var refresh2 = true
    private var isUpdate = false

    override fun getPresenter(): ManyLendPresenter = ManyLendPresenter()

    override fun getLayoutId(): Int = R.layout.activity_add_loan

    override fun getLoanInfo(): LoanInfo = loanInfo

    override fun isShowBacking(): Boolean = true

    override fun initView(savedInstanceState: Bundle?) {
        loanInfo = (intent.getSerializableExtra("LoanInfo") as? LoanInfo) ?: LoanInfo()
        isUpdate = intent.getBooleanExtra("isUpdate", false)
        if (!isUpdate) {
            loanInfo.loanId = intent.getStringExtra("loanId")
            loanInfo.loanCode = intent.getStringExtra("loanCode")
        }
        toolbarTitle.text = if (isUpdate) "修改放款" else "新增放款"
        subTitle.text = if (isUpdate) "修改" else "保存"
        presenter.setDefaultValue()

        subTitle.setOnClickListener {
            presenter.saveData(isUpdate)
        }
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initRecycler(list: MutableList<CommonItem<Any>>) {
        recycler_add_loan.layoutManager = LinearLayoutManager(this)
        adapter = UpdateScheduleAdapter(this, list)
        adapter?.setTextChangeListener(TextChangeListener { position, text ->
            when (position) {
                9 -> loanInfo.otherPayRemark = text
                10 -> loanInfo.note = text
            }
        })

        adapter?.setOnItemClick(RecycleItemClickListener { position ->
            when (position) {
                1, 4 -> presenter.showTimeChoose(position)
                6 -> presenter.showReimburseType(recycler_add_loan)
                11 -> {//显示账单
                    when {
                        loanInfo.interestRate <= 0.0 || loanInfo.interestRate > 100 -> showMsg("请正确的填写年利率(0~100)")
                        loanInfo.paymentMethod == -1 -> showMsg("请选择还款类型")
                        loanInfo.lendingAmount < 1000 -> showMsg("放款金额不能小于1000!")
                        loanInfo.lendingTime.isNullOrBlank() -> showMsg("放款日期不能为空!")
                        loanInfo.returnTime.isNullOrBlank() -> showMsg("还款期限不能为空!")
                        loanInfo.monthDate.isNullOrBlank() -> showMsg("月还款日不能为空!")
                        else -> {
                            refreshItem(0, Utils.parseTwoMoney(BigDecimal(loanInfo.lendingAmount)))
                            refreshItem(2, Utils.parseTwoMoney(BigDecimal(loanInfo.monthAmount)))
                            refreshItem(3, loanInfo.monthDate)
                            refreshItem(5, Utils.parseTwoMoney(BigDecimal(loanInfo.interestRate)))
                            refreshItem(7, Utils.parseTwoMoney(BigDecimal(loanInfo.otherAmount)))
                            if (loanInfo.lendingPeriod > 0)
                                refreshItem(8, loanInfo.lendingPeriod.toString())
                            refreshItem(9, loanInfo.otherPayRemark ?: "")
                            refreshItem(10, loanInfo.note ?: "")
                            val list = ReimburseFormula.getReimburseData(loanInfo.lendingAmount, loanInfo.interestRate, loanInfo.lendingTime
                                    ?: "", loanInfo.returnTime
                                    ?: "", (loanInfo.monthDate
                                    ?: "0").toInt(), loanInfo.paymentMethod)
                            adapter?.setOtherAmount(loanInfo.otherAmount)
                            refreshItem(list)
                        }
                    }
                }
            }
            if (position != 11) refreshItem(mutableListOf())
        })

        adapter?.setTextEditChangeListener(TextChangeListener { position, text ->
            when (position) {
                0 -> {
                    refresh1 = true
                    if (!text.isNullOrBlank()) loanInfo.lendingAmount = Utils.getBigLong(text)
                }
                2 -> if (!text.isNullOrBlank()) loanInfo.monthAmount = Utils.getBigLong(text)
                3 -> {
                    refresh2 = true
                    loanInfo.monthDate = text
                }
                5 -> {
                    refresh2 = true
                    if (!text.isNullOrBlank() && text.indexOf(".") != 0) {
                        loanInfo.interestRate = Utils.getBigLong(text)//年利率
                    }
                }
                7 -> {
                    refresh2 = true
                    if (!text.isNullOrBlank() && text.indexOf(".") != 0) {
                        loanInfo.otherAmount = Utils.getBigLong(text)//其他月供
                    }
                }
                8 -> {
                    refresh2 = true
                    if (!text.isNullOrBlank()) {
                        if (text.toInt() in 1..360) {
                            loanInfo.lendingPeriod = text.toInt()
                            if (!loanInfo.lendingTime.isNullOrBlank()) {
                                refreshItem(4, ReimburseFormula.getLateMonth(loanInfo.lendingTime
                                        ?: "", text.toInt(), "yyyy-MM-dd", "yyyy-MM-dd", 0))
                            }
                        } else {
                            loanInfo.lendingPeriod = 0
                            showMsg("请正确的填写还款期数(1~360)")
                            refreshItem(8, "")
                        }
                    }
                }
            }
            refreshItem(mutableListOf())
        })
        recycler_add_loan.adapter = adapter

        recycler_add_loan.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                val layoutManager = recyclerView?.layoutManager as? LinearLayoutManager
                val firstVisibleItemPosition = layoutManager?.findFirstVisibleItemPosition()
                        ?: 0
                when {
                    refresh1 && firstVisibleItemPosition > 2 -> {
                        refresh1 = false
                        refreshItem(0, Utils.parseTwoMoney(BigDecimal(loanInfo.lendingAmount)))
                        refreshItem(2, Utils.parseTwoMoney(BigDecimal(loanInfo.monthAmount)))
                    }
                    refresh2 && firstVisibleItemPosition > 5 -> {
                        refresh2 = false
                        refreshItem(3, loanInfo.monthDate)
                        refreshItem(5, Utils.parseTwoMoney(BigDecimal(loanInfo.interestRate)))
                        refreshItem(7, Utils.parseTwoMoney(BigDecimal(loanInfo.otherAmount)))
                        if (loanInfo.lendingPeriod > 0)
                            refreshItem(8, loanInfo.lendingPeriod.toString())
                    }
                }
            }
        })
    }

    override fun refreshItem(position: Int, text: String) {
        val item: CommonItem<*> = adapter?.datas?.get(position) as CommonItem<*>
        item.content = text
        adapter?.notifyItemChanged(position, item)
    }

    fun refreshItem(reimburses: MutableList<Reimburse>) {
        val item: CommonItem<*> = adapter?.datas?.get(adapter?.datas?.size!! - 1) as CommonItem<*>
        item.list = reimburses
        adapter?.notifyItemChanged(adapter?.datas?.size!! - 1, item)
    }

    override fun complete() {
        if (isUpdate) {
            setResult(99)
            showMsg("修改成功!")
        } else {
            setResult(88)
            showMsg("保存成功!")
        }
        this@ManyLendActivity.finish()
    }

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }
}
