package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.GoldRegisteredInfo
import java.util.HashMap

/**
 * Created by Administrator on 2018/3/29.
 */
class CustomerAccountMImp : ModelImp() {
    fun getCustomerParam(): String {
        val map = HashMap<String, Any>()
        map["IsUseZip"] = false
        map["Function"] = ""
        map["TranName"] = "GoldAccount"
        map["Action"] = "GetGoldAccountListInfoByUserId"
        map["ParamString"] = ""
        Logger.e("==客户账户列表==>%s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    fun getCustomerData(jsonArray: JSONArray): MutableList<GoldRegisteredInfo> {
        val list = mutableListOf<GoldRegisteredInfo>()
        var commonItem: GoldRegisteredInfo
        for (i in jsonArray.indices) {
            commonItem = GoldRegisteredInfo()
            val jsonObject = jsonArray.getJSONObject(i)
            commonItem.customerName = jsonObject.getString("CstmNm")
            commonItem.telPhone = jsonObject.getString("PhoneNo")
            commonItem.bankName = jsonObject.getString("BankName")
            commonItem.bankNo = jsonObject.getString("BankCardNo")
            commonItem.email = jsonObject.getString("Email")
            commonItem.IDCardNo = jsonObject.getString("IDCardNo")
            commonItem.insertTime = Utils.FormatTime(jsonObject.getString("InsertTime"), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd   HH:mm:ss")
            list.add(commonItem)
        }
        return list
    }
}