package entity

import java.io.Serializable

class TransactionRecord : Serializable {
    var Id: Int = 0
    var DelMarker: Boolean? = null
    var InsertTime: String? = null
    var UpdateTime: String? = null
    var TransSn: String? = null//交易流水号
    var OriginCode: Int = 0//访问来源
    var OperatorId: Int = 0//操作人id
    var OperatorName: String? = null//操作人姓名
    var ApplyUserId: Int = 0//申请人
    var ApplyCompanyId: String? = null//申请门店（交易所属门店）
    var ApplyCompanyName: String? = null//申请门店名称
    var Amount: Double = 0.0//交易金额
    var HappenTime: String? = null//交易时间
    var TransType: Int = 0//交易类型
    var TransWay: Int = 0//交易方式
    var OutAcct: String? = null//支出账户
    var InAcct: String? = null//收入账户
    var State: Int = 0//交易状态
    var Remark: String? = null//交易备注
    var BizId: String? = null//业务相关id
    var FailReason: String? = null//失败原因
}