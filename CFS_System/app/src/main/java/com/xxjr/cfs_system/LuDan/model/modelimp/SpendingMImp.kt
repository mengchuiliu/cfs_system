package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import java.util.HashMap

class SpendingMImp : ModelImp() {
    //通用参数组装
    fun getSpendParam(list: List<Any>, tranName: String): String {
        val map = HashMap<String, Any>()
        map["Action"] = "GetPayoutList"
        map["IsUseZip"] = false
        map["Function"] = ""
        map["ParamString"] = list
        map["TranName"] = tranName
        Logger.e("==支出审核==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    fun getAuditState(auditPos: Int): String = when (auditPos) {
        0 -> ""
        1 -> "1"
        2 -> "2"
        3 -> "-1"
        4 -> "-3"
        5 -> "-2"
        else -> ""
    }
}