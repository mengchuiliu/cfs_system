package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.orhanobut.logger.Logger
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.transfer_receivable.RechargeInfoActivity
import entity.CommonItem
import java.util.HashMap

/**
 * Created by Administrator on 2018/4/10.
 */
class RechargeInfoPresenter : BasePresenter<RechargeInfoActivity, ModelImp>() {
    private var commonItems = mutableListOf<CommonItem<Any>>()

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        if (view.getRechargeInfo().isNotBlank()) {
            val jsonObject = JSONObject.parseObject(view.getRechargeInfo())
            setRechargeCodeInfoData(jsonObject)
            getData(0, getDataParam(mutableListOf(jsonObject.getString("RechargeCode"))), true)
        }
    }

    fun getDataParam(mutableList: MutableList<Any>): String {
        val map = HashMap<String, Any>()
        map["Action"] = "GetBooksInfoByRechargeCode"
        map["IsUseZip"] = false
        map["Function"] = ""
        map["ParamString"] = mutableList
        map["TranName"] = "GoldAccount"
        Logger.e("==充值码详细==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            if ((data?.data ?: "").isNotBlank()) {
                val jsonArray = JSONArray.parseArray(data?.data)
                if (jsonArray.isNotEmpty()) {
                    for (i in jsonArray.indices) {
                        setReceivableInfo(jsonArray.getJSONObject(i))
                    }
                }
                view.initRVCode(commonItems)
            } else {
                view.initRVCode(commonItems)
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    //设置金账户信息
    private fun setRechargeCodeInfoData(jsonObject: JSONObject) {
        commonItems.add(CommonItem<Any>().apply {
            type = 4
            name = jsonObject.getString("CstmNm")
            content = jsonObject.getString("BankName")
            remark = jsonObject.getString("BankCardNo")
        })
        for (i in 0..9) {
            when (i) {
                0 -> commonItems.add(CommonItem<Any>().apply {
                    type = 2
                    name = "充值码信息"
                })
                1 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "充值码          "
                    content = jsonObject.getString("RechargeCode")
                    icon = R.color.detail1
                    isEnable = false
                    isLineShow = true
                })
                2 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "充值流水号  "
                    content = jsonObject.getString("TradeSn")
                    isEnable = false
                    isLineShow = true
                })
                3 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "充值金额      "
                    content = String.format("%.2f元", jsonObject.getDoubleValue("Amount"))
                    isEnable = false
                    isLineShow = true
                })
                4 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "入账户名      "
                    content = jsonObject.getString("ReceiveAcctName")
                    isEnable = false
                    isLineShow = true
                })
                5 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "入账银行      "
                    content = jsonObject.getString("ReceiveBank")
                    isEnable = false
                    isLineShow = true
                })
                6 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "支行信息      "
                    content = jsonObject.getString("ReceiveBranchBank")
                    isEnable = false
                    isLineShow = true
                })
                7 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "入账账号      "
                    content = jsonObject.getString("ReceiveCardNo")
                    isEnable = false
                    isLineShow = true
                })
                8 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "所属商户      "
                    content = jsonObject.getString("MerchantName")
                    isEnable = false
                    isLineShow = true
                })
                9 -> commonItems.add(CommonItem<Any>().apply { type = 1 })
            }
        }
    }

    //设置回款信息
    private fun setReceivableInfo(jsonObject: JSONObject) {
        for (i in 0..10) {
            when (i) {
                0 -> commonItems.add(CommonItem<Any>().apply { type = 0 })
                1 -> commonItems.add(CommonItem<Any>().apply {
                    type = 2
                    name = "回款入账信息"
                })
                2 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "收支类型："
                    content = jsonObject.getString("BookType")
                })
                3 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "收支方式："
                    content = jsonObject.getString("PayType")
                })
                4 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "金额：       "
                    content = String.format("￥%.2f", jsonObject.getDoubleValue("Amount"))
                    isClick = true
                    icon = R.color.detail1
                })
                5 -> commonItems.add(CommonItem<Any>().apply {
                    type = 2
                    name = "所属门店："
                    content = jsonObject.getString("CompanyName")
                    isClick = true
                })
                6 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "合同编号："
                    content = jsonObject.getString("S5")
                })
                7 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "所属客户："
                    content = jsonObject.getString("CustomerNames")
                })
                8 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "业 务 员  ："
                    content = jsonObject.getString("servicePeople")
                })
                9 -> commonItems.add(CommonItem<Any>().apply {
                    type = 3
                    name = "合同备注："
                    content = if (jsonObject.getString("Remark").isNullOrBlank()) "无" else jsonObject.getString("Remark")
                })
                10 -> commonItems.add(CommonItem<Any>().apply { type = 1 })
            }
        }

    }
}