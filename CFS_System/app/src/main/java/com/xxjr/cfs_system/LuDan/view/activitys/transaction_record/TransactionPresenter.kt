package com.xxjr.cfs_system.LuDan.view.activitys.transaction_record

import com.alibaba.fastjson.JSON
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import entity.TransactionRecord

class TransactionPresenter : BasePresenter<TransactionListActivity, ModelImp>() {
    private var homePage = 0
    private var searchType = 0

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        getTransactionData(0, 0)
    }

    fun getTransactionData(page: Int, searchType: Int) {
        homePage = page
        this.searchType = searchType
        getData(0, model.getMoreParam(mutableListOf<Any>().apply {
            if (view.chooseTime1.isNullOrBlank() && view.chooseTime2.isNullOrBlank()) {
                add("")
                add("")
            } else {
                add("${view.chooseTime1} 00:00:00")
                add("${view.chooseTime2} 23:59:59")
            }
            add(if (searchType == 3) view.searchCompany else "")//门店
            add(0)
            add(0)
            add(0)
            add(if (searchType == 1) view.searchContent else "")//流水号
            add(if (searchType == 2) view.searchContent else "")//操作人
            add(if (searchType == 4) view.searchContent else "")//交易账户
            add(homePage)
            add(10)
        }, "TradingFlow", "GetList"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.data ?: "").isNotBlank()) {
                        val temp = JSON.parseArray(data?.data ?: "", TransactionRecord::class.java)
                        if (temp.size == 0) {
                            if (homePage == 0) {
                                view.showMsg("还没有交易数据!")
                                view.transactionRecords.clear()
                                view.refreshChange()
                            } else {
                                view.showMsg("没有更多交易数据!")
                            }
                        } else {
                            if (view.pull) {
                                view.transactionRecords.addAll(temp)
                            } else {
                                view.transactionRecords.clear()
                                view.transactionRecords.addAll(temp)
                            }
                            view.refreshChange()
                        }
                        view.completeRefresh()
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) {
        view.completeRefresh()
        view.showMsg(msg)
    }
}