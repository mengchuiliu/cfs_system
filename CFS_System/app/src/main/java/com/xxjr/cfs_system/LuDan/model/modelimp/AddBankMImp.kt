package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import entity.CommonItem
import java.util.HashMap

class AddBankMImp : ModelImp() {

    //通用参数组装
    override fun getParam(list: List<Any>, tranName: String): String {
        val map = HashMap<String, Any>()
        map.put("DBMarker", "DB_CFS_Loan")
        map.put("Marker", "HQServer")
        map.put("IsUseZip", false)
        map.put("Action", "ADD")
        map.put("Function", "Pager")
        map.put("ParamString", list)
        map.put("TranName", tranName)
        Logger.e("==银行卡参数==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    fun getListData(): MutableList<CommonItem<Any>> {
        val list: MutableList<CommonItem<Any>> = ArrayList()
        var item: CommonItem<Any>
        for (i in 0..4) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.type = 0
                    item.name = "持卡人    "
                    item.content = Hawk.get("UserRealName") ?: ""
                    item.isEnable = false
                }
                1 -> {
                    item.type = 2
                    item.name = "卡号    "
                    item.hintContent = "银行卡号"
                }
                2 -> {
                    item.type = 0
                    item.name = "支行信息"
                    item.hintContent = "地区+支行名称"
                }
                3 -> {
                    item.type = 0
                    item.name = "身份证    "
                    item.hintContent = "身份证卡号"
                }
                4 -> {
                    item.type = 1
                    item.name = "是否设为默认卡"
                    item.isLineShow = false
                }
            }
            list.add(item)
        }
        return list
    }
}