package com.xxjr.cfs_system.LuDan.presenter

import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.forget_update_psw.ResetPswActivity

class ResetPswPresenter : BasePresenter<ResetPswActivity, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
    }

    //重置密码
    fun resetPsw(account: String, psw: String, sms: String) {
        getData(0, model.getParam(mutableListOf<Any>().apply {
            add(account)
            add(psw)
            add(sms)
        }, "ResetPwd"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> view.over()
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
}