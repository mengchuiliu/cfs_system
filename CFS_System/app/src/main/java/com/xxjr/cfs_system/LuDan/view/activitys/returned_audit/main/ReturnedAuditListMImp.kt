package com.xxjr.cfs_system.LuDan.view.activitys.returned_audit.main

import com.alibaba.fastjson.JSON
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.tools.Constants

/**
 *
 * 回款审核 业务方法
 * @author huangdongqiang
 * @date 25/06/2018
 */
class ReturnedAuditListMImp : ModelImp(){

    /**
     * 获取参数
     */
    fun getReturnedAuditListParams(list : List<Any>) : String {
        val map = HashMap<String, Any>()
        //map[Constants.HTTP_PARAM_USER_ID] = Hawk.get("UserID")
        map[Constants.HTTP_PARAM_TRAN_NAME] = "GetHeadquarterCaseList"
        map[Constants.HTTP_PARAM_PARAM_STRING] = list
        return JSON.toJSONString(map)
    }

    /**
     * 回款审核二级菜单 参数
     */
    fun getBooksSiftStatsParams(list: List<Any>) : String {
        val map = HashMap<String, Any>()
        map[Constants.HTTP_PARAM_TRAN_NAME] = "GetBooksSiftStats"
        map[Constants.HTTP_PARAM_PARAM_STRING] = list
        return JSON.toJSONString(map)
    }


    /**
     * 收支类型（现金1、转账2、代扣3、金账户4）多个参数时以逗号隔开
     */
    fun getPayType(schedulePos: Int) : String {
        return when (schedulePos) {
            0 -> "1,2"
            1 -> "3"
            2 -> "4"
            else ->"0"
        }
    }

    /**
     * 金账户：待充值 6,已入账 1, 交易中5
     * 代扣： 待审核 0,审核拒绝2,已审核3,已发起代扣4,已入账1
     * 现金转账：待审核0,审核拒绝2,已入账1
     */
    fun getState(schedulePos : Int, auditPos : Int) : Int {
        return when (schedulePos) {
            0 -> when (auditPos) {
                0 -> 0
                1 -> 2
                2 -> 1
                else -> -1
            }
            1 -> when (auditPos) {
                0 -> 0
                1 -> 2
                2 -> 3
                3 -> 4
                4 -> 1
                else -> -1
            }
            2 -> when (auditPos) {
                0 -> 6
                1 -> 5
                2 -> 1
                else -> -1
            }
            else -> -1
        }
    }
}