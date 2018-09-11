package com.xxjr.cfs_system.LuDan.model.modelimp

import com.xxjr.cfs_system.LuDan.model.ModelImp
import entity.CommonItem

/**
 * Created by Administrator on 2017/11/24.
 */
class PostMImp : ModelImp() {

    fun getRVData(customer: String): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var item: CommonItem<Any>
        for (i in 0..2) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.type = 0
                    item.position = 0
                    item.name = "客户姓名"
                    item.content = customer
                }
                1 -> {
                    item.type = 0
                    item.position = 1
                    item.name = "资料类型"
                }
                2 -> {
                    item.type = 1
                }
            }
            list.add(item)
        }
        return list
    }
}