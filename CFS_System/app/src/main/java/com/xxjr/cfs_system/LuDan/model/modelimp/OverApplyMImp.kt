package com.xxjr.cfs_system.LuDan.model.modelimp

import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.LoanInfo
import entity.OverApply
import entity.Schedule
import java.math.BigDecimal

class OverApplyMImp : ModelImp() {

    fun getOverApplyData(jsonObject: JSONObject): OverApply {
        val apply = OverApply()
        val contractInfo: JSONObject? = jsonObject.getJSONObject("ContractInfo")
        apply.contractId = contractInfo?.getIntValue("ContractId")
        apply.contractCode = contractInfo?.getString("ContractCode")
        apply.salesmanCompany = contractInfo?.getString("SalesmanCompany")
        apply.salesmanName = contractInfo?.getString("SalesmanName")
        apply.payment = contractInfo?.getDoubleValue("DepositMoney")
        val costObject: JSONObject? = jsonObject.getJSONObject("CostInfo")
        apply.serviceCharge = costObject?.getDoubleValue("LendSum")
        apply.income = costObject?.getDoubleValue("IncomeGeneration")
        apply.commission = costObject?.getDoubleValue("MortgageMemberCommission")
        apply.costing = costObject?.getDoubleValue("CostSum")
        apply.formula = if (TextUtils.isEmpty(costObject?.getString("MortgageMemberCommissionRemark"))) null else "提成算法--${costObject?.getString("MortgageMemberCommissionRemark")}"
        apply.costList = getCostDetail(jsonObject)
        apply.bookList = getLendDetail(jsonObject)
        apply.overList = getOverDetail(jsonObject)
        return apply
    }

    //累计成本
    private fun getCostDetail(jsonObject: JSONObject): MutableList<Schedule> {
        val schedules = java.util.ArrayList<Schedule>()
        val costDetail = jsonObject.getJSONArray("CostDetail")
        if (costDetail != null && costDetail.size != 0) {
            var schedule: Schedule
            for (i in costDetail.indices) {
                schedule = Schedule()
                val `object` = costDetail.getJSONObject(i)
                val costType = Utils.getTypeValue(Utils.getTypeDataList("LoanCostType"), `object`.getIntValue("CostType"))
                val builder = StringBuilder()
                val insertTime = if (`object`["InsertTime"] == null) "" else `object`.getString("InsertTime")
                builder.append(`object`.getString("ServiceName")).append("--").append(costType).append("：").append(`object`.getDoubleValue("Money")).append("元").append("  备注:")
                        .append(`object`.getString("Remark")).append("  ").append(if (insertTime.contains("T")) insertTime.substring(0, insertTime.indexOf("T")) else insertTime)
                schedule.status = builder.toString()
                val builder1 = StringBuilder()
                val AuditStatus = `object`.getIntValue("AuditStatus")
                when (AuditStatus) {
                    0 -> builder1.append("未审核")
                    1 -> builder1.append("经理审核通过")
                    2 -> builder1.append("门店经理审核拒绝")
                    3 -> builder1.append("会计审核通过")
                    4 -> builder1.append("按揭经理审核拒绝")
                    5 -> builder1.append("总部财务添加")
                    6 -> builder1.append("按揭经理审核通过")
                    7 -> builder1.append("财务会计审核拒绝")
                    8 -> builder1.append("特定人审核通过")
                    9 -> builder1.append("特定人审核拒绝")
                }
                builder1.append("--").append(`object`.getString("AuditorName") ?: "")
                if (AuditStatus == 2 || AuditStatus == 4 || AuditStatus == 7 || AuditStatus == 9) {
                    builder1.append("  审核备注:").append(`object`.getString("AuditRemark"))
                }
                val auditTime = if (`object`["AuditTime"] == null) "" else `object`.getString("AuditTime")
                schedule.name = builder1.toString()
                schedule.date = if (auditTime.contains("T")) auditTime.substring(0, auditTime.indexOf("T")) else auditTime
                schedules.add(schedule)
            }
        }
        return schedules
    }

    //账目信息
    private fun getLendDetail(jsonObject: JSONObject): MutableList<CommonItem<*>> {
        val commonItems = ArrayList<CommonItem<*>>()
        val lendDetail = jsonObject.getJSONArray("LendDetail")
        if (lendDetail != null && lendDetail.size != 0) {
            var commonItem: CommonItem<*>
            for (i in lendDetail.indices) {
                commonItem = CommonItem<Any>()
                val `object` = lendDetail.getJSONObject(i)
                val payTime = if (`object`["PayTime"] == null) "" else `object`.getString("PayTime")
                commonItem.hintContent = "收支类型：" + `object`.getString("BookTypeName")
                commonItem.payType = "支付方式：" + `object`.getString("PayTypeName")
                commonItem.name = "金额：${Utils.parseMoney(BigDecimal(`object`.getDoubleValue("Money")))}元"
                commonItem.content = "经办人：" + `object`.getString("ServiceName")
                commonItem.date = "收支日期：" + if (payTime.contains("T")) payTime.substring(0, payTime.indexOf("T")) else payTime
                commonItem.remark = "备  注 ：" + `object`.getString("Remark")
                commonItem.type = `object`.getIntValue("LendId")//回款id
                val state = `object`.getIntValue("State")//出入账状态0待审核和6金账户待充值都可以删
                if (`object`.getIntValue("BookType") == 3) {
                    commonItem.isClick = true//false可以删
                } else {
                    if (state != 0 && state != 6) {
                        commonItem.isClick = true
                    }
                }
                commonItems.add(commonItem)
            }
        }
        return commonItems
    }

    //申请信息
    private fun getOverDetail(jsonObject: JSONObject): MutableList<CommonItem<*>> {
        val commonItems = java.util.ArrayList<CommonItem<*>>()
        val operateArray = jsonObject.getJSONArray("OperateInfo")
        if (operateArray != null && operateArray.size > 0) {
            var commonItem: CommonItem<*>
            for (i in operateArray.indices) {
                commonItem = CommonItem<Any>()
                val `object` = operateArray.getJSONObject(i)
                val name = `object`.getString("RoleName")
                val builder = StringBuilder()
                if (!TextUtils.isEmpty(name) && name.length > 2) {
                    builder.append(name.substring(0, 2)).append("\n").append(name.substring(2, name.length))
                }
                commonItem.hintContent = builder.toString()
                commonItem.name = `object`.getString("UserName")
                commonItem.content = `object`.getString("FollowDescription")
                val OperateTime = if (`object`["OperateTime"] == null) "" else `object`.getString("OperateTime")
                commonItem.date = Utils.getTimeFormat(OperateTime)
                commonItems.add(commonItem)
            }
        }
        return commonItems
    }

    //数据源
    fun getItemData(loanInfo: LoanInfo, remarkShow: Boolean, overApply: OverApply?): MutableList<CommonItem<Any>> {
        val list: MutableList<CommonItem<Any>> = ArrayList()
        var item: CommonItem<Any>
        for (i in 0..26) {
            item = CommonItem()
            when (i) {
                0 -> item.type = 0
                1 -> {
                    item.type = 1
                    item.name = "合同信息 ${if (loanInfo.pactCode.isNullOrBlank()) overApply?.contractCode
                            ?: "" else loanInfo.pactCode}"
                    item.icon = R.mipmap.icon_compact
                }
                2 -> {
                    item.type = 2
                    item.name = "业务员："
                    item.content = "${if ((overApply?.salesmanCompany
                                    ?: "").isBlank()) CacheProvide.getStoreName(loanInfo.companyID) else overApply?.salesmanCompany}-" +
                            (overApply?.salesmanName ?: "")
                }
                3 -> {
                    item.type = 2
                    item.name = "定 金 ："
                    item.content = "${Utils.parseMoney(BigDecimal(overApply?.payment ?: 0.0))}元"
                }
                4 -> {
                    item.type = 0
                    item.isClick = true
                }
                5 -> item.type = 0
                6 -> {
                    item.type = 1
                    item.name = "贷款信息 ${loanInfo.loanCode}"
                    item.icon = R.mipmap.icon_loan
                }
                7 -> {
                    item.type = 2
                    item.name = "贷款内容："
                    item.content = "${loanInfo.bankName}.${loanInfo.productName}(" + (if (TextUtils.isEmpty(loanInfo.loanTypeName)) "" else loanInfo.loanTypeName.substring(0, 1)) + ")"
                }
                8 -> {
                    item.type = 2
                    item.name = "申请金额："
                    item.content = "${Utils.parseMoney(BigDecimal(loanInfo.amount))}元"
                }
                9 -> {
                    item.type = 2
                    item.name = " 按 揭 员 ："
                    item.content = if (loanInfo.mortgageName.isNullOrBlank()) CacheProvide.getMortgageName(loanInfo.mortgage) else loanInfo.mortgageName
                }
                10 -> {
                    item.type = 2
                    item.name = "批复金额："
                    item.content = "${Utils.parseMoney(BigDecimal(loanInfo.replyAmount))}元"
                    item.hintContent = loanInfo.approvalTime
                    item.isLineShow = true
                }
                11 -> {
                    item.type = 2
                    item.name = "放款金额："
                    item.content = "${Utils.parseMoney(BigDecimal(loanInfo.lendingAmount))}元"
                    item.hintContent = loanInfo.lendingTime
                    item.isLineShow = true
                }
                12 -> {
                    item.type = 0
                    item.isClick = true
                }
                13 -> item.type = 0
                14 -> {
                    item.type = 1
                    item.name = "成本信息"
                    item.icon = R.mipmap.icon_cost
                }
                15 -> {
                    item.type = 2
                    item.name = "服务费收入："
                    item.content = "${Utils.parseMoney(BigDecimal(overApply?.serviceCharge
                            ?: 0.0))}元"
                }
                16 -> {
                    item.type = 2
                    item.name = "实际创收："
                    item.content = "${Utils.parseMoney(BigDecimal(overApply?.income ?: 0.0))}元"
                }
                17 -> {
                    item.type = 2
//                    item.name = "按揭员提成："
                    item.name = ""
                    item.content = "${Utils.parseMoney(BigDecimal(overApply?.commission ?: 0.0))}元"
                }
                18 -> {
                    item.type = 2
//                    item.name = overApply?.formula
                    item.name = ""
                }
                19 -> {
                    item.type = 3
                    item.name = "累计成本："
                    item.content = "${Utils.parseMoney(BigDecimal(overApply?.costing ?: 0.0))}元"
                }
                20 -> {
                    item.type = 4
                    item.list = overApply?.costList
                }
                21 -> {
                    item.type = 0
                    item.isClick = true
                }
                22 -> item.type = 0
                23 -> {
                    item.type = 1
                    item.name = "合同账目信息"
                    item.icon = R.mipmap.icon_account
                }
                24 -> {
                    item.type = 4
                    item.isClick = true
                    item.list = overApply?.bookList
                }
                25 -> item.type = 0
                26 -> {
                    item.type = 5
                    item.name = "结案日期"
                    item.isClick = true
                    item.isLineShow = true
                    item.isEnable = remarkShow
                    item.content = loanInfo.overTime
                }
            }
            list.add(item)
        }
        if (remarkShow) {
            for (i in 0..1) {
                val item1: CommonItem<Any> = CommonItem()
                when (i) {
                    0 -> item1.type = 0
                    1 -> {
                        item1.type = 6
                        item1.name = "结单备注"
                        item1.position = 100
                    }
                }
                list.add(item1)
            }
        }
        for (i in 0..2) {
            val item2: CommonItem<Any> = CommonItem()
            when (i) {
                0 -> item2.type = 0
                1 -> {
                    item2.type = 0
                    item2.isClick = true
                }
                2 -> {
                    item2.type = 4
                    item2.isClick = true
                    item2.list = overApply?.overList
                }
            }
            list.add(item2)
        }
        return list
    }
}