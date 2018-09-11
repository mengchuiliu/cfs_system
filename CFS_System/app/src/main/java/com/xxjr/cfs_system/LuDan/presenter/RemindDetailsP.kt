package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.reimbursement_remind.RemindDetailsActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.LoanInfo
import java.math.BigDecimal

/**
 * Created by Administrator on 2018/4/9.
 */
class RemindDetailsP : BasePresenter<RemindDetailsActivity, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        getData(0, model.getParam(mutableListOf<Any>(view.getLendingId()), "GetLoanInfoById"), true)
    }

    override fun permissionSuccess(code: Int) {
        when (code) {
            Constants.REQUEST_CODE_PERMISSION_Phone -> {
                view.callPhone()
            }
        }
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            if ((data?.returnString ?: "").isNotBlank()) {
                val jsonObject = JSONObject.parseObject(data?.returnString)
                if (jsonObject.isNotEmpty()) {
                    view.initRV(getItemData(jsonObject))
                } else {
                    view.showMsg("数据获取失败")
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    private fun getItemData(jsonObject: JSONObject): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        list.add(CommonItem<Any>().apply { type = 0 })
        val jsonArray = jsonObject.getJSONArray("CustomerInfo")
        if (jsonArray.isNotEmpty()) {
            list.addAll(getCustomers(jsonArray))
        }
        var commonItem: CommonItem<Any>
        for (i in 1..11) {
            commonItem = CommonItem()
            when (i) {
                1 -> commonItem.apply {
                    type = 1
                    icon = R.mipmap.icon_loan
                    name = "贷款信息-${jsonObject.getString("LoadInfo")}"
                }
                2 -> commonItem.apply {
                    type = 2
                    name = "贷款编号："
                    content = jsonObject.getString("LoanCode")
                }
                3 -> commonItem.apply {
                    type = 2
                    name = "申请金额："
                    content = Utils.parseMoney(BigDecimal(jsonObject.getDoubleValue("TotalApplyAmt"))) + "元"
                }
                4 -> commonItem.apply {
                    type = 2
                    name = "申请客户："
                    content = jsonObject.getString("CustomerNames")
                }
                5 -> commonItem.apply {
                    type = 2
                    name = "按 揭 员  ："
                    content = jsonObject.getString("MortgageUserName")
                }
                6 -> commonItem.apply {
                    type = 2
                    name = "业 务 员  ："
                    content = jsonObject.getString("Salesman")
                }
                7 -> commonItem.type = 4
                8 -> commonItem.type = 0
                9 -> commonItem.apply {
                    type = 1
                    icon = R.mipmap.loanlist
                    name = "放款信息"
                }
                10 -> commonItem.apply {
                    type = 3
                    item = LoanInfo().apply {
                        lendingAmount = jsonObject.getDoubleValue("LendAmount")
                        lendingTime = Utils.getTime(jsonObject.getString("LendDate"))
                        monthAmount = jsonObject.getDoubleValue("MonthPay")
                        monthDate = jsonObject.getString("MonthPayDay")
                        returnTime = Utils.getTime(jsonObject.getString("PayDeadline"))
                        clerkName = jsonObject.getString("InsertInfo")
                        otherAmount = jsonObject.getDoubleValue("OtherPay")
                        lendingPeriod = jsonObject.getIntValue("RepaymentPeriods")//还款期数
                    }
                }
                11 -> commonItem.type = 4
            }
            list.add(commonItem)
        }
        return list
    }

    //获取客户信息
    private fun getCustomers(jsonArray: JSONArray): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        for (i in jsonArray.indices) {
            val jsonObject = jsonArray.getJSONObject(i)
            list.add(CommonItem<Any>().apply {
                type = 1
                icon = R.mipmap.icon_client
                name = "客户信息"
            })
            for (j in 0..3) {
                when (j) {
                    0 -> {
                        list.add(CommonItem<Any>().apply {
                            type = 2
                            name = "客户姓名："
                            content = jsonObject.getString("Name")
                        })
                    }
                    1 -> {
                        list.add(CommonItem<Any>().apply {
                            type = 2
                            name = "客户电话："
                            content = jsonObject.getString("Phone")
                            isClick = true
                        })
                    }
                    2 -> list.add(CommonItem<Any>().apply { type = 4 })
                    3 -> list.add(CommonItem<Any>().apply { type = 0 })
                }
            }
        }
        return list
    }
}