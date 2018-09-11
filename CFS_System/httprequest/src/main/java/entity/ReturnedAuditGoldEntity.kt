package entity

import java.io.Serializable

/**
 *
 * App获取回款审核列表（金账户信息）
 * @author huangdongqiang
 * @date 27/06/2018
 */
class ReturnedAuditGoldEntity : Serializable{
    var ID: Int = 0                //Id
    var BankCardNo: String = ""    //银行卡号
    var CstmNm: String = ""        //户名
    var BankNm: String = ""        //银行名
    var Rem: String = ""           //金账户备注

}