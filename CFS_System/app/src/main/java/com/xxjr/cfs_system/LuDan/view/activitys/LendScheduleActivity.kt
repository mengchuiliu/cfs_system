package com.xxjr.cfs_system.LuDan.view.activitys

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.adapters.UpdateScheduleAdapter
import com.xxjr.cfs_system.LuDan.presenter.UpdateSchedulePresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.UpdateScheduleVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.*
import entity.CommonItem
import entity.LoanInfo
import entity.Reimburse
import kotlinx.android.synthetic.main.activity_update_schedule.*
import java.math.BigDecimal

class LendScheduleActivity : BaseActivity<UpdateSchedulePresenter, LendScheduleActivity>(), UpdateScheduleVInter {
    private var loanInfo: LoanInfo? = null
    private var isBack: Boolean = false
    private var adapter: UpdateScheduleAdapter? = null
    private var remark = ""
    private var refresh1 = true
    private var refresh2 = true

    override fun getPresenter(): UpdateSchedulePresenter = UpdateSchedulePresenter()

    override fun getLayoutId(): Int = R.layout.activity_update_schedule

    override fun isShowBacking(): Boolean = true

    override fun getLoanInfo(): LoanInfo = loanInfo ?: LoanInfo()

    override fun getNewLend(): Boolean = true

    override fun initView(savedInstanceState: Bundle?) {
        loanInfo = intent.getSerializableExtra("loanInfo") as? LoanInfo
        toolbarTitle.text = CacheProvide.getLoanStatus(loanInfo?.scheduleId ?: 0)
        subTitle.text = "回退"
        initBtClick()
        presenter.setDefaultValue()
    }

    private fun initBtClick() {
        if (getLoanInfo().scheduleId == 1) {
            subTitle.visibility = View.GONE
        }
        subTitle.setOnClickListener {
            isBack = !isBack
            if (isBack) {
                subTitle.text = "更新"
                tv_update.text = "回退进度"
            } else {
                subTitle.text = "回退"
                tv_update.text = "更新进度"
            }
            presenter.refreshAdapterData(isBack)
        }

        tv_update.setOnClickListener {
            presenter.checkAndUpdate(isBack)
        }
    }

    override fun initRecycler(list: MutableList<CommonItem<Any>>) {
        rv_schedule.layoutManager = LinearLayoutManager(this)
        adapter = UpdateScheduleAdapter(this, list)
        adapter?.setOnItemClick(RecycleItemClickListener { position ->
            when (position) {
                1, 4 -> presenter.showTimeChoose(position)
                6 -> presenter.showReimburseType(rv_schedule)
                11 -> {//显示账单
                    when {
                        loanInfo?.interestRate ?: 0.0 <= 0.0 || loanInfo?.interestRate ?: 0.0 > 50 -> showMsg("请正确的填写年利率(0~50)")
                        loanInfo?.paymentMethod == -1 -> showMsg("请选择还款类型")
                        loanInfo?.lendingAmount ?: 0.0 < 1000 -> showMsg("放款金额不能小于1000!")
                        loanInfo?.lendingTime.isNullOrBlank() -> showMsg("放款日期不能为空!")
                        loanInfo?.returnTime.isNullOrBlank() -> showMsg("还款期限不能为空!")
                        loanInfo?.monthDate.isNullOrBlank() -> showMsg("月还款日不能为空!")
                        else -> {
                            refreshItem(0, Utils.parseTwoMoney(BigDecimal(getLoanInfo().lendingAmount)))
                            refreshItem(2, Utils.parseTwoMoney(BigDecimal(getLoanInfo().monthAmount)))
                            refreshItem(3, getLoanInfo().monthDate)
                            refreshItem(5, Utils.parseTwoMoney(BigDecimal(loanInfo?.interestRate
                                    ?: 0.0)))
                            refreshItem(7, Utils.parseTwoMoney(BigDecimal(getLoanInfo().otherAmount)))
                            if (loanInfo?.lendingPeriod ?: 0 > 0)
                                refreshItem(8, loanInfo?.lendingPeriod.toString())
                            refreshItem(9, loanInfo?.otherPayRemark ?: "")
                            refreshItem(10, loanInfo?.note ?: "")
                            val list = ReimburseFormula.getReimburseData(loanInfo?.lendingAmount
                                    ?: 0.0, loanInfo?.interestRate ?: 0.0, loanInfo?.lendingTime
                                    ?: "", loanInfo?.returnTime
                                    ?: "", (loanInfo?.monthDate
                                    ?: "0").toInt(), loanInfo?.paymentMethod ?: -1)
                            adapter?.setOtherAmount(loanInfo?.otherAmount ?: 0.0)
                            refreshItem(list)
                        }
                    }
                }
            }
            if (position != 11) refreshItem(mutableListOf())
        })
        adapter?.setTextChangeListener(TextChangeListener { position, text ->
            when (position) {
                9 -> if (!text.isNullOrBlank()) loanInfo?.otherPayRemark = text
                10 -> remark = text ?: ""
            }
        })

        adapter?.setTextEditChangeListener(TextChangeListener { position, text ->
            when (position) {
                0 -> {
                    refresh1 = true
                    if (!text.isNullOrBlank()) loanInfo?.lendingAmount = Utils.getBigLong(text)
                }
                2 -> if (!text.isNullOrBlank()) loanInfo?.monthAmount = Utils.getBigLong(text)
                3 -> {
                    refresh2 = true
                    loanInfo?.monthDate = text
                }
                5 -> {
                    refresh2 = true
                    if (!text.isNullOrBlank() && text.indexOf(".") != 0) {
                        loanInfo?.interestRate = Utils.getBigLong(text)//年利率
                    }
                }
                7 -> {
                    refresh2 = true
                    if (!text.isNullOrBlank() && text.indexOf(".") != 0) {
                        loanInfo?.otherAmount = Utils.getBigLong(text)//其他月供
                    }
                }
                8 -> {
                    refresh2 = true
                    if (!text.isNullOrBlank()) {
                        if (text.toInt() in 1..360) {
                            loanInfo?.lendingPeriod = text.toInt()
                            if (!loanInfo?.lendingTime.isNullOrBlank()) {
                                refreshItem(4, ReimburseFormula.getLateMonth(loanInfo?.lendingTime
                                        ?: "", text.toInt(), "yyyy-MM-dd", "yyyy-MM-dd", 0))
                            }
                        } else {
                            loanInfo?.lendingPeriod = 0
                            showMsg("请正确的填写还款期数(1~360)")
                            refreshItem(8, "")
                        }
                    }
                }
            }
            refreshItem(mutableListOf())
        })
        rv_schedule.adapter = adapter

        rv_schedule.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                val layoutManager = recyclerView?.layoutManager as? LinearLayoutManager
                val firstVisibleItemPosition = layoutManager?.findFirstVisibleItemPosition()
                        ?: 0
                when {
                    refresh1 && firstVisibleItemPosition > 2 -> {
                        refresh1 = false
                        refreshItem(0, Utils.parseTwoMoney(BigDecimal(loanInfo?.lendingAmount
                                ?: 0.0)))
                        refreshItem(2, Utils.parseTwoMoney(BigDecimal(loanInfo?.monthAmount
                                ?: 0.0)))
                    }
                    refresh2 && firstVisibleItemPosition > 5 -> {
                        refresh2 = false
                        refreshItem(3, loanInfo?.monthDate ?: "")
                        refreshItem(5, Utils.parseTwoMoney(BigDecimal(loanInfo?.interestRate
                                ?: 0.0)))
                        refreshItem(7, Utils.parseTwoMoney(BigDecimal(loanInfo?.otherAmount
                                ?: 0.0)))
                        if (loanInfo?.lendingPeriod ?: 0 > 0)
                            refreshItem(8, loanInfo?.lendingPeriod.toString())
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

    override fun refreshItem(position: Int, show: Boolean) {
        val item: CommonItem<*> = adapter?.datas?.get(position) as CommonItem<*>
        item.isEnable = show
        adapter?.notifyItemChanged(position, item)
    }

    override fun refreshAdapter(list: MutableList<CommonItem<Any>>) {
        adapter?.setNewData(list as List<Any>?)
    }

    fun refreshItem(reimburses: MutableList<Reimburse>) {
        val item: CommonItem<*> = adapter?.datas?.get(adapter?.datas?.size!! - 1) as CommonItem<*>
        item.list = reimburses
        adapter?.notifyItemChanged(adapter?.datas?.size!! - 1, item)
    }

    override fun getRemark(): String = remark

    override fun complete() {
        RxBus.getInstance().post(Constants.POST_REFRESH_MY_TASK, true)
        showMsg("进度更新成功!")
        setResult(Constants.REQUEST_CODE_UPDATE_SCHEDULE)
        this@LendScheduleActivity.finish()
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)
}
