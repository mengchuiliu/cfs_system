package com.xxjr.cfs_system.LuDan.view.activitys.gold_account

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.TradeDetailAdapter
import com.xxjr.cfs_system.main.MyApplication
import entity.CommonItem
import entity.GoldTradeDetail
import kotlinx.android.synthetic.main.activity_gold_set.*
import kotlinx.android.synthetic.main.toolbar.*

class TradeDetailActivity : AppCompatActivity() {
    private lateinit var tradeDetail: GoldTradeDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold_set)
        gold_set_title.findViewById<TextView>(R.id.title).text = "明细详情"
        iv_back.visibility = View.VISIBLE
        iv_back.setOnClickListener { finish() }
        (application as MyApplication).addActivity(this)
        tradeDetail = (intent.getSerializableExtra("tradeDetail") as? GoldTradeDetail) ?: GoldTradeDetail()
        initRV(getTradeData())
    }

    private fun initRV(commonItems: MutableList<CommonItem<Any>>) {
        rv_gold_set.layoutManager = LinearLayoutManager(this@TradeDetailActivity)
        rv_gold_set.adapter = TradeDetailAdapter(this@TradeDetailActivity, commonItems)
    }

    private fun getTradeData(): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var commonItem: CommonItem<Any>
        for (i in 0..9) {
            commonItem = CommonItem()
            when (i) {
                0 -> commonItem.type = 0
                1 -> commonItem.apply {
                    type = 1
                    name = if (tradeDetail.isAdd) "入账金额" else "出账金额"
                    content = String.format("%.2f", tradeDetail.changeAmt)
                    icon = if (tradeDetail.isAdd) R.color.detail2 else R.color.detail1
                }
                2 -> commonItem.apply {
                    type = 0
                    isLineShow = true
                }
                3 -> commonItem.apply {
                    type = 2
                    name = "类型"
                    content = tradeDetail.tradeTp
                }
                4 -> commonItem.apply {
                    type = 2
                    name = "记账时间"
                    content = tradeDetail.bookTime
                }
                5 -> commonItem.apply {
                    type = 2
                    name = "交易号码"
                    content = tradeDetail.tradeSN
                }
                6 -> commonItem.apply {
                    type = 2
                    name = "往来账户信息"
                    content = tradeDetail.dealInfo
                }
                7 -> commonItem.apply {
                    type = 2
                    name = "交易概述"
                    content = tradeDetail.description
                }
                8 -> commonItem.apply {
                    type = 2
                    name = "备注        "
                    content = tradeDetail.remark
                }
                9 -> commonItem.apply {
                    type = 0
                    isClick = true
                }
            }
            list.add(commonItem)
        }
        return list
    }
}
