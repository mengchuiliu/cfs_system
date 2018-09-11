package com.xxjr.cfs_system.LuDan.presenter

import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.VisitDetailMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.VisitDetailVInter
import java.util.*

/**
 * Created by Administrator on 2017/12/13.
 */
class VisitDetailPresenter : BasePresenter<VisitDetailVInter, VisitDetailMImp>() {
    override fun getModel(): VisitDetailMImp = VisitDetailMImp()

    override fun setDefaultValue() {
        getData(0, model.getFunctionParam(getParamList(view.getCurYear(), view.getCurMonth()), "VisitMgr", "GetVisitStatisticsForCompany"), true)
    }

    private fun getParamList(year: Int, month: Int): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add(view.getCompanyId())
        list.add("$year-${if (month < 10) "0$month" else "$month"}-01 00:00:00")
        list.add(getMaxDayTime(year, month))
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
                view.initStoreRV(model.getStoreDatas(data.returnDataSet.getJSONArray("CompanyVisitInfo")))
                view.initClerkRV(model.getClerkDatas(data.returnDataSet.getJSONArray("SalesVisitInfo")))
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
}