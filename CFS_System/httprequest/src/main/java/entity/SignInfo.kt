package entity

import android.content.Context
import android.widget.Toast
import java.io.Serializable

/**
 * Created by Administrator on 2018/1/17.
 */
class SignInfo : Serializable {
    var companyID: String? = null //门店id
    var companyName: String? = null //所属公司
    var zoneName: String? = null //所属区域
    var aisleType: String? = null//代扣平台
    var bankCode: String? = null//银行编码 - 签约银行
    var accountType: String? = null//银行卡类型
    var accountNo: String? = null//银行卡号码
    var accountName: String? = null//持卡人姓名
    var accountProp: String? = null//账户属性
    var IDType: String? = null//证件类型
    var IDCardNo: String? = null//证件号码
    var friendCardNo: String? = null//亲友身份证
    var telPhone: String? = null//手机号码
    var signer: String? = null//签约者身份
    var bankRule: String? = null // 银行规则
    var protocolNo: String? = null //协议号
    var protocolState: String? = null //协议状态
    var remark: String? = null //冻结原因

    fun check(context: Context): Boolean {
        if (accountName.isNullOrBlank()) {
            showMsg(context, "持卡人不能为空")
            return false
        }
        if (aisleType.isNullOrBlank()) {
            showMsg(context, "请选择代扣平台")
            return false
        }
        if (bankCode.isNullOrBlank()) {
            showMsg(context, "请选择签约银行")
            return false
        }
        if (accountType.isNullOrBlank()) {
            showMsg(context, "请选择银行卡类型")
            return false
        }
        if (accountProp.isNullOrBlank()) {
            showMsg(context, "请选择账号属性")
            return false
        }
        if (accountNo.isNullOrBlank()) {
            showMsg(context, "银行卡号不能为空")
            return false
        }
        if (signer.isNullOrBlank()) {
            showMsg(context, "请选择签约者身份")
            return false
        }
        if (IDType.isNullOrBlank()) {
            showMsg(context, "请选择证件类型")
            return false
        }
        if (IDCardNo.isNullOrBlank()) {
            showMsg(context, "证件号不能为空")
            return false
        }
        if (telPhone.isNullOrBlank()) {
            showMsg(context, "请正确的填写手机号")
            return false
        }
        return true
    }

    private fun showMsg(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}