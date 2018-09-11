package com.xxjr.cfs_system.LuDan.view.activitys.transaction_record

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.ItemCommonAdapter
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.TransactionRecord
import kotlinx.android.synthetic.main.activity_common_list.*

class TransactionDetailActivity : BaseActivity<BasePresenter<*, *>, BaseViewInter>() {
    private lateinit var record: TransactionRecord

    override fun getPresenter(): BasePresenter<*, *>? = null

    override fun getLayoutId(): Int = R.layout.activity_common_list

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "交易详情"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        record = (intent.getSerializableExtra("TransactionRecord") as? TransactionRecord) ?: TransactionRecord()
        initRV()
    }

    private fun initRV() {
        rv_remind.layoutManager = LinearLayoutManager(this@TransactionDetailActivity)
        rv_remind.adapter = ItemCommonAdapter(this@TransactionDetailActivity, getItemData())
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    private fun getItemData() = mutableListOf<CommonItem<Any>>().apply {
        for (i in 0..18) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> {
                        type = 0
                        icon = R.color.blank_bg
                        position = 8
                    }
                    1 -> {
                        type = 2
                        name = "流水号："
                        content = record.TransSn ?: ""
                    }
                    2 -> {
                        type = 0
                        icon = R.color.white
                        position = 8
                    }
                    3 -> {
                        type = 0
                        icon = R.color.blank_bg
                        position = 1
                    }
                    4 -> {
                        type = 2
                        name = "所属门店："
                        content = record.ApplyCompanyName ?: ""
                        icon = R.color.font_home
                    }
                    5 -> {
                        type = 2
                        name = "交易类型："
                        content = when (record.TransType) {
                            1 -> "成本报销"
                            2 -> "按揭提成"
                            3 -> "业务员提成"
                            4 -> "退定金"
                            5 -> "退回款"
                            6 -> "拆借"
                            7 -> "固定支出"
                            8 -> "回款"
                            else -> "未指定"
                        }
                    }
                    6 -> {
                        type = 2
                        name = "交易方式："
                        content = when (record.TransWay) {
                            1 -> "现金"
                            2 -> "转账"
                            3 -> "代扣"
                            4 -> "划拨"
                            5 -> "金账户冻结"
                            6 -> "金账户解冻"
                            else -> "未指定"
                        }
                    }
                    7 -> {
                        type = 2
                        name = "交易金额："
                        content = "${String.format("%,.2f", Utils.div(record.Amount, 100.0, 2))}元"
                        icon = R.color.detail1
                    }
                    8 -> {
                        type = 2
                        name = "交易状态："
                        content = when (record.State) {
                            1 -> "交易申请中"
                            2 -> "交易申请失败"
                            3 -> "交易中"
                            4 -> "交易成功"
                            5 -> "交易失败"
                            else -> "未指定"
                        }
                        icon = when (record.State) {
                            1 -> R.color.detail3
                            2 -> R.color.detail1
                            3 -> R.color.font_home
                            4 -> R.color.detail2
                            5 -> R.color.detail1
                            else -> R.color.detail3
                        }
                    }
                    9 -> {
                        type = 2
                        name = "交易时间："
                        content = Utils.FormatTime(record.HappenTime
                                ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm")
                    }
                    10 -> {
                        type = 2
                        name = "交易备注："
                        val sb = StringBuilder()
                        sb.append(record.Remark ?: "")
                        if (record.State == 2 || record.State == 5) {
                            sb.append("  失败原因：").append(record.FailReason ?: "")
                        }
                        content = sb.toString()
                    }
                    11 -> {
                        type = 0
                        icon = R.color.white
                        position = 15
                    }
                    12 -> {
                        type = 0
                        icon = R.color.blank_bg
                        position = 8
                    }
                    13 -> {
                        type = 2
                        name = "支出账户："
                        content = record.OutAcct ?: ""
                    }
                    14 -> {
                        type = 2
                        name = "收款账户："
                        content = record.InAcct ?: ""
                    }
                    15 -> {
                        type = 2
                        name = "    操作人："
                        content = record.OperatorName ?: ""
                    }
                    16 -> {
                        type = 2
                        name = "创建时间："
                        content = Utils.FormatTime(record.InsertTime
                                ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm")
                    }
                    17 -> {
                        type = 2
                        name = "修改时间："
                        content = Utils.FormatTime(record.UpdateTime
                                ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm")
                    }
                    18 -> {
                        type = 0
                        icon = R.color.white
                        position = 15
                    }
                }
            })
        }
    }
}
