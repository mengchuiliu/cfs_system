package com.xxjr.cfs_system.LuDan.view.activitys.borrow.model

import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.tools.Utils
import entity.BorrowCustomer
import entity.BorrowDetail
import entity.CommonItem
import java.math.BigDecimal

class DetailMImp : ModelImp() {
    private var blank = "            "

    /**
     * @param borrowCustomer 拆借客户详情
     * @param borrowDetail 拆借详情
     * @param fileData 资料详情
     * @param isBorrowShrink 拆借详情是否伸缩
     */
    fun getItemData(isPersonShrink: Boolean, isBorrowShrink: Boolean, isFileShrink: Boolean,
                    borrowCustomer: BorrowCustomer, borrowDetail: BorrowDetail, fileData: MutableList<CommonItem<Any>>) = mutableListOf<CommonItem<Any>>().apply {
        for (i in 0..45) {
            add(CommonItem<Any>().apply {
                if (i in 1..24) isClick = isPersonShrink
                if (i in 26..43) isClick = isBorrowShrink
                when (i) {
                    0 -> {
                        type = 5
                        name = "客户信息"
                        isEnable = false
                        isClick = isPersonShrink
                    }
                    1 -> {
                        type = 2
                        name = "客户姓名$blank"
                        content = borrowCustomer.Name ?: ""
                        icon = R.color.font_home
                    }
                    2 -> {
                        type = 2
                        name = "手机号$blank    "
                        content = borrowCustomer.Phone ?: ""
                    }
                    3 -> {
                        type = 2
                        name = "身份证号$blank"
                        content = borrowCustomer.IdCardNumber ?: ""
                    }
                    4 -> {
                        type = 2
                        name = "身份证地址        "
                        content = borrowCustomer.IdCardDetail ?: ""
                    }
                    5 -> {
                        type = 2
                        name = "学历$blank        "
                        content = when (borrowCustomer.Qualification) {
                            1 -> "本科及以上"
                            2 -> "大专"
                            3 -> "高中或者中专"
                            4 -> "初中及以下"
                            else -> ""
                        }
                    }
                    6 -> {
                        type = 2
                        name = "婚姻状态$blank"
                        content = when (borrowCustomer.MaritalStatus) {
                            0 -> "未设置"
                            1 -> "未婚"
                            2 -> "已婚"
                            3 -> "离异"
                            4 -> "丧偶"
                            else -> ""
                        }
                    }
                    7 -> {
                        type = 2
                        name = "民族$blank        "
                        content = borrowCustomer.RaceName ?: ""
                    }
                    8 -> {
                        type = 2
                        name = "居住地址$blank"
                        content = borrowCustomer.AbodeDetail ?: ""
                    }
                    9 -> {
                        type = 2
                        name = "单位名称$blank"
                        content = borrowCustomer.EmpName ?: ""
                    }
                    10 -> {
                        type = 2
                        name = "部门$blank        "
                        content = borrowCustomer.EmpDepartment ?: ""
                    }
                    11 -> {
                        type = 2
                        name = "职务$blank        "
                        content = borrowCustomer.EmpPost ?: ""
                    }
                    12 -> {
                        type = 2
                        name = "月收入$blank    "
                        content = "${borrowCustomer.MonthIncome ?: ""}元"
                    }
                    13 -> {
                        type = 2
                        name = "单位电话$blank"
                        content = borrowCustomer.EmpPhone ?: ""
                    }
                    14 -> {
                        type = 2
                        name = "单位地址$blank"
                        content = borrowCustomer.EmpDetail ?: ""
                    }
                    15 -> {
                        type = 2
                        name = "联系人1姓名      "
                        content = borrowCustomer.ContactName1 ?: ""
                    }
                    16 -> {
                        type = 2
                        name = "联系人1关系      "
                        content = when (borrowCustomer.ContactRelation1) {
                            0 -> "未设置"
                            1 -> "配偶"
                            2 -> "亲戚"
                            3 -> "朋友"
                            4 -> "同事"
                            else -> ""
                        }
                    }
                    17 -> {
                        type = 2
                        name = "联系人1电话      "
                        content = borrowCustomer.ContactMobile1 ?: ""
                    }
                    18 -> {
                        type = 2
                        name = "联系人2姓名      "
                        content = borrowCustomer.ContactName2 ?: ""
                    }
                    19 -> {
                        type = 2
                        name = "联系人2关系      "
                        content = when (borrowCustomer.ContactRelation2) {
                            0 -> "未设置"
                            1 -> "配偶"
                            2 -> "亲戚"
                            3 -> "朋友"
                            4 -> "同事"
                            else -> ""
                        }
                    }
                    20 -> {
                        type = 2
                        name = "联系人2电话      "
                        content = borrowCustomer.ContactMobile2 ?: ""
                    }
                    21 -> {
                        type = 2
                        name = "房产地址$blank"
                        content = borrowCustomer.HouseDetailAddr ?: ""
                    }
                    22 -> {
                        type = 2
                        name = "房屋面积$blank"
                        content = "${borrowCustomer.HouseArea ?: " 0"}㎡"
                    }
                    23 -> {
                        type = 2
                        name = "产权比例$blank"
                        content = "${borrowCustomer.HousePropertyRatio ?: " 0"}%"
                    }
                    24 -> type = 4
                    25 -> type = 0
                    26 -> {
                        type = 5
                        name = "拆借信息"
                        isEnable = false
                    }
                    27 -> {
                        type = 2
                        name = "合同编号$blank"
                        content = borrowDetail.Code ?: ""
                        if (!isBorrowShrink) isClick = borrowDetail.Code.isNullOrBlank()
                    }
                    28 -> {
                        type = 2
                        name = "贷款用途$blank"
                        content = when (borrowDetail.FunctionType) {
                            "11" -> "进货周转"
                            "12" -> "店铺装修"
                            "13" -> "扩大经营"
                            "14" -> "日常消费"
                            "15" -> "其他"
                            else -> ""
                        }
                    }
                    29 -> {
                        type = 2
                        name = "申请金额$blank"
                        content = "${Utils.parseMoney(BigDecimal(borrowDetail.ApplyAmount))} 元"
                        icon = R.color.detail1
                    }
                    30 -> {
                        type = 2
                        name = "申请期限$blank"
                        content = "${borrowDetail.ApplyBackTime}月"
                    }
                    31 -> {
                        type = 2
                        name = "拆借状态$blank"
                        content = when (borrowDetail.State) {
                            -1 -> "资料不全"//-1
                            1 -> "已提交"//1
                            2 -> "已进件待授信"//2
                            3 -> "已授信待审批"//3
                            4 -> "审批中"//4
                            5 -> "已拆借待放款"//5
                            6 -> "已放款"//6
                            -2 -> "授信审核未通过"//-2
                            -3 -> "审批未通过"//-3
                            else -> "未知"
                        }
                    }
                    32 -> {
                        type = 2
                        name = "银行卡号$blank"
                        content = borrowDetail.BankCardNum ?: ""
                    }
                    33 -> {
                        type = 2
                        name = "银行卡机构        "
                        content = borrowDetail.BankCardName ?: ""
                    }
                    34 -> {
                        type = 2
                        name = "银行支行$blank"
                        content = borrowDetail.BankCardBranch ?: ""
                    }
                    35 -> {
                        type = 2
                        name = "所绑手机号        "
                        content = borrowDetail.BankCardPhone ?: ""
                    }
                    36 -> {
                        type = 2
                        name = "拆借类型$blank"
                        content = when (borrowDetail.Type) {
                            1 -> "批复前短拆"
                            2 -> "批复后短拆"
                            3 -> "批复前赎楼"
                            4 -> "批复后赎楼"
                            else -> ""
                        }
                    }
                    37 -> {
                        type = 2
                        name = "拆借日期$blank"
                        content = Utils.FormatTime(borrowDetail.StartDate, "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")
                    }
                    38 -> {
                        type = 2
                        name = "预计结算日期    "
                        content = Utils.FormatTime(borrowDetail.EstimateSettlementDate, "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")
                    }
                    39 -> {
                        type = 2
                        name = "预计拆借创收    "
                        content = "${Utils.parseMoney(BigDecimal(borrowDetail.CompanyRevenue
                                ?: "0"))}元"
                        icon = R.color.detail1
                    }
                    40 -> {
                        type = 2
                        name = "还款方式$blank"
                        content = when (borrowDetail.RepaymentType) {
                            1 -> "先息后本(按月还息)"
                            2 -> "先息后本(按季还息)"
                            3 -> "先息后本(到期还息)"
                            4 -> "等额本息"
                            5 -> "等额本金"
                            6 -> "随借随还"
                            7 -> "先息加还部分本金"
                            else -> "未设置"
                        }
                    }
                    41 -> {
                        type = 2
                        name = "拆借申请人        "
                        content = borrowDetail.ApplicantName ?: ""
                    }
                    42 -> {
                        type = 2
                        name = "审核备注$blank"
                        content = borrowDetail.AuditRemark ?: ""
                    }
                    43 -> type = 4
                    44 -> type = 0
                    45 -> {
                        type = 5
                        name = "影像资料"
                        isEnable = false
                        isClick = isFileShrink
                    }
                }
            })
        }
        for (item in fileData) {
            add(item.apply { isClick = isFileShrink })
        }
        add(CommonItem<Any>().apply { type = 0 })
    }

}