package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.tools.DateUtil
import entity.CommonItem
import entity.PersonalInfo
import java.util.*

/**
 * Created by Administrator on 2018/3/20.
 */
class AddVisitMImp : ModelImp() {
    fun getVisitParam(json: String): String {
        val map = HashMap<String, Any>()
        map["IsUseZip"] = false
        map["Function"] = "VisitorRegister"
        map["ParamString"] = ""
        map["Json"] = json
        map["Action"] = ""
        map["TranName"] = "VisitMgr"
        Logger.e("==通用数据参数==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }


    fun getVisitData(salesName: String, salesPhoneNumber: String): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var item: CommonItem<Any>
        for (i in 0..7) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.type = 0
                    item.name = "来访时间"
                    item.content = DateUtil.getFormatDateHH(Date())
                }
                1 -> {
                    item.type = 1
                    item.name = "公司名称"
                    item.content = Hawk.get("CompanyName", "")
                    item.isEnable = false
                }
                2 -> {
                    item.type = 1
                    item.name = "客户名称"
                    item.hintContent = "请输入客户名称"
                    item.isLineShow = true
                }
                3 -> {
                    item.type = 1
                    item.name = "客户手机号码"
                    item.hintContent = "请输入手机号"
                }
                4 -> {
                    item.type = 3
                    item.name = "需求贷款金额"
                    item.hintContent = "请输入金额"
                }
                5 -> {
                    item.type = 0
                    item.name = "贷款类型"
                }
                6 -> {
                    item.type = 1
                    item.name = "业务员"
                    item.content = salesName
                    item.isEnable = false
                }
                7 -> {
                    item.type = 1
                    item.name = "业务员手机号码"
                    item.content = salesPhoneNumber
                }
            }
            list.add(item)
        }
        return list
    }
}