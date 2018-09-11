package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.TopUpInfoActivity
import entity.CommonItem
import java.util.HashMap

/**
 * Created by Administrator on 2018/3/22.
 */
class TopUpPresenter : BasePresenter<TopUpInfoActivity, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        val jsonObject = JSONObject.parseObject(view.getReturnString())
        view.initRv(getTopUpInfoData(jsonObject), jsonObject.getString("RechargeCode") ?: "")
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {}

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    private fun getTopUpInfoData(jsonObject: JSONObject): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var commonItem: CommonItem<Any>
        for (i in 0..6) {
            commonItem = CommonItem()
            when (i) {
                0 -> {
                    commonItem.name = "充值流水号  "
                    commonItem.content = jsonObject.getString("TradeSn")
                }
                1 -> {
                    commonItem.name = "充值金额      "
                    commonItem.content = "${jsonObject.getString("Amount")}元"
                }
                2 -> {
                    commonItem.name = "入账户名      "
                    commonItem.content = jsonObject.getString("ReceiveAcctName")
                }
                3 -> {
                    commonItem.name = "入账银行      "
                    commonItem.content = jsonObject.getString("ReceiveBank")
                }
                4 -> {
                    commonItem.name = "支行信息      "
                    commonItem.content = jsonObject.getString("ReceiveBranchBank")
                }
                5 -> {
                    commonItem.name = "入账账号      "
                    commonItem.content = jsonObject.getString("ReceiveCardNo")
                }
                6 -> {
                    commonItem.name = "所属商户      "
                    commonItem.content = jsonObject.getString("MerchantName")
                }
            }
            list.add(commonItem)
        }
        return list
    }
}