package entity

/**
 *
 * 回款审核二级菜单 数量
 * @author huangdongqiang
 * @date 26/06/2018
 */
class ReturnedAuditCountEntity {
    var AllCount: Int = 0       //以下字段总和
    var PreAuditCount: Int = 0  //待审核
    var InAccountCount: Int = 0 //已入账
    var RefuseAuditCount: Int = 0   //已拒绝
    var AuditCount: Int = 0     //已审核
    var ApplyCount: Int = 0     //已发起
    var TradeCount: Int = 0     //交易中
    var PreRechargeCount: Int = 0   //待充值
}