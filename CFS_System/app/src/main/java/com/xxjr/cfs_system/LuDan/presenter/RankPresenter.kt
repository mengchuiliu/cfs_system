package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.HttpResult
import com.xxjr.cfs_system.LuDan.model.modelimp.RankMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.RankVInter
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.DateUtil
import entity.ChooseType
import java.util.*

class RankPresenter(val view: RankVInter) : HttpResult {
    private var maxN = 0
    private var clickPos = 3
    private var monthDate = DateUtil.getChooseDate(Date())

    private val model = RankMImp()

    override fun reusltSuccess(data: ResponseData?) {
        val jsonArray = JSON.parseArray(data?.data)
        if (jsonArray != null) {
            view.initRecycler(model.getItemData(jsonArray, view.getCompanyId(), clickPos, view.getType(),
                    view.getRankType(), view.getTimeType(), monthDate), getDoubles(jsonArray), maxN)
        }
    }

    override fun reusltFailed(msg: String?) {
        view.showMsg(msg)
    }

    fun getClickPos(): Int = clickPos

    fun getStoreName(id: String): String = model.getStoreName(id)

    fun getData(month: Int, year: Int, companyID: String, pos: Int, companyType: String, companyZone_id: String) {
        maxN = 0
        clickPos = pos
        model.getData(view.getFrgContext(), Hawk.get("SessionID"), 0,
                model.getParam(getParamList(month, year, companyID, companyType, companyZone_id), "StoreLoanStatistics"), this, true)
    }

    private fun getParamList(month: Int, year: Int, companyID: String, companyType: String, companyZone_id: String): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        val map1 = HashMap<String, Any>()
        when (view.getType()) {
            2 -> {
                map1["MethondName"] = "GetReceivableRankStatistics"
                if (view.getRankType() == 1) map1["OrderBySql"] = "ORDER  BY Amount DESC" else map1["OrderBySql"] = "ORDER BY Count DESC"
            }
            3 -> {
                map1["MethondName"] = "GetLoanRankStatistics"
                if (view.getRankType() == 1) map1["OrderBySql"] = "ORDER  BY Amount DESC" else map1["OrderBySql"] = "ORDER BY Count DESC"
            }
            4 -> map1["MethondName"] = "GetUserReceivableRankStatistics"
            5 -> map1["MethondName"] = "GetUserSignbillRankStatistics"
            6 -> {
                map1["MethondName"] = "GetReceivableRankStatistics"
                map1["OrderBySql"] = "ORDER BY Count DESC"
            }
        }
        map1.put("CompanyId", companyID)
        map1.put("Year", year)
        map1.put("Month", month)
        map1.put("CompanyType", companyType)
        map1.put("CompanyZone_id", companyZone_id)
        val s = JSON.toJSONString(map1)
        list.add(s)
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

    private fun getDoubles(jsonArray: JSONArray): MutableList<Double> {
        val doubles = ArrayList<Double>()
        for (i in jsonArray.indices) {
            val jsonObject = jsonArray.getJSONObject(i)
            when (view.getType()) {
                2, 3 -> {
                    when (view.getRankType()) {
                        1 -> {
                            doubles.add(jsonObject.getDoubleValue("Count"))
                            if (i == 0) {
                                maxN = jsonObject.getDoubleValue("Amount").toInt()
                            }
                        }
                        else -> {
                            doubles.add(jsonObject.getDoubleValue("Amount"))
                            if (i == 0) {
                                maxN = jsonObject.getDoubleValue("Count").toInt()
                            }
                        }
                    }
                }
                4 -> doubles.add(jsonObject.getDoubleValue("Amount"))
                5 -> doubles.add(jsonObject.getDoubleValue("Count"))
                6 -> doubles.add(jsonObject.getDoubleValue("Count"))
            }
        }
        return doubles
    }

    fun setMonthDate(date: String) {
        monthDate = date
    }
}