package com.xxjr.cfs_system.LuDan.model.modelimp

import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.Cost
import java.math.BigDecimal

class AuditCostMImp : ModelImp() {
    fun getItemData(cost: Cost): MutableList<CommonItem<*>> {
        val list: MutableList<CommonItem<*>> = ArrayList()
        var commonItem: CommonItem<*>
        for (i in 0..21) {
            commonItem = CommonItem<Any>()
            when (i) {
                0 -> commonItem.type = 0
                1 -> {
                    commonItem.type = 1
                    commonItem.name = "贷款信息—${cost.customerName}【${CacheProvide.getBank(cost.bankId
                            ?: 0)}·${CacheProvide.getProduct(cost.productId ?: 0)}" +
                            "(${if (CacheProvide.getLoanType(cost.loanType ?: 0).isBlank()) ""
                            else CacheProvide.getLoanType(cost.loanType ?: 0).substring(0, 1)})】"
                    commonItem.icon = R.mipmap.icon_loan
                }
                2 -> {
                    commonItem.type = 2
                    commonItem.name = "贷款编号："
                    commonItem.content = cost.loanCode
                }
                3 -> {
                    commonItem.type = 2
                    commonItem.name = "贷款客户："
                    commonItem.content = cost.customerName
                }
                4 -> {
                    commonItem.type = 2
                    commonItem.name = "申请金额："
                    commonItem.content = "${Utils.parseMoney(BigDecimal(cost.applyMoney))}元"
                }
                5 -> {
                    commonItem.type = 2
                    commonItem.name = "批复金额："
                    commonItem.content = "${Utils.parseMoney(BigDecimal(cost.replyMoney))}元"
                }
                6 -> {
                    commonItem.type = 2
                    commonItem.name = "业 务 员  ："
                    commonItem.content = cost.clerkName
                }
                7 -> commonItem.type = 0
                8 -> commonItem.type = 0
                9 -> {
                    val permits = CFSUtils.getPermitValue(Hawk.get("PermitValue", ""), 1)
                    commonItem.type = 1
                    commonItem.name = "成本信息"
                    commonItem.icon = R.mipmap.icon_cost
                    if (cost.auditStatus == 0 && permits != null && permits.contains("CY")) {
                        commonItem.isLineShow = true
                    }
                }
                10 -> {
                    commonItem.type = 2
                    commonItem.name = "${CacheProvide.getCostType(cost.costType)}："
                    commonItem.content = "${Utils.parseMoney(BigDecimal(cost.money))}元"
                }
                11 -> {
                    commonItem.type = 2
                    commonItem.name = "发生日期："
                    commonItem.content = Utils.getTime(cost.happenDate ?: "")
                }
                12 -> {
                    commonItem.type = 2
                    commonItem.name = "录 入 者  ："
                    commonItem.content = cost.serviceName
                    commonItem.hintContent = Utils.getTime(cost.operateTime ?: "")
                }
                13 -> {
                    commonItem.type = 2
                    commonItem.name = "成本备注："
                    commonItem.isClick = true
                    commonItem.content = cost.remark
                    if (cost.remark.isNullOrBlank()) {
                        commonItem.isLineShow = true
                    }
                }
                14 -> {
                    commonItem.type = 2
                    commonItem.name = "审核状态："
                    commonItem.content = when (cost.auditStatus) {
                        0 -> "未审核"
                        1 -> "门店经理审核通过"
                        2 -> "门店经理审核拒绝"
                        3 -> "会计审核通过"
                        4 -> "按揭经理审核拒绝"
                        5 -> "总部财务添加"
                        6 -> "按揭经理审核通过"
                        7 -> "财务会计审核拒绝"
                        8 -> "特定人审核通过"
                        9 -> "特定人审核拒绝"
                        else -> ""
                    }
                }
                15 -> {
                    commonItem.type = 2
                    commonItem.name = "门店审核人："
                    commonItem.content = cost.auditorName
                    commonItem.hintContent = Utils.getTime(cost.auditTime ?: "")
                    if (cost.auditStatus < 1 || cost.auditStatus == 5) {
                        commonItem.isLineShow = true
                    }
                }
                16 -> {
                    commonItem.type = 2
                    commonItem.name = "按揭经理审核人："
                    commonItem.content = cost.auditorManager
                    commonItem.hintContent = Utils.getTime(cost.managerAuditDateTime ?: "")
                    if (cost.auditStatus < 3 || cost.auditStatus == 5) {
                        commonItem.isLineShow = true
                    }
                }
                17 -> {
                    commonItem.type = 2
                    commonItem.name = "特定审核人："
                    commonItem.content = cost.auditorManager2
                    commonItem.hintContent = Utils.getTime(cost.managerAuditDateTime2 ?: "")
                    if (cost.money < (CFSUtils.getAuditCost().toDouble())) {
                        commonItem.isLineShow = true
                    } else {
                        if (cost.auditStatus != 3 && cost.auditStatus != 7 && cost.auditStatus != 8 && cost.auditStatus != 9) {
                            commonItem.isLineShow = true
                        }
                    }
                }
                18 -> {
                    commonItem.type = 2
                    commonItem.name = "会计审核人："
                    commonItem.content = cost.accounterName
                    commonItem.hintContent = Utils.getTime(cost.accounterTime ?: "")
                    if ((cost.auditStatus != 3 && cost.auditStatus != 7) || cost.auditStatus == 5) {
                        commonItem.isLineShow = true
                    }
                }
                19 -> {
                    commonItem.type = 2
                    commonItem.name = "审核备注："
                    commonItem.isClick = true
                    when (cost.auditStatus) {
                        2, 4 -> commonItem.content = cost.auditRemark
                        7 -> commonItem.content = cost.accounterAuditRemark
                        else -> commonItem.isLineShow = true
                    }
                }
                20 -> {
                    commonItem.type = 2
                    if (cost.auditStatus == 5) {
                        commonItem.isLineShow = true
                    }
                    commonItem.name = "是否已报销："
                    if (cost.paidTime.isNullOrBlank()) {
                        commonItem.content = "未付款"
                    } else {
                        commonItem.content = "已付款-${cost.paidName}"
                        commonItem.hintContent = Utils.getTime(cost.paidTime ?: "")
                    }
                }
                21 -> commonItem.type = 0
            }
            list.add(commonItem)
        }
        return list
    }
}