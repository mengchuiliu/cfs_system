package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.xxjr.cfs_system.LuDan.model.ModelImp
import entity.BankManager
import java.util.ArrayList
import java.util.regex.Pattern

/**
 * Created by Administrator on 2017/12/1.
 */
class BankListMImp : ModelImp() {
    fun getManagerList(data: String): MutableList<BankManager> {
        val temp = ArrayList<BankManager>()
        val jsonArray = JSON.parseArray(data)
        if (jsonArray != null && jsonArray.size != 0) {
            var manager: BankManager
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                manager = BankManager()
                manager.id = jsonObject.getIntValue("ID")
                manager.bankManagerName = jsonObject.getString("BankManagerName")
                manager.belongBankId = jsonObject.getIntValue("BelongBank")
                manager.belongBankName = jsonObject.getString("BelongBankName")
                manager.phone1 = jsonObject.getString("Phone1")
                manager.phone2 = jsonObject.getString("Phone2")
                manager.rate = jsonObject.getString("Rate")
                manager.branchBankName = jsonObject.getString("BranchBankName")
                manager.recommendedName = jsonObject.getString("RecommendedName")
                val zone = jsonObject.getString("Zone")
                if (!zone.isNullOrBlank()) {
                    if (isNumeric(zone)) {
                        manager.zoneId = jsonObject.getIntValue("Zone")
                    }
                }
                manager.remark = jsonObject.getString("Remark")
                temp.add(manager)
            }
        }
        return temp
    }

    fun isNumeric(str: String): Boolean {
        val pattern = Pattern.compile("[0-9]*")
        val isNum = pattern.matcher(str)
        return isNum.matches()
    }
}