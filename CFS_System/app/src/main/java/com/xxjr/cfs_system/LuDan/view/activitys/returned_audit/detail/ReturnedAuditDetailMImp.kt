package com.xxjr.cfs_system.LuDan.view.activitys.returned_audit.detail

import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.tools.Constants

/**
 *
 * 回款审核详情   业务方法
 * @author huangdongqiang
 * @date 27/06/2018
 */
class ReturnedAuditDetailMImp : ModelImp() {

    /**
     * 是否是代扣
     */
    fun isWithhold(schedulePos: Int): Boolean {
        return when (schedulePos) {
            1 -> true
            else -> false
        }
    }

    /**
     * 是否是金账户
     */
    fun isGoldInfo(schedulePos: Int): Boolean {
        return when (schedulePos) {
            2 -> true
            else -> false

        }
    }

    /**
     *  是否显示  "审核" 与 "拒绝" 底部按钮
     * 「现金／转账」 的 「待审核」 +  「代扣」 的 「待审核」
     */
    fun isShowWaitForCheckLayout(schedulePos: Int, auditPos: Int): Boolean {
        return when (schedulePos) {
            0 -> when (auditPos) {
                0 -> true
                else -> false
            }
            1 -> when (auditPos) {
                0 -> true
                else -> false
            }
            else -> false
        }
    }

    /**
     * 是否显示发起代扣按钮
     * 「代扣」 的 「已审核」下显示
     */
    fun isShowWithholdLayout(schedulePos: Int, auditPos: Int): Boolean {
        return when (schedulePos) {
            1 -> when (auditPos) {
                2 -> true
                else -> false
            }
            else -> false
        }
    }

    /**
     * 获取代扣信息请求参数
     */
    fun getWithholdParams(list: List<Any>): String {
/*      val map = HashMap<String, Any>()
        map[Constants.HTTP_PARAM_TRAN_NAME] = "Get_Info_ByWhere"
        map[Constants.HTTP_PARAM_USER_ID] = Hawk.get("UserID")
        map[Constants.HTTP_PARAM_PARAM_STRING] = list
        map[Constants.HTTP_PARAM_DB_MARKER] = "DB_CFS_Loan"
        return JSON.toJSONString(map)*/

        val map = HashMap<String, Any>()
        map[Constants.HTTP_PARAM_TRAN_NAME] = "GetLoanProtocol"
        map[Constants.HTTP_PARAM_USER_ID] = Hawk.get("UserID")
        map[Constants.HTTP_PARAM_PARAM_STRING] = list
        map[Constants.HTTP_PARAM_DB_MARKER] = "DB_CFS"
        map[Constants.HTTP_PARAM_FUNCTION] = "Pager"
        map[Constants.HTTP_PARAM_MARKER] = "HQServer"
        return JSON.toJSONString(map)
    }

    /**
     * 通用接口
     * 获取代扣信息请求参数
     */
    fun getWithholdParams(list: List<Any>, tranName: String): String {
        val map = java.util.HashMap<String, Any>()
        map["Action"] = "GET"
        map[Constants.HTTP_PARAM_DB_MARKER] = "DB_CFS_Loan"
        map[Constants.HTTP_PARAM_MARKER] = "HQServer"
        map["IsUseZip"] = false
        map[Constants.HTTP_PARAM_PARAM_STRING] = list
        map[Constants.HTTP_PARAM_TRAN_NAME] = tranName
        return JSON.toJSONString(map)
    }

    /**
     * App获取回款审核列表（金账户信息）参数
     */
    fun getGoldInfoParams(list: List<Any>): String {
        val map = HashMap<String, Any>()
        map[Constants.HTTP_PARAM_TRAN_NAME] = "Get_Info_ByWhere"
        map[Constants.HTTP_PARAM_USER_ID] = Hawk.get("UserID")
        map[Constants.HTTP_PARAM_PARAM_STRING] = list
        return JSON.toJSONString(map)
    }


    /**
     * App回款审核审批通过拒绝操作 参数
     */
    fun getCheckActionParams(list: List<Any>): String {
        val map = HashMap<String, Any>()
        map[Constants.HTTP_PARAM_TRAN_NAME] = "AuditReceivable"
        map[Constants.HTTP_PARAM_USER_ID] = Hawk.get("UserID")
        map[Constants.HTTP_PARAM_PARAM_STRING] = list
        return JSON.toJSONString(map)
    }

    /**
     * App回款审核审批通过拒绝操作 参数 代扣 - 只有代扣平台AisleType为（通联支付1，上海富友2，通联协议5）
     */
    fun getCheckActionSepcailParams(list: List<Any>): String {
        val map = HashMap<String, Any>()
        map[Constants.HTTP_PARAM_TRAN_NAME] = "BooksAuditMoney"
        map[Constants.HTTP_PARAM_USER_ID] = Hawk.get("UserID")
        map[Constants.HTTP_PARAM_PARAM_STRING] = list
        map[Constants.HTTP_PARAM_DB_MARKER] = "DB_CFS_Loan"
        return JSON.toJSONString(map)
    }


    /**
     * App回款审核发起代扣
     */
    fun getWithholdActionParams(list: List<Any>): String {
        val map = HashMap<String, Any>()
        map[Constants.HTTP_PARAM_TRAN_NAME] = "SingleTradeCollect"
        map[Constants.HTTP_PARAM_USER_ID] = Hawk.get("UserID")
        map[Constants.HTTP_PARAM_PARAM_STRING] = list
        return JSON.toJSONString(map)
    }

    /**
     * App回款审核上传资料（图片）查看
     */
    fun getFileCheckParams(list: List<Any>): String {
        val map = HashMap<String, Any>()
        map[Constants.HTTP_PARAM_TRAN_NAME] = "Get_Info_ByWhere"
        map[Constants.HTTP_PARAM_USER_ID] = Hawk.get("UserID")
        map[Constants.HTTP_PARAM_PARAM_STRING] = list
        map[Constants.HTTP_PARAM_DB_MARKER] = "DB_CFS_Loan"
        return JSON.toJSONString(map)
    }

    /**
     * App回款审核上传资料（图片）删除
     */
    fun getFileDeleteParams(list: List<Any>): String {
        val map = HashMap<String, Any>()
        map[Constants.HTTP_PARAM_TRAN_NAME] = "BooksDelFile"
        map[Constants.HTTP_PARAM_USER_ID] = Hawk.get("UserID")
        map[Constants.HTTP_PARAM_PARAM_STRING] = list
        map[Constants.HTTP_PARAM_DB_MARKER] = "DB_CFS_Loan"
        return JSON.toJSONString(map)
    }
}