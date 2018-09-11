package entity

import java.io.Serializable

/**
 * Created by Administrator on 2018/3/30.
 * 金账户交易详情实例
 */
class GoldTradeDetail : Serializable {
    var isAdd: Boolean = false //false->出账 true->入账
    var tradeTp: String = "" //交易类型
    var tradeSN: String = "" // 交易号码
    var changeAmt: Double = 0.0 //交易金额
    var tradeTime: String = ""
    var bookTime: String = ""
    var description: String = "" //交易概述
    var remark: String ?= ""
    var dealInfo: String = "" //往来账户信息
}