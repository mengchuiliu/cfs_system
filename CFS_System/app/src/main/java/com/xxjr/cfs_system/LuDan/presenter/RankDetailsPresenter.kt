package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.viewinter.RankDetailsVInter
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem

/**
 * Created by Administrator on 2017/11/16.
 */
class RankDetailsPresenter : BasePresenter<RankDetailsVInter, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        getData(0, model.getParam(getListParam(), "StoreLoanStatistics"), true)
    }

    private fun getListParam(): MutableList<Any> {
        val list = mutableListOf<Any>()
        val map = hashMapOf<Any, Any>()
        map["MethondName"] = "GetReceivablePastSixMonthsDetail"
        map["CompanyId"] = view.getCommonItem()?.remark ?: ""
        list.add(JSON.toJSONString(map))
        return list
    }

    fun getDatas(commonItem: CommonItem<Any>): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var item: CommonItem<Any>
        for (i in 0..6) {
            item = CommonItem()
            when (i) {
                0 -> item.type = 0
                1 -> {
                    item.type = 1
                    item.icon = R.mipmap.date
                    item.name = commonItem.name
                }
                2 -> {
                    item.type = 2
                    item.name = "批复金额："
                    item.content = "${Utils.div(if (commonItem.content.isNullOrBlank()) 0.0 else commonItem.content.toDouble())}万"
                }
                3 -> {
                    item.type = 2
                    item.name = "放款金额："
                    item.content = "${Utils.div(if (commonItem.hintContent.isNullOrBlank()) 0.0 else commonItem.hintContent.toDouble())}万"
                }
                4 -> {
                    item.type = 2
                    item.name = " 签 单 量 ："
                    item.content = "${commonItem.icon}笔"
                }
                5 -> {
                    item.type = 2
                    item.name = "回款金额："
                    item.content = "${Utils.div(commonItem.percent)}万"
                }
                6 -> item.type = 0
            }
            list.add(item)
        }
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        val json = data?.returnDataSet ?: JSONObject()
        if (json.isNotEmpty()) {
            val dataArray = json.getJSONArray("Data") ?: JSONArray()
            val detailArray = json.getJSONArray("CompanyInfo") ?: JSONArray()
            view.initRV(getDataDetails(dataArray), getDetails(detailArray))
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    //数据详情
    private fun getDataDetails(jsonArray: JSONArray): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        if (jsonArray.isNotEmpty()) {
            var item: CommonItem<Any>
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                item = CommonItem()
                item.name = "${jsonObject.getString("Year")}年${jsonObject.getString("Month")}月总数据"
                item.content = jsonObject.getString("ReplyAmount")
                item.hintContent = jsonObject.getString("LendAmount")
                item.icon = jsonObject.getIntValue("Count")
                item.percent = jsonObject.getDoubleValue("Amount")
                list.add(item)
            }
        }
        return list
    }

    //获取门店人员详情
    private fun getDetails(jsonArray: JSONArray): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        if (jsonArray.isNotEmpty()) {
            var item: CommonItem<Any>
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                item = CommonItem()
                item.type = jsonObject.getIntValue("ContactType")
                item.name = jsonObject.getString("ContactTypeName") ?: ""
                item.content = jsonObject.getString("EmpName") ?: ""
                item.hintContent = jsonObject.getString("Mobile") ?: ""
                list.add(item)
            }
        }
        return list
    }
}