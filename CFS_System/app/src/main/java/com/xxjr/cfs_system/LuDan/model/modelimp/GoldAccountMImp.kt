package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.orhanobut.logger.Logger
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.model.ModelImp
import entity.CommonItem

/**
 * Created by Administrator on 2018/3/12.
 */
class GoldAccountMImp : ModelImp() {

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

    fun getAmountData(jsonObject: JSONObject?): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var item: CommonItem<Any>
        for (i in 0..1) {
            item = CommonItem()
            when (i) {
                0 -> item.apply {
                    icon = R.mipmap.available_amount
                    name = "可用金额(元)"
                    percent = if (jsonObject != null && jsonObject.isNotEmpty()) jsonObject.getDoubleValue("UsableAmt") else 0.00
                }
                1 -> item.apply {
                    icon = R.mipmap.frozen_amount
                    name = "冻结金额(元)"
                    percent = if (jsonObject != null && jsonObject.isNotEmpty()) jsonObject.getDoubleValue("FreezeAmt") else 0.00
                }
//                2 -> item.apply {
//                    icon = R.mipmap.outstanding_amount
//                    name = "未转结金额(元)"
//                    percent = if (jsonObject != null && jsonObject.isNotEmpty()) jsonObject.getDoubleValue("UnSettledAmt") else 0.00
//                    isEnable = false
//                }
            }
            list.add(item)
        }
        return list
    }
}