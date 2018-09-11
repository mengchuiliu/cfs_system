package entity

import java.io.Serializable

/**
 * Created by Administrator on 2017/7/26.
 * 成本审核
 */

class Cost : Serializable {
    var loanCostId: Int = 0//成本ID
    var loanCode: String? = null//贷款编号
    var loanId: Int? = null//贷款id
    var bankId: Int? = null//银行id
    var loanType: Int? = null//贷款类型id
    var productId: Int? = null//银行产品id
    var customerName: String? = null//申请客户
    var costType: Int = 0//成本类型
    var money: Double = 0.0//成本金额
    var applyMoney: Double = 0.0//申请金额
    var replyMoney: Double = 0.0//批复金额
    var remark: String? = null//成本备注
    var serviceID: Int = 0//操作员ID
    var serviceName: String? = null//操作员名称
    var clerkID: Int = 0//业务员ID
    var clerkName: String? = null//业务员名称
    var operateTime: String? = null//操作时间
    var happenDate: String? = null//发生时间
    var auditStatus: Int = 0//审核状态  0：未审核，1：门店经理审核通过， 2：门店经理审核拒绝，3：会计审核通过，4：按揭经理审核拒绝 5：总部财务添加的 6：按揭经理审核通过 7：财务会计审核拒绝
    var auditorID: Int = 0//门店审核人ID
    var auditorName: String? = null//门店审核人名称
    var auditTime: String? = null//审核时间
    var auditRemark: String? = null//审核备注
    var auditorManager: String? = null//按揭经理名称
    var auditorManager2: String? = null//特定审核名称
    var managerAuditDateTime: String? = null//按揭经理审核时间
    var managerAuditDateTime2: String? = null//特定审核时间
    var accounterName: String? = null//会计审核人
    var accounterTime: String? = null//会计审核时间
    var accounterAuditRemark: String? = null //会计审核备注
    var paidName: String? = null//已报销人
    var paidTime: String? = null//报销时间

    companion object {
        private const val serialVersionUID = -8711872925567731950L
    }
}
