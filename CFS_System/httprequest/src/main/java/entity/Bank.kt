package entity

import java.io.Serializable

/**
 * Created by Administrator on 2017/10/17.
 */
class Bank : Serializable {
    var owner: String? = null
    var bankCode: String? = null
    var idCard: String? = null
    var isDefault: Boolean = false

    var bankName: String? = null //银行名称
    var cardType: String? = null//银行卡类型
    var userID: Int? = null//所有者
    var bankIconUrl: String? = null //银行logo下载地址
}