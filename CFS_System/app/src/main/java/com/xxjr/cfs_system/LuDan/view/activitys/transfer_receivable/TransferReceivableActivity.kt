package com.xxjr.cfs_system.LuDan.view.activitys.transfer_receivable

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.alibaba.fastjson.JSONObject
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TransferReceivableAdapter
import com.xxjr.cfs_system.LuDan.presenter.TransferReceivableP
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.WithdrawalActivity
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_transfer_receivable.*
import refresh_recyclerview.SimpleItemDecoration
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class TransferReceivableActivity : BaseActivity<TransferReceivableP, TransferReceivableActivity>(), BaseViewInter, View.OnClickListener {
    private var type = 0 // 0->充值码 ，1->待回款转账
    private var permits: List<String>? = null
    private lateinit var adapter: TransferReceivableAdapter
    private lateinit var topAdapter: CommonAdapter<CommonItem<Any>>

    private var ids = "" //回款id
    private var totalAmount = 0.0 //总金额、
    private var companyId = "" //门店id
    private var UserNo = "" //金账户账号
    private var protocolID = "" //协议id

    fun getType() = type

    override fun getPresenter(): TransferReceivableP = TransferReceivableP()

    override fun getLayoutId(): Int = R.layout.activity_transfer_receivable

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        permits = CFSUtils.getPostPermitValue(Hawk.get("PermitValue", ""), "817")
        toolbarTitle.text = "待转账回款"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        ll_top.setOnClickListener(this)
        ll_transfer.setOnClickListener(this)
        tv_batch_transfer.setOnClickListener(this)
        initRV()
        showTransferButton()
        presenter.getData()
    }

    fun initRV() {
        rv_topup.layoutManager = LinearLayoutManager(this)
        rv_topup.addItemDecoration(SimpleItemDecoration(this, 6))
        topAdapter = object : CommonAdapter<CommonItem<Any>>(this@TransferReceivableActivity, arrayListOf(), R.layout.item_topup_code) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.setText(R.id.tv_topup_code, item.payType)
                holder.setText(R.id.tv_company_name, item.hintContent)
                holder.setText(R.id.tv_amount, String.format("￥%.2f", item.percent))
                holder.setText(R.id.tv_date, item.date)
                holder.convertView.setOnClickListener {
                    val intent = Intent(this@TransferReceivableActivity, RechargeInfoActivity::class.java)
                    intent.putExtra("rechargeInfo", item.content)
                    startActivity(intent)
                }
            }
        }
        rv_topup.adapter = topAdapter

        rv_transfer.layoutManager = LinearLayoutManager(this@TransferReceivableActivity)
        adapter = TransferReceivableAdapter(this@TransferReceivableActivity, arrayListOf())
        adapter.setOnItemClick(RecycleItemClickListener { position ->
            if (clickIsDiff(position)) {
                CustomDialog.showTwoButtonDialog(this@TransferReceivableActivity, "不同金账户回款不能同时操作，是否切换？", "确定", "取消",
                        { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                            clearAll()
                            refreshItem(position)
                        })
            } else {
                refreshItem(position)
            }
        })
        adapter.setOnTitleItemClick(RecycleItemClickListener { position ->
            if (clickIsDiff(position)) {
                CustomDialog.showTwoButtonDialog(this@TransferReceivableActivity, "不同金账户回款不能同时操作，是否切换？", "确定", "取消",
                        { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                            clearAll()
                            refreshAllItem(position)
                        })
            } else {
                refreshAllItem(position)
            }

        })
        rv_transfer.adapter = adapter
    }

    //是否点击不同组金账户true->是
    private fun clickIsDiff(pos: Int): Boolean {
        val clickItem = adapter.datas?.get(pos) as CommonItem<Any>
        var clickPos = -1
        for (i in adapter.datas.indices) {
            val item = adapter.datas?.get(i) as CommonItem<Any>
            if (item.isClick) {
                clickPos = item.position
                break
            }
        }
        return if (clickPos == -1) false else clickItem.position != clickPos
    }

    private fun clearAll() {
        val list = mutableListOf<CommonItem<Any>>()
        for (i in adapter.datas.indices) {
            val item = adapter.datas?.get(i) as CommonItem<Any>
            item.isClick = false
            list.add(item)
        }
        refreshTransfer(list)
    }

    //单个刷新
    private fun refreshItem(pos: Int) {
        val clickItem = adapter.datas.get(pos) as CommonItem<Any>
        clickItem.isClick = !clickItem.isClick
        adapter.notifyItemChanged(pos, clickItem)
        var isTitle = true
        var titlePos = -1
        for (i in adapter.datas.indices) {
            val item = adapter.datas?.get(i) as CommonItem<Any>
            if (item.type == 1 && item.position == clickItem.position) {
                titlePos = i
            }
            if (item.type == 2 && item.position == clickItem.position) {
                if (!item.isClick) {
                    isTitle = false
                    break
                }
            }
        }
        if (titlePos != -1) {
            val titleItem = adapter.datas.get(titlePos) as CommonItem<Any>
            titleItem.isClick = isTitle
            adapter.notifyItemChanged(titlePos, titleItem)
        }
    }

    //整体刷新
    private fun refreshAllItem(pos: Int) {
        val clickItem = adapter.datas?.get(pos) as CommonItem<Any>
        clickItem.isClick = !clickItem.isClick
        adapter.notifyItemChanged(pos, clickItem)
        for (i in adapter.datas.indices) {
            val item = adapter.datas?.get(i) as CommonItem<Any>
            if (item.type == 2 && item.position == clickItem.position) {
                item.isClick = clickItem.isClick
                adapter.notifyItemChanged(i, item)
            }
        }
    }

    fun refreshCode(commonItems: MutableList<CommonItem<Any>>) {
        topAdapter.setNewData(commonItems)
        if (commonItems.isEmpty()) {
            rl_nodata.visibility = View.VISIBLE
            rv_topup.visibility = View.GONE
        } else {
            rv_topup.visibility = View.VISIBLE
            rl_nodata.visibility = View.GONE
        }
    }

    fun refreshTransfer(commonItems: MutableList<CommonItem<Any>>) {
        adapter.setNewData(commonItems as List<Any>?)
        if (commonItems.isEmpty()) {
            rl_nodata.visibility = View.VISIBLE
            rv_transfer.visibility = View.GONE
        } else {
            rl_nodata.visibility = View.GONE
            rv_transfer.visibility = View.VISIBLE
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            ll_top -> {
                type = 0
                showTitleText(tv_topup, tv_top_line, tv_transfer, tv_transfer_line)
            }
            ll_transfer -> {
                type = 1
                showTitleText(tv_transfer, tv_transfer_line, tv_topup, tv_top_line)
            }
            tv_batch_transfer -> getTransferData()
        }
    }

    private fun showTitleText(showT: TextView, showL: TextView, notShowT: TextView, notShowL: TextView) {
        showT.setTextColor(resources.getColor(R.color.font_home))
        showL.setBackgroundResource(R.color.font_home)
        notShowT.setTextColor(resources.getColor(R.color.font_c5))
        notShowL.setBackgroundResource(R.color.font_cc)
        showTransferButton()
        presenter.getData()
    }

    private fun showTransferButton() = tv_batch_transfer.let {
        when (type) {
            0 -> {
                rv_topup.visibility = View.VISIBLE
                rv_transfer.visibility = View.GONE
                it.visibility = View.GONE
            }
            1 -> {
                rv_topup.visibility = View.GONE
                rv_transfer.visibility = View.VISIBLE
                if (permits != null && permits?.contains("EB")!!) {
                    it.visibility = View.VISIBLE
                } else {
                    it.visibility = View.GONE
                }
            }
        }
    }

    //批量转账数据记录并转账
    private fun getTransferData() {
        val stringBuilder = StringBuilder()
        var titlePos = -1
        var amount = 0.0
        for (item in adapter.datas as MutableList<CommonItem<Any>>) {
            if (item.isClick && item.type == 2) {
                titlePos = item.position
                amount += item.percent
                if (!item.remark.isNullOrBlank()) {
                    stringBuilder.append(item.remark).append(",")
                }
            }
        }
        if (titlePos == -1) {
            showMsg("请先选择要转账的回款!")
            return
        }
        for (item in adapter.datas as MutableList<CommonItem<Any>>) {
            if (item.type == 1 && item.position == titlePos) {
                companyId = item.hintContent
                UserNo = item.remark
                protocolID = item.content
                break
            }
        }
        if (stringBuilder.length > 1) {
            ids = stringBuilder.substring(0, stringBuilder.length - 1)
        }
        totalAmount = amount
        val intent = Intent(this@TransferReceivableActivity, WithdrawalActivity::class.java)
        intent.putExtra("Type", 2)// 批量转账
        intent.putExtra("UserNo", UserNo)
        intent.putExtra("totalAmount", amount)
        startActivityForResult(intent, 55)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 55 && resultCode == 55) {
            val payType = data?.getIntExtra("payType", -1)
            val tradeSn: String
            when (payType) {
                0 -> {
                    tradeSn = data.getStringExtra("TradeSn") ?: ""
                    if (tradeSn.isNotBlank())
                        presenter.uploadGoldInfo(ids, tradeSn, totalAmount, companyId, UserNo, payType, protocolID, "")
                }
                2 -> {//充值码充值
                    val returnString = data.getStringExtra("returnString")
                    val jsonObject = JSONObject.parseObject(returnString)
                    tradeSn = jsonObject.getString("TradeSn") ?: ""
                    if (tradeSn.isNotBlank())
                        presenter.uploadGoldInfo(ids, tradeSn, totalAmount, companyId, UserNo, payType, protocolID, returnString)
                }
            }
        }
    }
}
