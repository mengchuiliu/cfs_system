package entity

import java.io.Serializable

/**
 *
 * 回款审核列表，代扣信息
 * @author huangdongqiang
 * @date 28/06/2018
 */
class ReturnedAuditWithholdEntity : Serializable{
    var ID: Int = 95                //Id
    var CardName: String = ""       //付款户名
    var BankCardNo: String = ""     //付款账号
    var ProtocolNo: String = ""     //代扣协议
    var AisleType: String = ""      //代扣平台 ：通联支付1，上海富友2，宝付3，通菀4，通联协议5
    var ReservePhone: String = ""   //预留手机号
}