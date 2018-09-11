package entity

import java.io.Serializable

/**
 *
 * App获取回款审核列表（基本信息）
 * @author huangdongqiang
 * @date 27/06/2018
 */
class ReturnedAuditEntity : Serializable {
    var CustomerNames: String = ""         //所属客户
    var CompanyId: String = ""             //所属门店
    var A16: Double = 0.0                  //放款金额
    //交易状态（0已发起 1交易成功 2交易失败 3未发送 4发送中 5 发送超时 其他返回留白）
    //注意：当支付方式为代扣3时，交易状态-1时，AisleType为1、2、5时显示为“未发起交易”其他交易状态为-1的情况统一留白
    //此外除代扣外，其他支付类型均留白
    var BusinessState: Int = 0
    var L3: Int = 0                        //贷款产品
    var PayType: Int = 0                   //支付方式（现金1、转账2、代扣3、金账户4）
    var ServiceID: Int = 0                 //业务员
    var ID: Int = 0                        //编号
    var CompanyZoneName: String = ""       //所属地区
    var MortgageID: Int = 0                //按揭员
    var Amount: Double = 0.0               //回款金额
    var PayTime: String = ""               //支付时间
    var ServicePeople: Int = 0             //经办人
    var State: Int = 0                     //审核状态（0 未审核 1 审核通过 2审核拒绝  3.金额已审核 4.已发起代扣 5.金账户处理中）
    var RecordTime: String = ""            //入账时间
    var HeadReceiveTime: String = ""       //收款时间
    var Auditor: String = ""               //审核人
    var AuditTime: String = ""              //审核日期
    var AuditRemark: String = ""           //审核备注
    var CustomerIDs: String = ""           //申请客户ID
    var AisleType: String = ""             //平台类型  通联支付1，上海富友2，宝付3，通菀4，通联协议5
    var ProtocolID: String = ""            //金账户Id或代扣Id
    var CompanyName: String = ""            //所属门店名称
    var LoanTypeName: String = ""           //贷款产品名称
    var ServiceIDName: String = ""          //业务员名称
    var MortgageIDName: String = ""         //按揭员名称
    var ServicePeopleName: String = ""      //经办人名称
    var AuditorName: String = ""            //审核人名称
    var BookTypeName: String = ""           //收支类型名称
    var ContractNo: String = ""             //所属协议
    var LoansMoney: Double = 0.0            //所有放款金额
}