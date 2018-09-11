package com.xxjr.cfs_system.LuDan.presenter

import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.viewinter.WebVInter

/**
 * Created by Administrator on 2017/11/28.
 */
class WebPresenter : BasePresenter<WebVInter, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
}