package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.tools.Utils
import entity.BorrowInfo
import java.util.ArrayList
import java.util.HashMap

class BorrowListMImp : ModelImp() {
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

    fun getBorrowList(data: String): MutableList<BorrowInfo> {
        val temp = ArrayList<BorrowInfo>()
        val jsonArray = JSON.parseArray(data)
        if (jsonArray != null && jsonArray.size != 0) {
            var borrowInfo: BorrowInfo
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                borrowInfo = BorrowInfo()
                borrowInfo.borrowId = jsonObject.getIntValue("LendId")
                borrowInfo.borrowState = jsonObject.getIntValue("LendingState")
                borrowInfo.customerIds = jsonObject.getString("CustomerIds")
                borrowInfo.customerNames = jsonObject.getString("CustomerNames")
                borrowInfo.borrowAmount = jsonObject.getDoubleValue("Amount")
                borrowInfo.borrowProposer = jsonObject.getString("ServiceName")
                borrowInfo.borrowDate = if (jsonObject.getString("LendingDate") == null) "" else Utils.getTime(jsonObject.getString("LendingDate"))
                borrowInfo.borrowRemark = jsonObject.getString("LendAuditRemark")
                temp.add(borrowInfo)
            }
        }
        return temp
    }

}