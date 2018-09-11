package com.xxjr.cfs_system.LuDan.presenter

import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.AuditCostMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.AuditCostVInter

class AuditCostPresenter : BasePresenter<AuditCostVInter, AuditCostMImp>() {
    var AuditStatus: Int = -1

    override fun getModel(): AuditCostMImp = AuditCostMImp()

    override fun setDefaultValue() {
        view.initRecycler(model.getItemData(view.getLoanInfo()))
    }

    //通过拒绝
    fun auditPass(costId: Int, auditStatus: Int, remark: String) {
        AuditStatus = auditStatus
        getData(0, model.getParam(getAuditParamList(costId, remark), "AuditLoanCost"), true)
    }

    //删除
    fun delCost(delId: Int) {
        getData(1, model.getParam(getDelParamList(delId), "DeleteLoanCost"), true)
    }

    private fun getDelParamList(delId: Int): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        list.add(delId)
        return list
    }

    private fun getAuditParamList(costId: Int, remark: String): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        list.add(costId)
        list.add(AuditStatus)
        list.add(remark)
        return list
    }


    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    when (AuditStatus) {
                        1, 3, 6, 8 -> view.showMsg("审核已通过!")
                        else -> view.showMsg("审核已拒绝!")
                    }
                    view.complete()
                }
                1 -> {
                    view.showMsg("删除成功!")
                    view.complete()
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
}