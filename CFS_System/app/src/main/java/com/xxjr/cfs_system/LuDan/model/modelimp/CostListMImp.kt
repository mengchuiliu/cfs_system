package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.Cost
import entity.LoanInfo
import java.util.ArrayList

class CostListMImp : ModelImp() {
    fun getCostList(data: String): MutableList<Cost> {
        val temp = ArrayList<Cost>()
        val jsonArray = JSON.parseArray(data)
        if (jsonArray != null && jsonArray.size != 0) {
            var cost: Cost
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                cost = Cost()
                cost.loanCostId = jsonObject.getIntValue("LoanCostId")
                cost.loanCode = jsonObject.getString("LoanCode")
                cost.clerkName = jsonObject.getString("ClerkName")
                cost.customerName = jsonObject.getString("CustomerNames")
                cost.bankId = jsonObject.getIntValue("BankId")
                cost.loanType = jsonObject.getIntValue("LoanType")
                cost.productId = jsonObject.getIntValue("ProductId")
                cost.costType = jsonObject.getIntValue("CostType")
                cost.money = jsonObject.getDoubleValue("Money")
                cost.happenDate = jsonObject.getString("HappenDate")
                cost.serviceName = jsonObject.getString("ServiceName")
                cost.operateTime = jsonObject.getString("OperateTime")
                cost.auditStatus = jsonObject.getIntValue("AuditStatus")
                cost.applyMoney = jsonObject.getDoubleValue("ApplyValue")
                cost.replyMoney = jsonObject.getDoubleValue("A1")
                cost.remark = jsonObject.getString("Remark")
                cost.auditRemark = jsonObject.getString("AuditRemark")
                cost.auditorName = jsonObject.getString("AuditorName")
                cost.auditTime = jsonObject.getString("AuditTime")
                cost.auditorManager = jsonObject.getString("AuditorManager")
                cost.auditorManager2 = jsonObject.getString("AuditorManager2")
                cost.managerAuditDateTime = jsonObject.getString("ManagerAuditDateTime")
                cost.managerAuditDateTime2 = jsonObject.getString("ManagerAuditDateTime2")
                cost.accounterName = jsonObject.getString("AccounterName")
                cost.accounterTime = jsonObject.getString("AccounterAuditDateTime")
                cost.accounterAuditRemark = jsonObject.getString("AccounterAuditRemark")
                cost.paidTime = jsonObject.getString("PassPayTime")
                cost.paidName = jsonObject.getString("PassPayName")
                temp.add(cost)
            }
        }
        return temp
    }

    //刷选条件
    fun getQuery(schedulePos: Int, auditPos: Int): String {
        val builder = StringBuilder("")
        when (schedulePos) {
            1 -> builder.append(" and ReimbursementStatus = '3' ")
            2 -> builder.append(" and (ReimbursementStatus = '0' OR ReimbursementStatus IS NULL) ")
            3 -> builder.append(" and ReimbursementStatus = '1' ")
            4 -> builder.append(" and ReimbursementStatus = '2' ")
        }
        when (auditPos) {
            1 -> builder.append(" and AuditStatus = '0' ")
            2 -> builder.append(" and AuditStatus = '2' ")
            3 -> builder.append(" and AuditStatus = '1' ")
            4 -> builder.append(" and AuditStatus = '4' ")
            5 -> builder.append(" and (AuditStatus = 6 AND Cost >= ${CFSUtils.getAuditCost().toDouble()}) ")//待特定人审核
            6 -> builder.append(" and AuditStatus = '9' ")
            7 -> builder.append(" and ((AuditStatus = 6 AND Cost < ${CFSUtils.getAuditCost().toDouble()}) OR AuditStatus = 8)")//待会计审核
            8 -> builder.append(" and AuditStatus = '7' ")
            9 -> builder.append(" and AuditStatus = '3' ")
            10 -> builder.append(" and AuditStatus = '5' ")
        }
        return builder.toString()
    }

    //    {"全部":616,"待门店经理审核":6,"会计审核通过":453,"待按揭经理审核":1,"门店经理审核拒绝":79,
    // "按揭经理审核拒绝":13,"总部财务添加":49,"待会计审核":9,"会计审核拒绝":6,"未付款":400,"已付款":53,"审核中":5,"无需报销":158}
    fun getTitleData0(jsonObject: JSONObject, schedulePos: Int): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var commonItem: CommonItem<*>
        for (i in 0..4) {
            commonItem = CommonItem<Any>()
            commonItem.isClick = (i == schedulePos)
            when (i) {
                0 -> commonItem.setName("全部(${jsonObject.getIntValue("全部")})")
                1 -> commonItem.setName("审核中(${jsonObject.getIntValue("审核中")})")
                2 -> commonItem.setName("无需报销(${jsonObject.getIntValue("无需报销")})")
                3 -> commonItem.setName("未付款(${jsonObject.getIntValue("未付款")})")
                4 -> commonItem.setName("已付款(${jsonObject.getIntValue("已付款")})")
            }
            list.add(commonItem)
        }
        return list
    }

    fun getTitleData1(jsonObject: JSONObject, auditPos: Int): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var commonItem: CommonItem<*>
        for (i in 0..10) {
            commonItem = CommonItem<Any>()
            commonItem.isClick = (i == auditPos)
            when (i) {
                0 -> commonItem.setName("全部(${jsonObject.getIntValue("全部")})")
                1 -> commonItem.setName("待门店经理审核(${jsonObject.getIntValue("待门店经理审核")})")
                2 -> commonItem.setName("门店经理审核拒绝(${jsonObject.getIntValue("门店经理审核拒绝")})")
                3 -> commonItem.setName("待按揭经理审核(${jsonObject.getIntValue("待按揭经理审核")})")
                4 -> commonItem.setName("按揭经理审核拒绝(${jsonObject.getIntValue("按揭经理审核拒绝")})")
                5 -> commonItem.setName("待特定人审核(${jsonObject.getIntValue("待特定人审核")})")
                6 -> commonItem.setName("特定人审核拒绝(${jsonObject.getIntValue("特定人审核拒绝")})")
                7 -> commonItem.setName("待会计审核(${jsonObject.getIntValue("待会计审核")})")
                8 -> commonItem.setName("会计审核拒绝(${jsonObject.getIntValue("会计审核拒绝")})")
                9 -> commonItem.setName("会计审核通过(${jsonObject.getIntValue("会计审核通过")})")
                10 -> commonItem.setName("总部财务添加(${jsonObject.getIntValue("总部财务添加")})")
            }
            list.add(commonItem)
        }
        return list
    }
}