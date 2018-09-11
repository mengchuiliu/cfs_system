package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.GoldAccountMImp
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.GoldAccountActivity
import entity.CommonItem

/**
 * Created by Administrator on 2018/3/12.
 */
class GoldAccountP : BasePresenter<GoldAccountActivity, GoldAccountMImp>() {
    override fun getModel(): GoldAccountMImp = GoldAccountMImp()

    override fun setDefaultValue() {}

    //获取账户详情
    fun getAccountBalance() {
        getData(0, model.getDataParam(getAccountParam(), "GoldAccountBalanceQry"), true)
    }

    private fun getAccountParam(): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add(1)
        list.add(view.getGoldUserInfo()?.userNo ?: "")
        return list
    }

    fun checkHeXinAccount() {
        getData(1, model.getMoreParam(arrayListOf(), "NFexAccount", "CheckBinding"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.returnString ?: "").isNotBlank()) {
                        val jsonArray = JSON.parseArray(data?.returnString ?: "")
                        if (jsonArray != null && jsonArray.size != 0) {
                            val jsonObject = jsonArray.getJSONObject(0)
                            view.refreshData(model.getAmountData(jsonObject), jsonObject.getDoubleValue("TotalAmt"),
                                    jsonObject.getInteger("State") ?: -1)
                        }
                    } else {
                        view.refreshData(model.getAmountData(null), 0.00, -1)
                    }
                }
                1 -> {
                    val returnString = data?.returnString ?: ""
                    if (returnString.isNotBlank()) {
                        if (returnString == "0") view.toRegisterOrExchange(returnString)
                        else view.toRegisterOrExchange(data?.data ?: "")
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) {
        when (resultCode) {
            0 -> view.dataFailed(msg)
            1 -> view.showMsg(msg)
        }
    }

    fun getProjectData(): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var item: CommonItem<Any>
        for (i in 0..3) {
            item = CommonItem()
            when (i) {
                0 -> item.apply {
                    icon = R.mipmap.withdrawal
                    name = "提现"
                }
                1 -> item.apply {
                    icon = R.mipmap.top_up
                    name = "充值"
                }
                2 -> item.apply {
                    icon = R.mipmap.detail
                    name = "明细"
                }
                3 -> item.apply {
                    icon = R.mipmap.customer_account
                    name = "顾客账户"
                }
//                4 -> item.apply {
//                    icon = R.mipmap.he_xin_icon
//                    name = "核新产融"
//                }
            }
            list.add(item)
        }
        return list
    }
}