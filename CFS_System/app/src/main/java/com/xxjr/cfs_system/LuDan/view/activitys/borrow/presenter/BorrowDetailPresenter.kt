package com.xxjr.cfs_system.LuDan.view.activitys.borrow.presenter

import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.BorrowDetailMImp
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.activitys.borrow.view.BorrowDetailActivity

/**
 * Created by Administrator on 2017/10/31.
 */
class BorrowDetailPresenter : BasePresenter<BorrowDetailActivity, BorrowDetailMImp>() {
    private var lendingState = 0
    private var updateTime: String? = null

    override fun getModel(): BorrowDetailMImp = BorrowDetailMImp()

    override fun setDefaultValue() {
        getData(0, model.getParam(getBorrowDetailParamList(view.getBorrowId()), "ChangeLendRecord", "GET"), true)
    }

    fun auditPass() {
        val state: Int
        if (lendingState == 4) state = 5 else state = 4
        getData(1, model.getParam(getParamList(state), "ChangeLendRecord", "UpdateLendState"), true)
    }

    fun auditRefuse() {
        getData(2, model.getParam(getParamList(3), "ChangeLendRecord", "UpdateLendState"), true)
    }

    private fun getBorrowDetailParamList(borrowId: Int): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        list.add(borrowId)
        return list
    }

    //更新拆借进度
    private fun getParamList(state: Int): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        list.add(view.getBorrowId())
        list.add(state)
        list.add(view.getRemark())
        list.add(updateTime ?: "")
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    val temp = model.getBorrowInfo(data?.returnString ?: "")
                    updateTime = temp?.updateTime
                    if (temp != null) {
                        lendingState = temp.borrowState ?: 0
                        view.initRecycler(model.getBorrowDetailData(temp), temp)
                    } else {
                        view.showMsg("数据加载失败!")
                    }
                }
                1, 2 -> view.complete()
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
}