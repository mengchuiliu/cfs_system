package entity

class Reimburse {
    var reimburseAmount: Double = 0.0 //每月还款金额
    var interests: Double = 0.0 //利息
    var reimburseDate: String? = null // 每月还款日期
    var cumulativeInterests = 0.0 //累计利息
    var cumulativeAmount = 0.0 //累计还款总额
    var remainAmount = 0.0 //剩余本金
    var countDate: String = "" // 期数
}