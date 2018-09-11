package com.xxjr.cfs_system.LuDan.model.modelimp

import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.CommonItem
import entity.LoanInfo
import java.math.BigDecimal
import java.util.HashMap

/**
 * Created by Administrator on 2017/11/17.
 */
class ManyLendMImp : ModelImp() {

    //多次放款列表参数
    fun AddLendParam(json: String, tranName: String, Action: String): String {
        val map = HashMap<String, Any>()
        map.put("Action", Action)
        map.put("DBMarker", "DB_CFS_Loan")
        map.put("Marker", "HQServer")
        map.put("IsUseZip", false)
        map.put("Json", json)
        map.put("TranName", tranName)
        Logger.e("==多次放款==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    //放款界面
    fun getLendingItem(loanInfo: LoanInfo): MutableList<CommonItem<Any>> {
        val list: MutableList<CommonItem<Any>> = ArrayList()
        var item: CommonItem<Any>
        for (i in 0..10) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.type = 2
                    item.name = "放款金额"
                    item.isClick = true
                    item.hintContent = "金额"
                    item.content = Utils.parseTwoMoney(BigDecimal(loanInfo.lendingAmount))
                }
                1 -> {
                    item.type = 1
                    item.name = "放款日期"
                    item.isClick = true
                    item.content = loanInfo.lendingTime ?: ""
                }
                2 -> {
                    item.type = 2
                    item.name = "月供金额"
                    item.isClick = true
                    item.hintContent = "金额"
                    item.content = Utils.parseTwoMoney(BigDecimal(loanInfo.monthAmount))
                }
                3 -> {
                    item.type = 2
                    item.name = "月还款日"
                    item.hintContent = "还款日"
                    item.isLineShow = true
                    item.content = loanInfo.monthDate ?: ""
                }
                4 -> {
                    item.type = 1
                    item.name = "还款期限"
                    item.isClick = true
                    item.content = loanInfo.returnTime ?: ""
                }
                5 -> {
                    item.type = 2
                    item.name = "年利率"
                    item.isEnable = false
                    item.content = Utils.parseTwoMoney(BigDecimal(loanInfo.interestRate))
                }
                6 -> {
                    item.type = 1
                    item.name = "还款类型"
                    item.isClick = true
                    item.content = loanInfo.paymentName
                }
                7 -> {
                    item.type = 2
                    item.name = "其他月供"
                    item.isClick = true
                    item.hintContent = "金额"
                    item.content = Utils.parseTwoMoney(BigDecimal(loanInfo.otherAmount))
                }
                8 -> {
                    item.type = 2
                    item.name = "还款期数"
                    item.isLineShow = true
                    item.content = "${loanInfo.lendingPeriod}"
                }
                9 -> {
                    item.type = 0
                    item.name = "其他月供说明"
                    item.position = 100
                    item.content = loanInfo.otherPayRemark ?: ""
                }
                10 -> {
                    item.type = 0
                    item.name = "备注"
                    item.position = 100
                    item.content = loanInfo.note ?: ""
                }
            }
            list.add(item)
        }
        list.add(CommonItem<Any>().apply { type = 4 })
        list.add(CommonItem<Any>().apply { type = 3 })
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