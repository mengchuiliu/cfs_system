package entity

import android.content.Context
import android.widget.Toast

/**
 * Created by Administrator on 2017/9/21.
 * 出入帐
 */
class BookRecord {
    var bookType = -2 //转账类型
    var payType = -2 //支付类型
    var amount = 0.0 //金额
    var serviceCharge = 0.0 //手续费
    var payTime = "" //支付时间
    var digest = "" //摘要
    var ourName = "" //收款方户名
    var ourAccount = "" //收款方账户
    var otherName = "" //付款方户名
    var otherAccount = "" //付款方账户
    var remark = "" //合同备注
    var protocolId = -1 //协议id

    fun check(context: Context, payType: Int): Boolean {
        when {
            bookType == -2 -> {
                showMsg(context, "请选择收支类型!")
                return false
            }
            payType == -2 -> {
                showMsg(context, "请选择支付方式!")
                return false
            }
            amount == 0.0 -> {
                showMsg(context, "请填写金额!")
                return false
            }
            payTime.isBlank() -> {
                showMsg(context, "请选择支付时间!")
                return false
            }
        }
        if (payType == 4) {
            if (protocolId == -1) {
                showMsg(context, "请选择金账户!")
                return false
            }
        } else {
            if (ourName.isBlank()) {
                if (payType == 3) {
                    showMsg(context, "请选择代扣平台!")
                } else {
                    showMsg(context, "请选择收款方账户!")
                }
                return false
            }
            if (otherName.isBlank()) {
                showMsg(context, "请选择付款方账户!")
                return false
            }
        }
        return true
    }

    private fun showMsg(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}