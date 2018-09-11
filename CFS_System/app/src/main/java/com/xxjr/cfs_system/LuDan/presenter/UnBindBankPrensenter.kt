package com.xxjr.cfs_system.LuDan.presenter

import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.UnBindBankMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.UnBindBankVInter
import entity.Bank

/**
 * Created by Administrator on 2017/10/18.
 */
class UnBindBankPrensenter : BasePresenter<UnBindBankVInter, UnBindBankMImp>() {
    override fun getModel(): UnBindBankMImp = UnBindBankMImp()

    override fun setDefaultValue() {
        view.initRecycler(model.getListData(view.getBank() ?: Bank()))
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
}