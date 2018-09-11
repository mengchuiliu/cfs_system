package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.services.CacheProvide
import entity.ChooseType
import entity.CommonItem
import entity.LoanInfo

class UpdateScheduleMImp : ModelImp() {

    fun getItemData(loanInfo: LoanInfo, isback: Boolean, newLend: Boolean): MutableList<CommonItem<Any>> {
        val list: MutableList<CommonItem<Any>> = ArrayList()
        if (!isback) {
            when (loanInfo.scheduleId) {
                1 -> {
                    val item = CommonItem<Any>()
                    item.type = 1
                    item.name = "银行经理"
                    item.isClick = true
                    list.add(item)
                }
                3, 103 -> list.addAll(getApprovalItem())
                4, 108 -> {
                    list.addAll(getLendingItem())
                    if (newLend && !isback) {
                        var item1: CommonItem<Any>
                        for (i in 0..4) {
                            item1 = CommonItem()
                            when (i) {
                                0 -> {
                                    item1.type = 2
                                    item1.name = "年利率"
                                    item1.isEnable = false
                                }
                                1 -> {
                                    item1.type = 1
                                    item1.name = "还款类型"
                                    item1.isClick = true
                                }
                                2 -> {
                                    item1.type = 2
                                    item1.name = "其他月供"
                                    item1.isClick = true
                                    item1.hintContent = "请输入金额"
                                }
                                3 -> {
                                    item1.type = 2
                                    item1.name = "还款期数"
                                    item1.isLineShow = true
                                }
                                4 -> {
                                    item1.type = 0
                                    item1.name = "其他月供说明"
                                    item1.position = 100
                                }
                            }
                            list.add(item1)
                        }
                    }
                }
            }
        }
        val item = CommonItem<Any>()
        item.type = 0
        item.name = "备注"
        item.position = 100
        list.add(item)

        if (newLend && !isback) {
            list.add(CommonItem<Any>().apply { type = 4 })
            list.add(CommonItem<Any>().apply { type = 3 })
        }
        return list
    }

    //批复界面
    private fun getApprovalItem(): MutableList<CommonItem<Any>> {
        val list: MutableList<CommonItem<Any>> = ArrayList()
        var item: CommonItem<Any>
        for (i in 0..4) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.type = 2
                    item.name = "批复金额"
                    item.isClick = true
                    item.hintContent = "请输入金额"
                }
                1 -> {
                    item.type = 2
                    item.name = "收 款 人"
                    item.hintContent = "收款人姓名"
                }
                2 -> {
                    item.type = 2
                    item.name = "收款账号"
                    item.hintContent = "收款人账号"
                }
                3 -> {
                    item.type = 2
                    item.name = "提 供 者"
                    item.hintContent = "提供者姓名"
                }
                4 -> {
                    item.type = 1
                    item.name = "批复日期"
                    item.isClick = true
                }
            }
            list.add(item)
        }
        return list
    }

    //放款界面
    private fun getLendingItem(): MutableList<CommonItem<Any>> {
        val list: MutableList<CommonItem<Any>> = ArrayList()
        var item: CommonItem<Any>
        for (i in 0..4) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.type = 2
                    item.name = "放款金额"
                    item.isClick = true
                    item.hintContent = "请输入金额"
                }
                1 -> {
                    item.type = 1
                    item.name = "放款日期"
                    item.isClick = true
                }
                2 -> {
                    item.type = 2
                    item.name = "月供金额"
                    item.isClick = true
                    item.hintContent = "请输入金额"
                }
                3 -> {
                    item.type = 2
                    item.name = "月还款日"
                    item.hintContent = "还款日"
                    item.isLineShow = true
                }
                4 -> {
                    item.type = 1
                    item.name = "还款期限"
                    item.isClick = true
                }
            }
            list.add(item)
        }
        return list
    }

    //获取还本  还息  还款类型等数据
    fun getChooseDatas(jsonArray: JSONArray): MutableList<ChooseType> {
        val list = mutableListOf<ChooseType>()
        for (i in jsonArray.indices) {
            val jsonObject = jsonArray.getJSONObject(i)
            val value = jsonObject.getIntValue("Value")
            list.add(ChooseType().apply {
                id = value
                content = jsonObject.getString("Name")
            })
        }
        return list
    }
}