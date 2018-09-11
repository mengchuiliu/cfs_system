package entity

import android.content.Context
import android.widget.Toast
import java.io.Serializable
import java.util.regex.Pattern

/**
 * Created by Administrator on 2018/3/12.
 */
class GoldRegisteredInfo : Serializable {
    var userNo: String = "" //金账户编号
    var customerName: String? = null//客户名称
    var enterpriseName: String? = null//企业名称
    var personName: String? = null//法人名称
    var IDType: Int = -1//证件类型
    var IDCardNo: String? = null//证件号码
    var telPhone: String? = null//手机号码
    var email: String? = null//邮箱
    var area: String? = null //开户行地区
    var bankId: Int = -1//银行id
    var bankName: String? = null//银行名称
    var branch: String? = null//支行名称
    var bankNo: String? = null//银行卡号码
    var insertTime: String = ""//时间

    fun check(context: Context, type: Int): Boolean {
        if (customerName.isNullOrBlank() && type == 1) {
            showMsg(context, "客户名称不能为空")
            return false
        }
        if (enterpriseName.isNullOrBlank() && type == 2) {
            showMsg(context, "企业名称不能为空")
            return false
        }
        if (IDType == -1) {
            showMsg(context, "请选择证件类型")
            return false
        }
        if (IDCardNo.isNullOrBlank()) {
            showMsg(context, "证件号不能为空")
            return false
        }
        if (telPhone.isNullOrBlank() && telPhone?.length != 11) {
            showMsg(context, "请正确的填写手机号")
            return false
        }
        if ((email ?: "").isNotBlank()) {
            if (!checkEmail(email ?: "")) {
                showMsg(context, "email格式不正确")
                return false
            }
        }
        if (bankId == -1) {
            showMsg(context, "请选择银行")
            return false
        }
        if (area.isNullOrBlank()) {
            showMsg(context, "请选择开户行地区")
            return false
        }
        if (bankNo.isNullOrBlank()) {
            showMsg(context, "银行卡号不能为空")
            return false
        }
        return true
    }

    private fun showMsg(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun checkEmail(email: String): Boolean {
        val regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*"
        return Pattern.matches(regex, email)
    }
}