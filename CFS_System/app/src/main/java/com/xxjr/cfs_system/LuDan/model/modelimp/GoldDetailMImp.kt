package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.tools.Utils
import entity.GoldTradeDetail

/**
 * Created by Administrator on 2018/3/13.
 */
class GoldDetailMImp : ModelImp() {
    //通用参数组装
    fun getDataParam(list: MutableList<Any>, Action: String): String {
        val map = hashMapOf<String, Any>()
        map["Action"] = Action
        map["DBMarker"] = "DB_CFS_Loan"
        map["Marker"] = "HQServer"
        map["IsUseZip"] = false
        map["Function"] = ""
        map["ParamString"] = list
        map["TranName"] = "GoldAccount"
        Logger.e("==通用数据参数==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    fun getDetailData(jsonArray: JSONArray): MutableList<GoldTradeDetail> {
        val list = mutableListOf<GoldTradeDetail>()
        var commonItem: GoldTradeDetail
        for (i in jsonArray.indices) {
            commonItem = GoldTradeDetail()
            val jsonObject = jsonArray.getJSONObject(i)
            commonItem.tradeSN = jsonObject.getString("TradeSN") ?: ""
            commonItem.isAdd = jsonObject.getBooleanValue("IsAdd")
            commonItem.tradeTp = jsonObject.getString("TradeTp") ?: ""
            commonItem.changeAmt = jsonObject.getDoubleValue("ChangeAmt")
            commonItem.tradeTime = Utils.FormatTime(jsonObject.getString("TradeTime"), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd")
            commonItem.bookTime = Utils.FormatTime(jsonObject.getString("BookTime"), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss")
            commonItem.description = jsonObject.getString("Description") ?: ""
            commonItem.remark = jsonObject.getString("Remark") ?: ""
            commonItem.dealInfo = jsonObject.getString("DealInfo") ?: ""
            list.add(commonItem)
        }
        return list
    }
}