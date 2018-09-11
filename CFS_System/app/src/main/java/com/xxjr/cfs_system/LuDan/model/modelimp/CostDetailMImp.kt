package com.xxjr.cfs_system.LuDan.model.modelimp

import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.Cost
import entity.LoanInfo
import java.math.BigDecimal
import java.util.HashMap

class CostDetailMImp : ModelImp() {

    fun getSaveParam(list: MutableList<*>): String {
        val map = HashMap<String, Any>()
        map.put("Action", "ADD")
        map.put("DBMarker", "CFS_Loan")
        map.put("Marker", "HQServer")
        map.put("Function", "de36151d-1492-483e-9b92-76d07c84148b")
        map.put("ParamString", list)
        map.put("TranName", "VDG")
        return JSON.toJSONString(map)
    }

    fun getItemData(loanInfo: LoanInfo, isAdd: Boolean, costList: MutableList<*>): MutableList<CommonItem<*>> {
        val list: MutableList<CommonItem<*>> = ArrayList()
        var commonItem: CommonItem<*>
        for (i in 0..9) {
            commonItem = CommonItem<Any>()
            when (i) {
                0 -> commonItem.type = 0
                1 -> {
                    commonItem.type = 1
                    commonItem.name = "贷款信息" + "【" + loanInfo.getBankName() + "·" + loanInfo.getProductName() +
                            "(" + (if (TextUtils.isEmpty(loanInfo.getLoanTypeName())) "" else loanInfo.getLoanTypeName().substring(0, 1)) + ")" + "】"
                    commonItem.icon = R.mipmap.icon_loan
                }
                2 -> {
                    commonItem.type = 2
                    commonItem.name = "贷款编号："
                    commonItem.content = loanInfo.loanCode
                }
                3 -> {
                    commonItem.type = 2
                    commonItem.name = "申请金额："
                    commonItem.content = "${Utils.parseMoney(BigDecimal(loanInfo.amount))}元"
                }
                4 -> {
                    commonItem.type = 2
                    commonItem.name = """ 业 务 员："""
                    commonItem.content = loanInfo.clerkName ?: ""
                    commonItem.isClick = true
                }
                5 -> {
                    commonItem.type = 2
                    commonItem.name = """申请客户："""
                    commonItem.content = loanInfo.customer
                }
                6 -> {
                    commonItem.type = 2
                    commonItem.name = """ 按 揭 员 ："""
                    commonItem.content = CacheProvide.getMortgageName(loanInfo.getMortgage())
                }
                7 -> {
                    commonItem.type = 2
                    commonItem.name = "最新进度："
                    commonItem.content = loanInfo.getSchedule()
                }
                8 -> commonItem.type = 0
                9 -> commonItem.type = 0
            }
            list.add(commonItem)
        }
        if (isAdd) {
            list.addAll(getCostRecord(loanInfo))
        } else {
            list.addAll(getCostAudit(loanInfo, costList))
        }
        return list
    }

    //录入
    private fun getCostRecord(loanInfo: LoanInfo): MutableList<CommonItem<*>> {
        val list: MutableList<CommonItem<*>> = ArrayList()
        var commonItem: CommonItem<*>
        for (i in 0..5) {
            commonItem = CommonItem<Any>()
            when (i) {
                0 -> {
                    commonItem.type = 3
                    commonItem.name = "合同编号"
                    commonItem.content = loanInfo.pactCode
                }
                1 -> {
                    commonItem.type = 3
                    commonItem.name = """ 操作员 """
                    commonItem.content = Hawk.get("UserRealName", "")
                }
                2 -> {
                    commonItem.type = 3
                    commonItem.name = "成本类型"
                    commonItem.isClick = true
                }
                3 -> {
                    commonItem.type = 3
                    commonItem.name = "发生日期"
                    commonItem.isClick = true
                }
                4 -> {
                    commonItem.type = 4
                    commonItem.name = "成本金额"
                    commonItem.isClick = true
                    commonItem.hintContent = "金额"
                }
                5 -> {
                    commonItem.type = 6
                    commonItem.name = "成本备注"
                    commonItem.position = 200
                }
            }
            list.add(commonItem)
        }
        return list
    }

    //审核
    private fun getCostAudit(loanInfo: LoanInfo, costList: MutableList<*>): MutableList<CommonItem<*>> {
        val list: MutableList<CommonItem<*>> = ArrayList()
        var commonItem: CommonItem<*>
        for (i in 0..1) {
            commonItem = CommonItem<Any>()
            when (i) {
                0 -> {
                    commonItem.type = 1
                    commonItem.name = "【通过:${loanInfo.getPassAuditCostMoney()}元" + "  未审核:${loanInfo.getNoAuditCostMoney()}元】"
                    commonItem.icon = R.mipmap.icon_cost
                }
                1 -> {
                    commonItem.type = 5
                    commonItem.position = -1
                    commonItem.list = costList
                }
            }
            list.add(commonItem)
        }
        return list
    }

    fun getCostData(data: String): MutableList<Cost> {
        val list: MutableList<Cost> = ArrayList()
        val jsonArray = JSON.parseArray(data)
        var cost: Cost
        for (i in jsonArray.indices) {
            cost = Cost()
            val jsonObject = jsonArray.getJSONObject(i)
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
            cost.managerAuditDateTime = jsonObject.getString("ManagerAuditDateTime")
            cost.accounterName = jsonObject.getString("AccounterName")
            cost.accounterTime = jsonObject.getString("AccounterAuditDateTime")
            cost.accounterAuditRemark = jsonObject.getString("AccounterAuditRemark")
            cost.paidTime = jsonObject.getString("PassPayTime")
            cost.paidName = jsonObject.getString("PassPayName")
            list.add(cost)
        }
        return list
    }
}