package com.xxjr.cfs_system.LuDan.presenter

import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.forget_update_psw.ForgetPswActivity

class ForgetPswPresenter : BasePresenter<ForgetPswActivity, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
    }

    //验证手机并获取验证码
    fun getSMS(account: String, phone: String) {
        getData(0, model.getParam(mutableListOf<Any>().apply {
            add(account)
            add(phone)
        }, "CheckMobile"), false)
    }

    fun checkSMS(account: String, sms: String) {
        getData(1, model.getParam(mutableListOf<Any>().apply {
            add(account)
            add(sms)
        }, "VerifyCode"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> view.setSMS()
                1 -> view.goResetPsw()
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
}