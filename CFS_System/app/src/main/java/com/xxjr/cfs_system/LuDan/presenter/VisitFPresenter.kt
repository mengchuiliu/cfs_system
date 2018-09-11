package com.xxjr.cfs_system.LuDan.presenter

import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.VisitFMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.VisitFVInter
import java.util.*

/**
 * Created by Administrator on 2017/12/12.
 */
class VisitFPresenter : BasePresenter<VisitFVInter, VisitFMImp>() {
    override fun getModel(): VisitFMImp = VisitFMImp()

    override fun setDefaultValue() {
    }

    fun getStoreVisitDatas(year: Int, month: Int, companyType: String, companyZone_id: String) {
        getFragmentData(view.getFrgContext(), 0, model.getFunctionParam(getParamList(year, month, companyType, companyZone_id), "VisitMgr", "GetCompanyVisitStatistics"), true)
    }

    private fun getParamList(year: Int, month: Int, companyType: String, companyZone_id: String): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add("")
        list.add("$year-${if (month < 10) "0$month" else "$month"}-01 00:00:00")
        list.add(getMaxDayTime(year, month))
        list.add(companyType)
        list.add(companyZone_id)
        return list
    }

    private fun getMaxDayTime(year: Int, month: Int): String {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        val day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)//本月份的天数
        return "$year-${if (month < 10) "0$month" else "$month"}-$day 23:59:59"
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            if (data?.returnDataSet != null) {
                view.initRv(model.getStoreDatas(data.returnDataSet.getJSONArray("CompanyVisitInfo")))
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
}