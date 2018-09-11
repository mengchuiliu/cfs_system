package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.DateUtil
import entity.CommonItem

class RankMImp : ModelImp() {
    val permits = CFSUtils.getPostPermitValue(Hawk.get("PermitValue", ""), "808")

    fun getItemData(jsonArray: JSONArray, companyId: String, pos: Int, type: Int, rankType: Int, timeType: Int, monthDate: String): MutableList<CommonItem<*>> {
        val list: MutableList<CommonItem<*>> = mutableListOf()
        val commonItem1 = CommonItem<Any>()
        commonItem1.type = 0
        list.add(commonItem1)

        //时间门店选择
        val commonItem2 = CommonItem<Any>()
        commonItem2.type = 1
        when (type) {
            2 -> {
                when (rankType) {
                    1 -> {
                        commonItem2.name = "按签单量排名"
                        commonItem2.isClick = true
                    }
                    else -> {
                        commonItem2.name = "按回款量排名"
                        commonItem2.isClick = false
                    }
                }
                commonItem2.isLineShow = true
            }
            3 -> {
                when (rankType) {
                    1 -> {
                        commonItem2.name = "按放款笔数排名"
                        commonItem2.isClick = true
                    }
                    else -> {
                        commonItem2.name = "按放款金额排名"
                        commonItem2.isClick = false
                    }
                }
                commonItem2.isLineShow = true
            }
            6 -> {
                commonItem2.isEnable = false
            }
            else -> commonItem2.name = if (companyId.isBlank()) "全部" else getStoreName(companyId)
        }
        commonItem2.position = pos
        commonItem2.icon = timeType
        if (timeType == 4) {
            commonItem2.content = "${getMonth(DateUtil.getMonth())}月"
            commonItem2.hintContent = "${getMonth(DateUtil.getMonth() - 1)}月"
            commonItem2.date = "${getMonth(DateUtil.getMonth() - 2)}月"
        } else {
            commonItem2.content = "${DateUtil.getYear()}"
            commonItem2.hintContent = "${DateUtil.getYear() - 1}"
            commonItem2.date = "${DateUtil.getYear() - 2}"
        }
        commonItem2.remark = monthDate
        list.add(commonItem2)

        //数据显示
        var item: CommonItem<Any>
        for (i in jsonArray.indices) {
            val jsonObject = jsonArray.getJSONObject(i)
            item = CommonItem()
            when (type) {
                2 -> {
                    item.type = 3
                    when (i) {
                        0 -> list[0].name = showName(jsonObject.getString("CompanyName"), companyId)
                        1 -> list[0].content = showName(jsonObject.getString("CompanyName"), companyId)
                        2 -> list[0].hintContent = showName(jsonObject.getString("CompanyName"), companyId)
                    }
                }
                3 -> {
                    item.type = 3
                    when (i) {
                        0 -> list[0].name = showName(jsonObject.getString("CompanyName"), companyId)
                        1 -> list[0].content = showName(jsonObject.getString("CompanyName"), companyId)
                        2 -> list[0].hintContent = showName(jsonObject.getString("CompanyName"), companyId)
                    }
                }
                4, 5 -> {
                    item.type = 2
                    when (i) {
                        0 -> list[0].name = showName(jsonObject.getString("SalesmanName"), companyId)
                        1 -> list[0].content = showName(jsonObject.getString("SalesmanName"), companyId)
                        2 -> list[0].hintContent = showName(jsonObject.getString("SalesmanName"), companyId)
                    }
                }
                6 -> {
                    item.type = 2
                    when (i) {
                        0 -> list[0].name = showName(jsonObject.getString("CompanyName"), companyId)
                        1 -> list[0].content = showName(jsonObject.getString("CompanyName"), companyId)
                        2 -> list[0].hintContent = showName(jsonObject.getString("CompanyName"), companyId)
                    }
                }
            }
            item.position = type
            when (type) {
                2, 3 -> {
                    item.name = "   ${jsonObject.getString("CompanyID")}${jsonObject.getString("CompanyName")}"
                    item.percent = jsonObject.getDoubleValue("Amount")
                    item.icon = jsonObject.getIntValue("Count")
                    item.content = jsonObject.getString("ReplyAmount")
                    item.hintContent = jsonObject.getString("LendAmount")
                    item.payType = jsonObject.getString("InTimeReceiveRate")//回款及时率
                    item.remark = jsonObject.getString("CompanyID")
                    item.date = jsonObject.getString("CompanyZone_id")
                    when (rankType) {
                        1 -> {
                            item.isClick = true
                        }
                        else -> {
                            item.isClick = false
                        }
                    }
                }
                4 -> {
                    item.name = showName(jsonObject.getString("SalesmanName"), companyId)
                    item.percent = jsonObject.getDoubleValue("Amount")
                    item.remark = jsonObject.getString("CompanyID")
                }
                5 -> {
                    item.name = showName(jsonObject.getString("SalesmanName"), companyId)
                    item.percent = jsonObject.getDoubleValue("Count")
                    item.remark = jsonObject.getString("CompanyID")
                }
                6 -> {
                    item.name = jsonObject.getString("CompanyID") + jsonObject.getString("CompanyName")
                    item.percent = jsonObject.getDoubleValue("Count")
                    item.remark = jsonObject.getString("CompanyID")
                }
            }
            list.add(item)
        }
        return list
    }

    private fun showName(name: String?, companyId: String): String {
        var showName = ""
        if (permits != null && permits.contains("E7")) {
            showName = name ?: ""
        } else {
            if (!name.isNullOrBlank() && name?.length!! > 1) {
                showName = "  ${name.substring(0, 1)} * * "
            }
        }
//        val companys = Hawk.get<String>("CompanyPowers")
//        if (companys.isNullOrBlank()) {
//            if (companyId == Hawk.get("CompanyID")) {
//                showName = name ?: ""
//            } else {
//                if (!name.isNullOrBlank() && name?.length!! > 1) {
//                    showName = "  ${name.substring(0, 1)} * * "
//                }
//            }
//        } else {
//            val strings = companys.split(",")
//            if (!name.isNullOrBlank() && name?.length!! > 1) {
////            showName = "  * * * ${name.substring(name.lastIndex - 1, name.lastIndex)}"
//                showName = "  ${name.substring(0, 1)} * * "
//            }
//            for (myCompany in strings) {
//                if (companyId == myCompany) {
//                    showName = name ?: ""
//                }
//            }
//        }
        return showName
    }

    fun getStoreName(id: String): String {
        if (id == "0") {
            return "全部"
        } else {
            var store = ""
            val jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey("CompanyInfoType")))
            if (jsonArray != null && jsonArray.size != 0) {
                for (i in jsonArray.indices) {
                    val `object` = jsonArray.getJSONObject(i)
                    if (`object`.getString("ID") == id) {
                        store = `object`.getString("Name")
                        break
                    }
                }
            }
            return store
        }
    }

    private fun getMonth(month: Int): Int = when {
        month == 0 -> 12
        month == -1 -> 11
        else -> month
    }
}