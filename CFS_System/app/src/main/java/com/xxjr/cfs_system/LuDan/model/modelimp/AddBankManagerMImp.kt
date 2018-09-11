package com.xxjr.cfs_system.LuDan.model.modelimp

import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.tools.Utils
import entity.BankManager
import entity.CommonItem

/**
 * Created by Administrator on 2017/12/1.
 */
class AddBankManagerMImp : ModelImp() {
    fun getAddData(manager: BankManager?): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var item: CommonItem<Any>
        for (i in 0..7) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.type = 1
                    item.name = "银行经理名称"
                    item.content = manager?.bankManagerName ?: ""
                    item.hintContent = "请填写"
                }
                1 -> {
                    item.type = 0
                    item.name = "所属银行"
                    item.content = manager?.belongBankName ?: ""
                }
                2 -> {
                    item.type = 1
                    item.name = "联系电话1"
                    item.content = manager?.phone1 ?: ""
                    item.hintContent = "请填写"
                    item.isClick = true
                }
                3 -> {
                    item.type = 1
                    item.name = "联系电话2"
                    item.content = manager?.phone2 ?: ""
                    item.hintContent = "请填写"
                    item.isClick = true
                }
                4 -> {
                    item.type = 1
                    item.name = "支行名称"
                    item.content = manager?.branchBankName ?: ""
                    item.hintContent = "请填写"
                }
                5 -> {
                    item.type = 1
                    item.name = "推荐人姓名"
                    item.content = manager?.recommendedName ?: ""
                    item.hintContent = "请填写"
                }
                6 -> {
                    item.type = 0
                    item.name = "地区"
                    item.content = Utils.getTypeValue(Utils.getTypeDataList("ZoneType"), manager?.zoneId ?: 0)
                }
                7 -> {
                    item.type = 2
                    item.name = "备注"
                    item.content = manager?.remark ?: ""
                    item.position = 50
                }
            }
            list.add(item)
        }
        return list
    }
}