package entity

import java.io.Serializable

/**
 *
 * App获取回款审核列表（代扣信息）
 * @author huangdongqiang
 * @date 27/06/2018
 */
class ReturnedAuditDetialEntity : Serializable{

    var ID: Int = 0                //Id
    var CardName: String = ""      //持卡人姓名
    var BankCardNo: String = ""    //银行卡号
    var ProtocolNo: String = ""    //协议号
    var AisleType: String = ""     //平台类型
    var ReservePhone: String = ""  //预留手机号


}