package com.xxjr.cfs_system.LuDan.view.activitys.lending_list

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.RemindDetailAdapter
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.ManyLendActivity
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.LoanInfo
import kotlinx.android.synthetic.main.activity_common_list.*
import java.math.BigDecimal

class LendingDetailActivity : BaseActivity<BasePresenter<*, *>, BaseViewInter>() {
    private lateinit var adapter: RemindDetailAdapter
    private lateinit var loanInfo: LoanInfo

    override fun getPresenter(): BasePresenter<*, *>? = null

    override fun getLayoutId(): Int = R.layout.activity_common_list

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "放款详情"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        loanInfo = (intent.getSerializableExtra("LoanInfo") as? LoanInfo) ?: LoanInfo()
        initRV()
    }

    private fun initRV() {
        rv_remind.layoutManager = LinearLayoutManager(this@LendingDetailActivity)
        adapter = RemindDetailAdapter(this@LendingDetailActivity, getItemData())
        adapter.setOnItemShrink(RecycleItemClickListener {
            val intent = Intent(this@LendingDetailActivity, ManyLendActivity::class.java)
            intent.putExtra("LoanInfo", loanInfo)
            intent.putExtra("isUpdate", true)
            startActivityForResult(intent, 99)
        })
        rv_remind.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 99) {
            setResult(99)
            finish()
        }
    }

    private fun getItemData() = mutableListOf<CommonItem<Any>>().apply {
        for (i in 0..20) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> {
                        type = 5
                        icon = R.mipmap.icon_loan
                        name = loanInfo.loanDescription
                    }
                    1 -> {
                        type = 2
                        name = "贷款编号      "
                        content = loanInfo.loanCode ?: ""
                    }
                    2 -> {
                        type = 2
                        name = "所属门店      "
                        content = loanInfo.companyID ?: ""
                    }
                    3 -> {
                        type = 2
                        name = "客户名称      "
                        content = loanInfo.customer ?: ""
                    }
                    4 -> {
                        type = 2
                        name = "申请金额      "
                        content = "${Utils.parseMoney(BigDecimal(loanInfo.amount))}元"
                    }
                    5 -> {
                        type = 2
                        name = "按揭员          "
                        content = loanInfo.mortgageName ?: ""
                    }
                    6 -> type = 4
                    7 -> type = 0
                    8 -> {
                        type = 5
                        icon = R.mipmap.loanlist
                        name = " 放款信息"
                        isLineShow = loanInfo.scheduleId != 1
                    }
                    9 -> {
                        type = 2
                        name = "放款金额      "
                        content = "${Utils.parseMoney(BigDecimal(loanInfo.lendingAmount))}元"
                    }
                    10 -> {
                        type = 2
                        name = "放款日期      "
                        content = Utils.FormatTime(loanInfo.lendingTime, "yyyy-MM-dd", "yyyy/MM/dd")
                    }
                    11 -> {
                        type = 2
                        name = "年利率          "
                        content = "${loanInfo.interestRate}%"
                    }
                    12 -> {
                        type = 2
                        name = "还款方式      "
                        content = loanInfo.paymentName ?: ""
                    }
                    13 -> {
                        type = 2
                        name = "还款期数      "
                        content = "${loanInfo.lendingPeriod}期"
                    }
                    14 -> {
                        type = 2
                        name = "月供金额      "
                        content = "${Utils.parseMoney(BigDecimal(loanInfo.monthAmount))}元"
                        icon = R.color.detail1
                    }
                    15 -> {
                        type = 2
                        name = "其他月供      "
                        content = "${Utils.parseMoney(BigDecimal(loanInfo.otherAmount))}元"
                    }
                    16 -> {
                        type = 2
                        name = "月还款日      "
                        content = "每月${loanInfo.monthDate ?: ""}号"
                    }
                    17 -> {
                        type = 2
                        name = "还款期限      "
                        content = Utils.FormatTime(loanInfo.returnTime, "yyyy-MM-dd", "yyyy/MM/dd")
                    }
                    18 -> {
                        type = 2
                        name = "确认状态      "
                        when (loanInfo.scheduleId) {
                            -2 -> {
                                content = "已修改"
                                icon = R.color.detail1
                            }
                            -1 -> {
                                content = "有异议"
                                icon = R.color.detail1
                            }
                            0 -> {
                                content = "未确认"
                                icon = R.color.detail3
                            }
                            1 -> {
                                content = "已确认"
                                icon = R.color.detail2
                            }
                        }
                    }
                    19 -> {
                        type = 2
                        name = "异议原因      "
                        isClick = loanInfo.disagreeReason.isNullOrBlank()
                        content = loanInfo.disagreeReason ?: ""
                        icon = R.color.detail1
                    }
                    20 -> type = 4
                }
            })
        }
    }
}
