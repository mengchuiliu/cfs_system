package com.xxjr.cfs_system.LuDan.view.activitys.gold_account.presenter

import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.he_xin_chan_rong.BindAccountActivity

class BindPresenter : BasePresenter<BindAccountActivity, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
    }

    fun bindAccount() {
        getData(0, model.getMoreParam(mutableListOf<Any>().apply {
            add(view.clientInfo.idCode)
            add(view.clientInfo.name)
            add(view.clientInfo.mobile)
            add("4")
        }, "NFexAccount", "Bind"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> view.bindSuccess()
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) {
    }

    fun canBind(): Boolean = when {
        view.clientInfo.name.isNullOrBlank() -> false
        view.clientInfo.idCode.isNullOrBlank() -> false
        view.clientInfo.mobile.isNullOrBlank() -> false//核新账户
        else -> true
    }
}