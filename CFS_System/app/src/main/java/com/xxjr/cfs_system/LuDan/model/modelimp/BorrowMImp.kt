package com.xxjr.cfs_system.LuDan.model.modelimp

import android.util.Log
import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.tools.Utils
import entity.BorrowInfo
import entity.CommonItem
import java.math.BigDecimal
import java.util.HashMap

class BorrowMImp : ModelImp() {

    fun getParam(list: List<Any>, tranName: String, Action: String): String {
        val map = HashMap<String, Any>()
        map.put("DBMarker", "DB_CFS_Loan")
        map.put("Marker", "HQServer")
        map.put("IsUseZip", false)
        map.put("Action", Action)
        map.put("Function", "Pager")
        map.put("ParamString", list)
        map.put("TranName", tranName)
        Logger.e("===上传拆借参数===> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }


    fun getRVData(borrowInfo: BorrowInfo, update: Boolean): MutableList<CommonItem<Any>> {
        val list: MutableList<CommonItem<Any>> = ArrayList()
        var item: CommonItem<Any>
        for (i in 0..8) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.type = 0
                    item.name = "拆借类型"
                    item.content = Utils.getTypeValue(Utils.getTypeDataList("LendingType"), borrowInfo.borrowTypeId ?: 0)
                }
                1 -> {
                    item.type = 0
                    item.name = "拆借日期"
                    item.content = borrowInfo.borrowDate ?: ""
                }
                2 -> {
                    item.type = 2
                    item.name = "拆借金额"
                    item.content = if (borrowInfo.borrowAmount == null) "" else (borrowInfo.borrowAmount!!.toLong()).toString()
                }
                3 -> {
                    item.type = 3
                    item.name = "拆借利息"
                    item.content = (borrowInfo.borrowInterest ?: "").toString()
                }
                4 -> {
                    item.type = 0
                    item.name = "预计结算日期"
                    item.content = borrowInfo.predictBorrowDate ?: ""
                }
                5 -> {
                    item.type = 1
                    item.name = "预计拆借创收"
                    item.content = if (borrowInfo.predictBorrowIncome == null) "" else "${Utils.parseMoney(BigDecimal(borrowInfo.predictBorrowIncome ?: 0.0))} 元"
                }
                6 -> {
                    item.type = 0
                    item.name = "还款方式"
                    item.content = Utils.getTypeValue(Utils.getTypeDataList("RePayType"), borrowInfo.rePayTypeId ?: -1)
                }
                7 -> {
                    item.type = 1
                    item.name = "拆借申请人"
                    if (update) {
                        item.content = borrowInfo.borrowProposer ?: ""
                    } else {
                        item.content = Hawk.get<String>("UserRealName")
                    }
                }
                8 -> {
                    item.type = 4
                    item.name = "拆借备注"
                    item.position = 200
                    item.content = borrowInfo.borrowRemark ?: ""
                }
            }
            list.add(item)
        }
        return list
    }
}