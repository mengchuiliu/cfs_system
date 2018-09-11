package com.xxjr.cfs_system.LuDan.view.activitys.withdraw_wallet

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.presenter.WithdrawListP
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.GoldWebActivity
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_withdraw_list.*
import kotlinx.android.synthetic.main.toolbar.*
import refresh_recyclerview.SimpleItemDecoration
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import java.util.*

class WithdrawListActivity : BaseActivity<WithdrawListP, WithdrawListActivity>(), BaseViewInter, View.OnClickListener {
    private var userNo = ""
    private var amount = 0.0
    private lateinit var adapter: CommonAdapter<CommonItem<Any>>

    override fun getPresenter(): WithdrawListP = WithdrawListP()

    override fun getLayoutId(): Int = R.layout.activity_withdraw_list

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "提成钱包"
        ivBack.visibility = View.VISIBLE
        presenter.setDefaultValue()
        tv_date.text = Utils.FormatTime(DateUtil.getFormatDate(Date()), "yyyy-MM-dd", "yyyy-MM月")
        initRv()
        iv_back.setOnClickListener(this)
        tv_date.setOnClickListener(this)
        iv_date.setOnClickListener(this)
    }

    private fun initRv() {
        rv_withdraw.layoutManager = LinearLayoutManager(this)
        rv_withdraw.addItemDecoration(SimpleItemDecoration(this@WithdrawListActivity, 5))
        adapter = object : CommonAdapter<CommonItem<Any>>(this@WithdrawListActivity, mutableListOf(), R.layout.item_withdraw_list) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.setText(R.id.tv_loan_detail, item.name)
                holder.setText(R.id.tv_bank_money, "回款金额：${item.content}元")
                holder.setText(R.id.tv_withdrawal_money, "提成金额：${item.hintContent}元")
                holder.setText(R.id.tv_withdrawal_type, "提成类型：${item.payType}")
                if (item.date.isNullOrBlank()) holder.setVisible(R.id.tv_time, false) else holder.setVisible(R.id.tv_time, true)
                holder.setText(R.id.tv_time, "清分时间：${item.date}")
                when (item.type) {// 状态 2,3待清分； 4 可提现； 5 已提现；
                    2, 3 -> {
                        holder.setVisible(R.id.tv_state, true)
                        holder.setVisible(R.id.tv_withdrawal_over, false)
                        holder.getView<TextView>(R.id.tv_state).isEnabled = false
                        holder.setText(R.id.tv_state, "待清分")
                    }
                    4 -> {
                        holder.setVisible(R.id.tv_state, true)
                        holder.setVisible(R.id.tv_withdrawal_over, false)
                        holder.getView<TextView>(R.id.tv_state).isEnabled = true
                        holder.setText(R.id.tv_state, "  提现  ")
                    }
                    5 -> {
                        holder.setVisible(R.id.tv_state, false)
                        holder.setVisible(R.id.tv_withdrawal_over, true)
                    }
                    else -> {
                        holder.setINVISIBLE(R.id.tv_state, true)
                        holder.setVisible(R.id.tv_withdrawal_over, false)
                    }
                }
                holder.setOnClickListener(R.id.tv_state) {
                    if (item.type == 4) {
                        CustomDialog.showTwoButtonDialog(this@WithdrawListActivity, "您将提成资金提现到 ？", "金账户", "银行卡",
                                { dialogInterface: DialogInterface, i: Int ->
                                    dialogInterface.dismiss()
                                    presenter.withdrawMoney(item.position, item.remark, item.hintContent, 0)
                                },
                                { dialogInterface: DialogInterface, i: Int ->
                                    dialogInterface.dismiss()
                                    presenter.withdrawMoney(item.position, item.remark, item.hintContent, 1)
                                    userNo = item.remark
                                    amount = item.hintContent.toDouble()
                                })
                    }
                }
            }
        }
        rv_withdraw.adapter = adapter
    }

    //提现到银行卡
    fun toGoldWeb() {
        val intent1 = Intent(this@WithdrawListActivity, GoldWebActivity::class.java)
        intent1.putExtra("UserNo", userNo)
        intent1.putExtra("Amount", amount)
        intent1.putExtra("Type", 0)
        startActivityForResult(intent1, 22)
    }

    fun refreshData(commonItems: MutableList<CommonItem<Any>>) {
        if (commonItems.isEmpty()) rl_no_data.visibility = View.VISIBLE else rl_no_data.visibility = View.GONE
        adapter.setNewData(commonItems)
    }

    fun setAmountShow(preLedger: String, preWithdraw: String) {
        tv_withdrawal_money.text = "${preWithdraw}元"
        tv_clearing_money.text = "${preLedger}元"
    }

    override fun onClick(p0: View?) {
        when (p0) {
            iv_back -> finish()
            iv_date, tv_date -> presenter.timeShow(tv_date)
        }
    }

    override fun onResume() {
        super.onResume()
        if (presenter.time.isNotBlank())
            presenter.getWithdrawListData(presenter.time)
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 22 && resultCode == 22) {
//            if (presenter.time.isNotBlank())
//                presenter.getWithdrawListData(presenter.time)
//        }
    }
}
