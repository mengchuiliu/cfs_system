package com.xxjr.cfs_system.LuDan.model.modelimp

import com.xxjr.cfs_system.LuDan.model.ModelImp
import entity.Bank
import entity.CommonItem

/**
 * Created by Administrator on 2017/10/18.
 */
class UnBindBankMImp : ModelImp() {

    fun getListData(bank: Bank): MutableList<CommonItem<Any>> {
        val list: MutableList<CommonItem<Any>> = ArrayList()
        var item: CommonItem<Any>
        for (i in 0..3) {
            item = CommonItem()
            item.isEnable = false
            when (i) {
                0 -> {
                    item.type = 0
                    item.name = "持卡人"
                    item.content = if (bank.owner.isNullOrBlank()) "" else bank.owner
                }
                1 -> {
                    item.type = 2
                    item.name = "卡号"
                    item.content = if (bank.owner.isNullOrBlank()) "" else bank.owner
                }
                2 -> {
                    item.type = 0
                    item.name = "身份证"
                    item.content = if (bank.owner.isNullOrBlank()) "" else bank.owner
                }
                3 -> {
                    item.type = 1
                    item.name = "是否为默认卡"
                    item.isClick = bank.isDefault
                    item.isLineShow = true
                }
            }
            list.add(item)
        }
        return list
    }
}