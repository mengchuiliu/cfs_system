package entity

class OverApply {
    var contractId: Int? = 0
    var contractCode: String? = null //合同编号
    var salesmanCompany: String? = null//业务员公司
    var salesmanName: String? = null//业务员
    var payment: Double? = 0.0  //定金
    var serviceCharge: Double? = 0.0 //服务费收入
    var income: Double? = 0.0 //实际创收
    var commission: Double? = 0.0 //按揭员提成
    var formula: String? = null //提成公式算法
    var costing: Double? = 0.0 //累计成本

    var costList: MutableList<*>? = null //累计成本列表数据
    var bookList: MutableList<*>? = null //出入账列表
    var overList: MutableList<*>? = null //申请结单详情列表
}