package com.xxjr.cfs_system.LuDan.model.modelimp

import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import entity.CommonItem
import java.util.HashMap

/**
 * Created by Administrator on 2017/12/13.
 */
class VisitDetailMImp : ModelImp() {
    fun getFunctionParam(list: List<Any>, tranName: String, Function: String): String {
        val map = HashMap<String, Any>()
        map.put("DBMarker", "DB_CFS_Loan")
        map.put("Marker", "HQServer")
        map.put("IsUseZip", false)
        map.put("Action", "Default")
        map.put("Function", Function)
        map.put("ParamString", list)
        map.put("TranName", tranName)
        Logger.e("==来访量统计详情==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    fun getStoreDatas(jsonArray: JSONArray): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        val comm = CommonItem<Any>()
        comm.name = "来访时间"
        comm.content = "来访人数"
        comm.isLineShow = true
        list.add(comm)
        if (jsonArray.size != 0) {
            var item: CommonItem<Any>
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                item = CommonItem()
                item.name = jsonObject.getString("VisitTime")
                item.position = jsonObject.getIntValue("VisitPersons")
                list.add(item)
            }
        }
        return list
    }

    fun getClerkDatas(jsonArray: JSONArray): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        val comm = CommonItem<Any>()
        comm.name = "业务员"
        comm.content = "来访人数"
        comm.isLineShow = true
        list.add(comm)
        if (jsonArray.size != 0) {
            var item: CommonItem<Any>
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                item = CommonItem()
                item.name = jsonObject.getString("SalesName")
                item.position = jsonObject.getIntValue("VisitPersons")
                list.add(item)
            }
        }
        return list
    }
}