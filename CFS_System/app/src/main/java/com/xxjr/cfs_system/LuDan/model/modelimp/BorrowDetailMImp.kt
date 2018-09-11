package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.tools.Utils
import entity.BorrowInfo
import entity.CommonItem
import entity.Schedule
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.HashMap

class BorrowDetailMImp : ModelImp() {

    fun getParam(list: List<Any>, tranName: String, Action: String): String {
        val map = HashMap<String, Any>()
        map.put("DBMarker", "DB_CFS_Loan")
        map.put("Marker", "HQServer")
        map.put("IsUseZip", false)
        map.put("Action", Action)
        map.put("Function", "Pager")
        map.put("ParamString", list)
        map.put("TranName", tranName)
        Logger.e("==拆借审核参数==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    fun getBorrowDetailData(borrowInfo: BorrowInfo): MutableList<CommonItem<Any>> {
        val list: MutableList<CommonItem<Any>> = ArrayList()
        var item: CommonItem<Any>
        for (i in 0..15) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.type = 1
                    item.name = "拆借信息"
                    item.icon = R.mipmap.icon_borrowing
                    if (borrowInfo.borrowState == 1) {
                        if (Hawk.get<String>("UserID") == borrowInfo.serviceID || Hawk.get<String>("UserType") == "99" || Hawk.get<String>("UserType") == "90") {
                            item.isClick = true
                        }
                    }
                }
                1 -> {
                    item.type = 2
                    item.name = "拆  借  类  型  ："
                    item.content = Utils.getTypeValue(Utils.getTypeDataList("LendingType"), borrowInfo.borrowTypeId ?: 0)
                }
                2 -> {
                    item.type = 2
                    item.name = "拆  借  金  额  ："
                    item.content = "${Utils.parseMoney(BigDecimal(borrowInfo.borrowAmount ?: 0.0))} 元"
                    item.isLineShow = true
                }
                3 -> {
                    item.type = 2
                    item.name = "拆  借  日  息  ："
                    val df = DecimalFormat("0.0000")
                    item.content = if (borrowInfo.borrowInterest == null) "" else "${df.format(borrowInfo.borrowInterest ?: 0.0)} %/每日"
                }
                4 -> {
                    item.type = 2
                    item.name = "预计结算日期："
                    item.content = borrowInfo.predictBorrowDate
                }
                5 -> {
                    item.type = 2
                    item.name = "预计拆借创收："
                    item.content = "${Utils.parseMoney(BigDecimal(borrowInfo.predictBorrowIncome ?: 0.0))} 元"
                }
                6 -> {
                    item.type = 2
                    item.name = "还  款  方  式  ："
                    item.content = Utils.getTypeValue(Utils.getTypeDataList("RePayType"), borrowInfo.rePayTypeId ?: -1)
                }
                7 -> {
                    item.type = 2
                    item.name = " 拆借申请人   ："
                    item.content = borrowInfo.borrowProposer
                }
                8 -> {
                    item.type = 2
                    item.name = "申  请  客  户  ："
                    item.content = borrowInfo.customerNames
                }
                9 -> {
                    item.type = 2
                    item.name = " 拆借审批人   ："
                    item.content = borrowInfo.approveName
                }
                10 -> {
                    item.type = 2
                    item.name = "进  度  状  态  ："
//                    进度状态 1申请中 2已取消  3不同意  4同意拆借 6已打款  7已逾期  9已回款(添加的时候默认为1)
                    item.content = when (borrowInfo.borrowState) {
                        1 -> "申请中"
                        3 -> "审批拒绝"
                        4 -> "审批中"
                        5 -> "审批通过"
                        6 -> "已放款"
                        9 -> "已回款"
                        else -> ""
                    }
                    item.isLineShow = true
                }
                11 -> {
                    item.type = 2
                    item.name = "实际结算日期："
                    item.content = borrowInfo.settlementDate
                }
                12 -> {
                    item.type = 2
                    item.name = "实际拆借创收："
                    item.content = "${
                    Utils.parseMoney(BigDecimal(borrowInfo.income ?: 0.0))} 元"
                }
                13 -> {
                    item.type = 2
                    item.name = "实际回款金额："
                    item.content = "${
                    Utils.parseMoney(BigDecimal(borrowInfo.backPayment ?: 0.0))} 元"
                }
                14 -> {
                    item.type = 3
                    item.name = "进度跟踪"
                    item.isClick = false
                }
                15 -> {
                    item.type = 4
                    item.isClick = false
                    item.list = getSchedule(borrowInfo.followContent)
                }
            }
            list.add(item)
        }
        return list
    }

    private fun getSchedule(jsonArray: JSONArray?): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        if (jsonArray?.isNotEmpty() == true) {
            if (jsonArray.size != 0) {
                var temp: Schedule
                for (i in jsonArray.indices) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    temp = Schedule()
                    val builder = StringBuilder()
                    val typeName = Utils.getTypeValue(Utils.getTypeDataList("LendingType"), jsonObject.getIntValue("LendingType"))
                    builder.append(typeName).append("-")
                    when (jsonObject.getIntValue("LendingState")) {
                        1 -> builder.append("申请中")
                        3 -> builder.append("审批拒绝")
                        4 -> builder.append("审批中")
                        5 -> builder.append("审批通过")
                        6 -> builder.append("已放款")
                        9 -> builder.append("已回款")
                    }
                    builder.append("  备注:${jsonObject.getString("Remark")}")
                    temp.status = builder.toString()
                    temp.name = jsonObject.getString("ServiceName")
                    temp.date = Utils.getTimeFormat(jsonObject.getString("UpdateTime"))
                    list.add(temp)
                }
            }
        }
        return list
    }

    fun getBorrowInfo(data: String): BorrowInfo? {
        var borrowInfo: BorrowInfo? = null
        val jsonObject = JSON.parseObject(data)
        if (jsonObject != null && jsonObject.isNotEmpty()) {
            borrowInfo = BorrowInfo()
            val borrowJson = jsonObject.getJSONObject("LendInfo")
            borrowInfo.borrowId = borrowJson.getIntValue("LendId")
            borrowInfo.borrowTypeId = borrowJson.getIntValue("LendingType")
            borrowInfo.borrowDate = Utils.getTime(borrowJson.getString("LendingDate"))
            borrowInfo.borrowAmount = borrowJson.getDoubleValue("Amount")
            borrowInfo.borrowInterest = borrowJson.getDoubleValue("interest")
            borrowInfo.predictBorrowDate = Utils.getTime(borrowJson.getString("EstimateSettlementDate"))
            borrowInfo.predictBorrowIncome = borrowJson.getDoubleValue("EstimateIncome")
            borrowInfo.rePayTypeId = borrowJson.getIntValue("RepaymentType")
            borrowInfo.borrowProposer = borrowJson.getString("ServiceName")
            borrowInfo.serviceID = borrowJson.getString("ServiceID")
            borrowInfo.customerNames = borrowJson.getString("CustomerNames")
            borrowInfo.approveName = borrowJson.getString("ApproverUserNames")
            borrowInfo.approveIds = borrowJson.getString("ApproverUserIDs")
            borrowInfo.borrowState = borrowJson.getIntValue("LendingState")
            borrowInfo.settlementDate = Utils.getTime(borrowJson.getString("SettlementDate"))
            borrowInfo.income = borrowJson.getDoubleValue("Income")
            borrowInfo.backPayment = borrowJson.getDoubleValue("BackPayment")
            borrowInfo.contractCode = borrowJson.getString("ContractCode")
            borrowInfo.contractID = borrowJson.getIntValue("ContractID")
            borrowInfo.borrowRemark = borrowJson.getString("Remark")
            borrowInfo.updateTime = borrowJson.getString("UpDateTime")
            borrowInfo.followContent = jsonObject.getJSONArray("LendFollowList")
        }
        return borrowInfo
    }
}