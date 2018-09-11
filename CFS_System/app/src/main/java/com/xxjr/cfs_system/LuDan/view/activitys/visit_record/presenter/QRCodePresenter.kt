package com.xxjr.cfs_system.LuDan.view.activitys.visit_record.presenter

import com.alibaba.fastjson.JSON
import com.orhanobut.logger.Logger
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.activitys.visit_record.QRCodeActivity
import java.util.HashMap

class QRCodePresenter : BasePresenter<QRCodeActivity, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        getData(0, getParam(), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            if ((data?.returnString ?: "").isNotBlank()) {
                view.createQRCode(data?.returnString ?: "")
            } else {
                view.showMsg("二维码生成失败：" + data?.returnMsg)
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg("二维码生成失败：$msg")

    //通用参数组装
    private fun getParam(): String {
        val map = HashMap<String, Any>()
        map["Function"] = "GetRegistrationUrl"
        map["IsUseZip"] = false
        map["TranName"] = "VisitMgr"
        Logger.e("==获取url==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

}