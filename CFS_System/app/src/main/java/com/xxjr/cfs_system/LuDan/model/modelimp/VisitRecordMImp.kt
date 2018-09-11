package com.xxjr.cfs_system.LuDan.model.modelimp

import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.tools.Utils
import entity.VisitRecord
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Administrator on 2017/12/7.
 */
class VisitRecordMImp : ModelImp() {
    fun getFunctionParam(list: List<Any>, tranName: String, Function: String, DBMarker: String): String {
        val map = HashMap<String, Any>()
        map.put("DBMarker", DBMarker)
        map.put("Marker", "HQServer")
        map.put("IsUseZip", false)
        map.put("Action", "Default")
        map.put("Function", Function)
        map.put("ParamString", list)
        map.put("TranName", tranName)
        Logger.e("==来访参数==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    //客户反馈
    fun getFeedbackData(jsonArray: JSONArray) = mutableListOf<VisitRecord>().apply {
        if (jsonArray.isNotEmpty()) {
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                add(VisitRecord().apply {
                    CompanyName = "客户：${(jsonObject.getString("Phone")
                            ?: "").replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2")}"
                    VisitTime = jsonObject.getString("InsertTime")
                    CustomerName = "公司：${jsonObject.getString("CompanyName") ?: ""}"
                    SalesName = jsonObject.getString("Text") ?: ""
                })
            }
        }
    }

    //来访邀请
    fun getInviteData(jsonArray: JSONArray) = mutableListOf<VisitRecord>().apply {
        if (jsonArray.isNotEmpty()) {
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                add(VisitRecord().apply {
                    CompanyName = jsonObject.getString("CompanyName") ?: ""
                    InsertTime = Utils.FormatTime(jsonObject.getString("InsertTime"), "yyyy-MM-dd'T'HH:mm:ss", "yyyy.MM.dd HH:mm")
                    VisitTime = Utils.FormatTime(jsonObject.getString("VisitTime"), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm")
                    CustomerName = jsonObject.getString("CustomerName") ?: ""
                    MobilePhone = jsonObject.getString("CustomerPhone") ?: ""
                    SalesName = jsonObject.getString("OperatorName") ?: ""
                    SalesPhoneNumber = jsonObject.getString("OperatorPhone") ?: ""
                    remark = jsonObject.getString("Remark")
                })
            }
        }
    }
}