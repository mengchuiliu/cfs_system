package entity

/**
 * Created by Administrator on 2017/11/22.
 * 工资确认实体
 */
class WageDetail {
    var wageId: Int? = 0//提成id
    var state: String? = null //状态 按揭员确认  1->财务未确认，不可确认 ，2->员工待确认，3->已确认
    var loanCode: String? = null//贷款编号
    var customerName: String? = null//客户姓名
    var loanType: Int? = 0//贷款类型
    var product: Int? = 0//银行产品
    var lendAmount: String? = null//放款金额
    var lendDate: String? = null//放款日期
    var calcCommission: String? = null//提成
    var commission: String? = null//实际提成
    var remark: String? = null//业务提成备注
    var userFeedBack: String? = null//员工异议
    var financialFeedBack: String? = null//财务异议
    var confirmedState: Int? = 0//1->已确认放款 该放款未确认或有异议
}