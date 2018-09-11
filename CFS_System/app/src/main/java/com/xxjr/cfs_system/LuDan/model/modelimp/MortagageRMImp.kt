package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.CommonItem

/**
 * Created by Administrator on 2017/12/12.
 */
class MortagageRMImp : ModelImp() {

    fun getMortgages(data: String): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        val item1 = CommonItem<Any>()
//        item1.name = "所属公司"
        item1.name = ""
        item1.remark = "申请客户"
        item1.content = "申请金额"
        item1.hintContent = "放款金额"
        list.add(item1)
        val jsonArray = JSON.parseArray(data)
        if (jsonArray != null && jsonArray.size != 0) {
            var item: CommonItem<Any>
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                item = CommonItem()
                item.name = jsonObject.getString("CompanyName")
                item.remark = jsonObject.getString("CustomerNames")
                item.content = "${Utils.div(jsonObject.getDoubleValue("LoanAmount"))}万"
                item.hintContent = "${Utils.div(jsonObject.getDoubleValue("LendAmount"))}万"
                item.percent = jsonObject.getDoubleValue("LendAmount")
                list.add(item)
            }
        }
        return list
    }


    fun getStoresDatas(jsonArray: JSONArray): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        val item1 = CommonItem<Any>()
//        item1.name = "所属公司"
        item1.name = ""
        item1.content = "申请金额"
        item1.hintContent = "放款金额"
        list.add(item1)
        if (jsonArray.size != 0) {
            var item: CommonItem<Any>
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                item = CommonItem()
                item.name = jsonObject.getString("CompanyName")
                item.content = "${Utils.div(jsonObject.getDoubleValue("LoanAmount"))}万(${jsonObject.getString("LoanCount")}笔)"
                item.hintContent = "${Utils.div(jsonObject.getDoubleValue("LendAmount"))}万(${jsonObject.getString("LendCount")}笔)"
                list.add(item)
            }
        }
        return list
    }

    fun getMortgageDatas(jsonArray: JSONArray): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        val item1 = CommonItem<Any>()
//        item1.name = "按揭员姓名"
        item1.name = ""
        item1.content = "申请金额"
        item1.hintContent = "放款金额"
        list.add(item1)
        if (jsonArray.size != 0) {
            var item: CommonItem<Any>
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                item = CommonItem()
                item.name = jsonObject.getString("MortgageClerkName")
                item.content = "${Utils.div(jsonObject.getDoubleValue("LoanAmount"))}万(${jsonObject.getString("LoanCount")}笔)"
                item.hintContent = "${Utils.div(jsonObject.getDoubleValue("LendAmount"))}万(${jsonObject.getString("LendCount")}笔)"
                list.add(item)
            }
        }
        return list
    }

    fun getZoneDataList(zone: String): List<ChooseType> {
        val list = java.util.ArrayList<ChooseType>()
        val chooseType1 = ChooseType()
        chooseType1.content = "全部"
        chooseType1.ids = ""
        list.add(chooseType1)
        val jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey("ZoneType")))
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                val `object` = jsonArray.getJSONObject(i)
                if (zone.isBlank()) {
                    chooseType = ChooseType()
                    chooseType.content = `object`.getString("Name")
                    chooseType.ids = `object`.getString("Value")
                    list.add(chooseType)
                } else {
                    val zones = zone.split(",")
                    if (zones.isNotEmpty()) {
                        for (ower in zones) {
                            if (ower == `object`.getString("Value")) {
                                chooseType = ChooseType()
                                chooseType.content = `object`.getString("Name")
                                chooseType.ids = `object`.getString("Value")
                                list.add(chooseType)
                                break
                            }
                        }
                    }
                }
            }
        }
        return list
    }
}