package com.xxjr.cfs_system.LuDan.presenter

import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem

class InterestPresenter : BasePresenter<BaseViewInter, ModelImp>() {

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
    }

    fun getDatas() = mutableListOf<CommonItem<Any>>().apply {
        for (i in 0..3) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> {
                        name = "贷款期限"
                        content = "年利率(%/年)"
                    }
                    1 -> {
                        name = "0-1年"
                        content = "4.35%"
                    }
                    2 -> {
                        name = "2-5年"
                        content = "4.75%"
                    }
                    3 -> {
                        name = "6-30年"
                        content = "4.9%"
                    }
                }
            })
        }
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
    }

    override fun onFailed(resultCode: Int, msg: String?) {
    }
}