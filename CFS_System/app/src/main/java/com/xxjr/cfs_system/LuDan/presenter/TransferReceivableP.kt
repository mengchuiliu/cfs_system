package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSONArray
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.TransferReceivableMImp
import com.xxjr.cfs_system.LuDan.view.activitys.transfer_receivable.TransferReceivableActivity

/**
 * Created by Administrator on 2018/4/9.
 */
class TransferReceivableP : BasePresenter<TransferReceivableActivity, TransferReceivableMImp>() {
    override fun getModel(): TransferReceivableMImp = TransferReceivableMImp()

    override fun setDefaultValue() {}

    fun getData() {
        when (view.getType()) {
            0 -> getData(0, model.getDataParam("GoldAccount", "GetRechargeCodeListApp"), true)
            1 -> getData(1, model.getDataParam("GoldAccount", "GetWaitTransfer"), true)
        }
    }

    fun uploadGoldInfo(ids: String, tradeSn: String, totalAmount: Double, companyId: String, UserNo: String, payType: Int, protocolID: String, json: String) {
        val list = mutableListOf<Any>()
        list.add(ids)
        list.add(tradeSn)
        list.add(totalAmount)
        list.add(companyId)
        list.add(UserNo)
        list.add(payType)
        list.add(protocolID)
        getData(2, model.getTransferParam("BooksGoldAccountCollent", list, json), false)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.data ?: "").isNotBlank()) {
                        val jsonArray = JSONArray.parseArray(data?.data)
                        if (jsonArray.isNotEmpty()) {
                            view.refreshCode(model.getCodeData(jsonArray))
                        } else {
                            view.refreshCode(mutableListOf())
                        }
                    } else {
                        view.refreshCode(mutableListOf())
                    }
                }
                1 -> {
                    if ((data?.returnString ?: "").isNotBlank()) {
                        val jsonArray = JSONArray.parseArray(data?.returnString)
                        if (jsonArray.isNotEmpty()) {
                            view.refreshTransfer(model.getTransferData(jsonArray))
                        } else {
                            view.refreshTransfer(mutableListOf())
                        }
                    } else {
                        view.refreshTransfer(mutableListOf())
                    }
                }
                2 -> getData()
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
}