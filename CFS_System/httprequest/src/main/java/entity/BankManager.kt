package entity

import java.io.Serializable

/**
 * Created by Administrator on 2017/12/1.
 */
class BankManager : Serializable {
    var id: Int? = -1
    var bankManagerName: String? = null //银行经理名称
    var belongBankId: Int? = 0 //所属银行
    var belongBankName: String? = null //所属银行名称
    var phone1: String? = null
    var phone2: String? = null
    var rate: String? = null
    var branchBankName: String? = null//支行名称
    var recommendedName: String? = null//推荐人名称
    var zoneId: Int? = 0//地区id
    var zone: String? = null//地区
    var remark: String? = null//地区
}