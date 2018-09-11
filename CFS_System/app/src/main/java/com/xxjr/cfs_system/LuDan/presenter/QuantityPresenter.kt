package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.HttpResult
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.viewinter.QuantityVInter
import com.xxjr.cfs_system.services.CacheProvide
import entity.ChooseType
import entity.CommonItem
import entity.ReportCompare
import java.util.HashMap

class QuantityPresenter(val view: QuantityVInter) : HttpResult {
    private val model = ModelImp()

    override fun reusltSuccess(data: ResponseData?) {
        val commonItemList: MutableList<ReportCompare> = arrayListOf()
        val doubles: MutableList<Double> = arrayListOf()
        val jsonObject = data?.returnDataSet ?: JSONObject()
        if (jsonObject.isNotEmpty()) {
            for (i in 12 downTo 1) {
                val jsonArray = jsonObject.getJSONArray("$i") ?: JSONArray()
                if (jsonArray.isNotEmpty()) {
                    commonItemList.add(ReportCompare().apply {
                        for (j in jsonArray.indices) {
                            val json = jsonArray.getJSONObject(j)
                            when (j) {
                                0 -> {
                                    show1 = true
                                    time1 = json.getString("Month")
                                    if (view.getType() == 0) amount1 = json.getDoubleValue("Amount")
                                    else if (view.getType() == 1) amount1 = json.getDoubleValue("Count")
                                    doubles.add(amount1)
                                }
                                1 -> {
                                    show2 = true
                                    time2 = json.getString("Month")
                                    if (view.getType() == 0) amount2 = json.getDoubleValue("Amount")
                                    else if (view.getType() == 1) amount2 = json.getDoubleValue("Count")
                                    doubles.add(amount2)
                                }
                                2 -> {
                                    show3 = true
                                    time3 = json.getString("Month")
                                    if (view.getType() == 0) amount3 = json.getDoubleValue("Amount")
                                    else if (view.getType() == 1) amount3 = json.getDoubleValue("Count")
                                    doubles.add(amount3)
                                }
                            }
                        }
                    })
                }
            }
            view.refreshData(commonItemList, doubles)
        }
    }

    override fun reusltFailed(msg: String?) {
        view.showMsg(msg)
    }

    fun getData(year: String, companyID: String, companyType: String, companyZone_id: String) {
        model.getData(view.getFrgContext(), Hawk.get("SessionID"), 0, model.getParam(getParamList(year, companyID, companyType, companyZone_id), "StoreLoanStatistics"), this, true)
    }

    private fun getParamList(year: String, companyID: String, companyType: String, companyZone_id: String): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        val map1 = HashMap<String, Any>()
        if (view.getType() == 0) {
            map1.put("MethondName", "GetReceivableStatistics")
        } else if (view.getType() == 1) {
            map1.put("MethondName", "GetSignbillStatistics")
        }
        map1.put("CompanyId", companyID)
        map1.put("MultiYear", year)
        map1.put("CompanyType", companyType)
        map1.put("CompanyZone_id", companyZone_id)
        val str = JSON.toJSONString(map1)
        list.add(str)
        return list
    }

    fun getTypeDataList(companyType: String, companyZone_id: String): List<ChooseType> {
        val list = java.util.ArrayList<ChooseType>()
        val chooseType1 = ChooseType()
        chooseType1.content = "全部"
        chooseType1.ids = ""
        list.add(chooseType1)
        val jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey("CompanyInfoType")))
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                val `object` = jsonArray.getJSONObject(i)
                if (companyType.isNotBlank()) {
                    if (companyType == `object`.getString("Type")) {
                        chooseType = ChooseType()
                        chooseType.content = `object`.getString("Name")
                        chooseType.ids = `object`.getString("ID")
                        list.add(chooseType)
                    }
                } else if (companyZone_id.isNotBlank()) {
                    if (companyZone_id == "other") {
                        val zoneId = `object`.getString("CompanyZone_id")
                        if (zoneId != "110000" && zoneId != "310000" && zoneId != "440100" && zoneId != "440300") {
                            chooseType = ChooseType()
                            chooseType.content = `object`.getString("Name")
                            chooseType.ids = `object`.getString("ID")
                            list.add(chooseType)
                        }
                    } else {
                        if (companyZone_id == `object`.getString("CompanyZone_id")) {
                            chooseType = ChooseType()
                            chooseType.content = `object`.getString("Name")
                            chooseType.ids = `object`.getString("ID")
                            list.add(chooseType)
                        }
                    }
                } else {
                    chooseType = ChooseType()
                    chooseType.content = `object`.getString("Name")
                    chooseType.ids = `object`.getString("ID")
                    list.add(chooseType)
                }
            }
        }
        return list
    }
}