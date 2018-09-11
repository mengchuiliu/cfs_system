package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSONArray
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.reimbursement_remind.RemindListActivity
import entity.LoanInfo

/**
 * Created by Administrator on 2018/4/8.
 */
class RemindListPresenter : BasePresenter<RemindListActivity, ModelImp>() {

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        getData(0, model.getParam(null, "GetLoanUpComingList"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            if ((data?.returnString ?: "").isNotBlank()) {
                val jsonArray = JSONArray.parseArray(data?.returnString)
                if (jsonArray.isNotEmpty()) {
                    view.initRv(getItems(jsonArray))
                } else {
                    view.initRv(mutableListOf())
                }
            } else {
                view.initRv(mutableListOf())
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    private fun getItems(jsonArray: JSONArray): MutableList<LoanInfo> {
        val list = mutableListOf<LoanInfo>()
        for (i in jsonArray.indices) {
            val jsonObject = jsonArray.getJSONObject(i)
            list.add(LoanInfo().apply {
                loanId = jsonObject.getString("ID")
                loanCode = jsonObject.getString("LoanCode")
                loanDescription = jsonObject.getString("LoadInfo")
                customer = jsonObject.getString("CustomerNames")
                lendingAmount = jsonObject.getDoubleValue("LendAmount")
                returnTime = jsonObject.getString("MonthPayDay")
            })
        }
        return list
    }
}