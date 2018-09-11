package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.xxjr.cfs_system.LuDan.model.ModelImp

class LoanFliterMImp : ModelImp() {
    fun getTitles(): MutableList<String> {
        val list = ArrayList<String>()
        for (i in 0..4) {
            when (i) {
                0 -> list.add("全部")
                1 -> list.add("待申请")
                2 -> list.add("待审核")
                3 -> list.add("已结案")
                4 -> list.add("已回退")
            }
        }
        return list
    }

    fun getBorrowTitles(): MutableList<String> {
        val list = ArrayList<String>()
        for (i in 0..10) {
            when (i) {
                0 -> list.add("全部") //0
                1 -> list.add("未提交")//-99
                2 -> list.add("资料不全")//-1
                3 -> list.add("已提交")//1
                4 -> list.add("已进件待授信")//2
                5 -> list.add("已授信待审批")//3
                6 -> list.add("审批中")//4
                7 -> list.add("已拆借待放款")//5
                8 -> list.add("已放款")//6
                9 -> list.add("授信审核未通过")//-2
                10 -> list.add("审批未通过")//-3
//                0 -> list.add("""  全部  """)
//                1 -> list.add("审批中")
//                2 -> list.add("已审核")
//                3 -> list.add("已拒绝")
//                4 -> list.add("已放款")
//                5 -> list.add("已回款")
            }
        }
        return list
    }

    fun getTitles(data: String, type: Int, list: MutableList<String>): MutableList<String> {
        val jsonArray = JSON.parseArray(data)
        if (jsonArray != null && jsonArray.isNotEmpty()) {
            val jsonObject = jsonArray.getJSONObject(0)
            when (type) {
                -1 -> {
                    list.clear()
                    list.add("全部(${jsonObject.getString("AllCount")})")
                    list.add("待申请(${jsonObject.getString("PreApplyCount")})")
                    list.add("待审核(${jsonObject.getString("PreAuditCount")})")
                    list.add("已结案(${jsonObject.getString("HasCasedCount")})")
                    list.add("已回退(${jsonObject.getString("FallBackCount")})")
                }
                0 -> list.set(type, "全部(${jsonObject.getString("AllCount")})")
                1 -> list.set(type, "待申请(${jsonObject.getString("PreApplyCount")})")
                2 -> list.set(type, "待审核(${jsonObject.getString("PreAuditCount")})")
                3 -> list.set(type, "已结案(${jsonObject.getString("HasCasedCount")})")
                4 -> list.set(type, "已回退(${jsonObject.getString("FallBackCount")})")
            }
        }
        return list
    }
}