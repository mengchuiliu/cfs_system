package com.xxjr.cfs_system.LuDan.model.modelimp

import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import entity.Bank
import java.util.HashMap

/**
 * Created by Administrator on 2017/10/18.
 */
class BindBankMImp : ModelImp() {

    fun getParam(list: List<Any>, tranName: String, Action: String): String {
        val map = HashMap<String, Any>()
        map.put("DBMarker", "DB_CFS_Loan")
        map.put("Marker", "HQServer")
        map.put("IsUseZip", false)
        map.put("Action", Action)
        map.put("Function", "Pager")
        map.put("ParamString", list)
        map.put("TranName", tranName)
        Logger.e("==银行卡参数==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    fun getCardsData(jsonArray: JSONArray): MutableList<Bank> {
        val list: MutableList<Bank> = ArrayList()
        var bank: Bank
        for (i in jsonArray.indices) {
            bank = Bank()
            val jsonObject = jsonArray.getJSONObject(i)
            bank.bankName = jsonObject.getString("Bank")
            bank.cardType = jsonObject.getString("CardType")
            bank.bankCode = jsonObject.getString("Account")
            bank.idCard = jsonObject.getString("CardNum")
            bank.isDefault = jsonObject.getBooleanValue("IsDefault")
            bank.owner = jsonObject.getString("AccountName")
            bank.userID = jsonObject.getIntValue("UserID")
            list.add(bank)
        }
        return list
    }
}