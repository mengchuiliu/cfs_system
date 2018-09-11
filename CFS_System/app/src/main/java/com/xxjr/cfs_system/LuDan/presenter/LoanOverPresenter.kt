package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.HttpResult
import com.xxjr.cfs_system.LuDan.model.modelimp.LoanOverMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.LoanOverVIntrer
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class LoanOverPresenter(val view: LoanOverVIntrer) : HttpResult {
    private val model = LoanOverMImp()

    fun getData(searchType: Int, query1: String, query2: String, query3: String) {
        view.setIsFirst(false)
        model.getData(view.getFrgContext(), Hawk.get("SessionID"), 0, model.getParam(getParamList(searchType, query1, query2, query3), "GetLoanCaseList"), this, false)
    }

    private fun getParamList(searchType: Int, query1: String, query2: String, query3: String): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        val builder = StringBuilder()
        builder.append(" and DelMarker = 0 ")
        when (searchType) {
            1 -> builder.append(" and CustomerNames like '%").append(query1).append("%'")
            2 -> builder.append(" and LoanCode like '%").append(query1).append("%'")
            3 -> if (query1.isNotBlank()) builder.append(" and CompanyID in ('").append(query1).append("')")
        }
        if (query2.isNotBlank() && query3.isNotBlank()) {
            builder.append(" and CONVERT(varchar(100),UpdateTime, 23) >= '").append(query2).append("'")
            builder.append(" and CONVERT(varchar(100),UpdateTime, 23) <= '").append(query3).append("'")
        }
        builder.append(model.getQuery(view.getType()))
        list.add(builder.toString())
        list.add(view.getPage().toString())
        list.add("10")
        list.add(true)
        return list
    }

    override fun reusltSuccess(data: ResponseData?) {
        val jsonArray = JSON.parseArray(data?.data)
        if (jsonArray != null && jsonArray.isNotEmpty()) {
            Observable.create(Observable.OnSubscribe<Any> { subscriber ->
                model.getArray()
                subscriber.onNext("")
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<Any>() {

                        override fun onCompleted() {}

                        override fun onError(e: Throwable) {
                            showView(jsonArray)
                        }

                        override fun onNext(o: Any) {
                            showView(jsonArray)
                        }
                    })
        } else {
            if (view.getPage() == 0) {
                view.getLoanInfos().clear()
                view.refreshChange()
                view.showMsg("没有相关贷款数据!")
            } else {
                view.completeRefresh()
                view.showMsg("没有更多贷款数据了!")
            }
        }
    }

    override fun reusltFailed(msg: String?) = view.showMsg(msg)

    private fun showView(jsonArray: JSONArray) {
        val temp = model.getLoanList(jsonArray)
        if (temp.size == 0) {
            if (view.getPage() == 0) {
                view.getLoanInfos().clear()
                view.refreshChange()
                view.showMsg("没有相关贷款数据!")
            } else {
                view.showMsg("没有更多贷款数据了!")
            }
        } else {
            if (view.getPull()) {
                view.getLoanInfos().addAll(temp)
            } else {
                view.getLoanInfos().clear()
                view.getLoanInfos().addAll(temp)
            }
            view.refreshChange()
        }
        view.completeRefresh()
    }
}