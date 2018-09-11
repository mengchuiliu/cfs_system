package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.CustomerAccountMImp
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.CustomerAccountActivity

/**
 * Created by Administrator on 2018/3/29.
 */
class CustomerAccountP : BasePresenter<CustomerAccountActivity, CustomerAccountMImp>() {
    override fun getModel(): CustomerAccountMImp = CustomerAccountMImp()

    override fun setDefaultValue() {
        getData(0, model.getCustomerParam(), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            if ((data?.returnString ?: "").isNotBlank()) {
                val jsonArray = JSON.parseArray(data?.returnString ?: "")
                if (jsonArray != null && jsonArray.size != 0) {
                    view.initRV(model.getCustomerData(jsonArray))
                } else {
                    view.initRV(mutableListOf())
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
}