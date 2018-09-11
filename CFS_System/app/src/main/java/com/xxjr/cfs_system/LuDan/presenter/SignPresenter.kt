package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import cn.jpush.android.api.JPushInterface
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.SignActivity

/**
 * Created by Administrator on 2018/1/16.
 */
class SignPresenter : BasePresenter<SignActivity, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {}

    //发送签约验证短信
    fun sendSignSMS() {
        getData(0, model.getParam(getSendListParam(), "ReSendSMS"), true)
    }

    //签约
    fun sign(sms: String) {
        if (sms.isBlank()) {
            view.showMsg("请正确的填写验证码!")
        } else {
            getData(1, model.getParam(getSignListParam(sms), "CfrmRealPay"), true)
        }
    }

    fun sendLoginSMS() {
        getData(2, model.getParam(mutableListOf(), "SendSMSVerificationCode"), true)
    }

    fun loginVerification(sms: String) {
        if (sms.isBlank()) {
            view.showMsg("请正确的填写验证码!")
        } else {
            getData(3, model.getParam(getLoginListParam(sms), "LoginSMSVerification"), true)
        }
    }

    private fun getSendListParam(): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add(view.getSignCode())
        list.add(view.getAisleType())
        return list
    }

    private fun getSignListParam(sms: String): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add(view.getSignCode())
        list.add(sms)
        list.add(view.getAisleType())
        return list
    }

//    private fun getLoginSendListParam(telphone: String): MutableList<Any> {
//        val list = mutableListOf<Any>()
//        list.add(JPushInterface.getRegistrationID(view as Context))
//        return list
//    }

    private fun getLoginListParam(sms: String): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add(sms)
        list.add(JPushInterface.getRegistrationID(view as Context))
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0, 2 -> {
                    view.showMsg("验证码已发送")
                    view.countStart()
                }
                1, 3 -> {
                    view.complete()
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) {
        view.showMsg(msg)
//                4001	无需验证
//                4002	已验证成功，勿重复验证
//                4003	已验证失败，请重新发起申请
//                3999	发送失败

    }
}