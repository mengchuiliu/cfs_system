package entity

import java.io.Serializable

class BorrowDetail : Serializable {
    var Id: Int = -1 //拆借合同Id
    var LoanContractId: Int = -1 //贷款合同Id
    var CustomerId: Int = -1//顾客Id
    var Code: String? = null //拆借编号
    var Type: Int = -1// 拆借类型，枚举见下
    var StartDate: String? = null//拆借日期
    var Interest: Double = 0.0 //拆借日息，百分比
    var EstimateSettlementDate: String? = null//预计结算日期
    var RepaymentType: Int = -1//还款方式，枚举见下
    var Applicant: Int = -1//拆借申请人Id
    var State: Int = -9//拆借进度状态，枚举见下
    var AuditRemark: String? = null//当前进度流程备注
    var Remark: String? = null//合同备注
    var FunctionType: String? = null//贷款用途，枚举见下	D1->进货周转 D2->店铺装修 D3->扩大经营 D4->日常消费 D5->其他
    var ApplyAmount: Double = 0.0 //申请金额
    var ApplyBackTime: Int = 0 //申请期限，即申请的还款期限，单位月
    var BankCardNum: String? = null//银行卡号
    var BankCardCode: String? = null//银行卡机构编号
    var BankCardName: String? = null//银行卡机构名称
    var BankCardBranch: String? = null//银行卡机构名称
    var BankCardPhone: String? = null//银行卡绑定的手机号码
    var SettlementDate: String? = null//实际结算日期
    var Income: String? = null//实际创收
    var HQCostInterest: String? = null//总部成本日息，百分比
    var BackPaymentAduitor: Int? = null//回款确认人Id
    var BackPayment: String? = null//实际回款
    var BackPaymentTime: String? = null//回款时间
    var LoanTime: String? = null//放款时间
    var CompanyRevenue: String? = null// 门店应创收
    var DelMarker: Boolean = false
    var InsertTime: String? = null// 记录创建时间
    var UpdateTime: String? = null//记录修改时间
    var LoanContractNum: String? = null//贷款合同编号
    var CustomerName: String? = null//顾客姓名
    var ApplicantName: String? = null//拆借申请人名称
    var CompanyID: String? = null//贷款合同所属门店Id
    var CompanyName: String? = null//贷款合同所属门店名称
    var ZoneId: String? = null//区域id
    var ZoneName: String? = null// 区域名称
    var Auditable: Boolean = false// 根据权限所得是否可审核  true->可以  false-> 无权限审核
}