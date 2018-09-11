package entity

import java.io.Serializable

class SpendingInfo : Serializable {
    var AC_UserIDs: String? = null
    var AC_UserTypes: String? = null
    var AC_User_Names: String? = null
    var AC_UserTypeNames: String? = null
    var AC_Auditable: Int = 0 //>0有审核权限
    var RecordId: Int = -1//支出记录的主键ID
    var CompanyID: String? = null//门店编号：“001”
    var Company: String? = null//门店：“001小小金融”
    var RecordType: String? = null//支出类型：1场地租金 2物业费
    var ExpenseItem: Int = -1//费用项 0办公场地 1设备机房2房屋综合税 3印花税 4物业费5水电费6物业费+水电费
    var PayDate: String? = null//缴费日期
    var ContractId: Int = -1//合同ID
    var StartDate: String? = null//起始日期
    var EndDate: String? = null//终止日期
    var Signatory: String? = null//签约人姓名
    var Area: String = ""//面积
    var CompanyAddress: String? = null//公司地址
    var ContractST: String? = null//合同开始日期
    var ContractET: String? = null//合同结束日期
    var Cycle: Int = 0//缴费周期
    var AuditState: Int = 0//状态1待区域主管审核 2待总部财务审核 -1待出纳付款 -3已付款 -2已拒绝
    var Creator: Int = 0
    var InsertTime: String? = null
    var ZoneId: String? = null
    var Amount: Double = 0.0 //金额
    var Receiver: String? = null//收款人
    var ReceiveAccount: String? = null//收款账号
    var ReceiverBank: String? = null//收款银行
    var Remark: String? = null//备注
    var IsHaveReceipt: Boolean? = null //是否有发票
    var ReceiptCompany: String? = null//发片公司名称
    var IsPayment: Boolean? = null //是否已付款
    var PayAccountType: Int = -1//支出账户属性 1：私户 2：公户
    var ID: Int = -1
}