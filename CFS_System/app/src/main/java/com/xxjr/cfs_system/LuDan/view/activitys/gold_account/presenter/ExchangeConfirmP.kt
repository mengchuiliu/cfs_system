package com.xxjr.cfs_system.LuDan.view.activitys.gold_account.presenter

import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.he_xin_chan_rong.ExchangeConfirmActivity

class ExchangeConfirmP : BasePresenter<ExchangeConfirmActivity, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {

    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
}