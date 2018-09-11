package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import java.util.HashMap

/**
 * Created by Administrator on 2018/4/9.
 */
class TransferReceivableMImp : ModelImp() {
    fun getDataParam(tranName: String, action: String): String {
        val map = HashMap<String, Any>()
        map["Action"] = action
        map["IsUseZip"] = false
        map["Function"] = ""
        map["ParamString"] = ""
        map["TranName"] = tranName
        Logger.e("==待回款转账==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    fun getTransferParam(tranName: String, list: MutableList<Any>, json: String): String {
        val map = HashMap<String, Any>()
        map["Action"] = ""
        map["DBMarker"] = "DB_CFS_Loan"
        map["IsUseZip"] = false
        map["Function"] = ""
        map["Json"] = json
        map["ParamString"] = list
        map["TranName"] = tranName
        Logger.e("==回调待回款转账==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    fun getCodeData(jsonArray: JSONArray): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        for (i in jsonArray.indices) {
            val jsonObject = jsonArray.getJSONObject(i)
            list.add(CommonItem<Any>().apply {
                content = jsonObject.toJSONString()
                hintContent = jsonObject.getString("CompanyName") ?: ""
                payType = jsonObject.getString("RechargeCode")
                percent = jsonObject.getDoubleValue("Amount")
                date = Utils.getTimeFormat(jsonObject.getString("RechargeDate"))
            })
        }
        return list
    }

    fun getTransferData(jsonArray: JSONArray): MutableList<CommonItem<Any>> {
        val commonItems = mutableListOf<CommonItem<Any>>()
        for (i in jsonArray.indices) {
            val jsonObject = jsonArray.getJSONObject(i)
            commonItems.add(CommonItem<Any>().apply {
                type = 1
                position = i
                name = jsonObject.getString("AccountInfo")
                hintContent = jsonObject.getString("CompanyId") ?: ""//门店id
                remark = jsonObject.getString("UserNo") ?: ""//金账户账号
                content = jsonObject.getString("ProtocolID") ?: ""//协议id
            })
            val jsonArray1 = jsonObject.getJSONArray("LoanInfo")
            if (jsonArray1 != null && jsonArray1.isNotEmpty()) {
                for (j in jsonArray1.indices) {
                    val jsonObject1 = jsonArray1.getJSONObject(j)
                    commonItems.add(CommonItem<Any>().apply {
                        type = 2
                        position = i
                        content = jsonObject1.getString("LoanTypeInfo")
                        percent = jsonObject1.getDoubleValue("Amount")
                        payType = jsonObject1.getString("BookType")
                        name = jsonObject1.getString("CustomerNames")
                        hintContent = jsonObject1.getString("Salesman")
                        remark = jsonObject1.getString("Id") ?: ""//回款id
                        if (j == jsonArray1.size - 1) {
                            isLineShow = true
                        }
                    })
                }
            }
            commonItems.add(CommonItem<Any>().apply { type = 0 })
        }
        return commonItems
    }
}