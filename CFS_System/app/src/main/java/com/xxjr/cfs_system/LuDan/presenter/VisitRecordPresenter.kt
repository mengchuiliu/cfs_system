package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.VisitRecordMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.VisitRecordVInter
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.VisitRecord
import java.util.ArrayList

/**
 * Created by Administrator on 2017/12/7.
 */
class VisitRecordPresenter : BasePresenter<VisitRecordVInter, VisitRecordMImp>() {
    private var homePage = 0
    private var type = 0

    override fun getModel(): VisitRecordMImp = VisitRecordMImp()

    override fun setDefaultValue() {
        refreshRecord(0, 0, 0)
    }

    fun refreshRecord(page: Int, searchType: Int, type: Int) {
        if (isViewAttached) {
            homePage = page
            this.type = type
            when (type) {
                0 -> getData(type, model.getFunctionParam(getRecordListParam(page, searchType),
                        "VisitMgr", "GetVisitCustomerList", "DB_CFS_Loan"), false) //来访记录
                1 -> {
                    getData(type, model.getMoreParam(mutableListOf<Any>().apply {
                        add(homePage.toString())//页码，从0开始
                        add("10")//页面项目数
                        add("")//过滤条件，指定记录Id
                        add("")//过滤条件，预约上门时间开始
                        add("")//过滤条件，预约上门时间结束
                        add("")// 过滤条件，区域Id
                        add("")//过滤条件，公司Id
                        add(if (searchType == 2) view.searchContent else "")//过滤条件，公司名称
                        add(if (searchType == 1) view.searchContent else "")//过滤条件，关键词（客户名称、客户手机号码）
                        if ((view.chooseTime1 ?: "").isNotBlank() && (view.chooseTime2
                                        ?: "").isNotBlank()) {
                            add("${view.chooseTime1} 00:00")//过滤条件，记录创建时间开始
                            add("${view.chooseTime2} 23:59")//过滤条件，记录创建时间结束
                        } else {
                            add("")//过滤条件，记录创建时间开始
                            add("")//过滤条件，记录创建时间结束
                        }
                    }, "Invitation", "GetList"), true)
                }//邀约
                2 -> getData(type, model.getFunctionParam(getFeedbackParam(page, searchType), "VisitMgr",
                        "GetSuggestionList", "DB_CFS_VisitMgr"), false)//客户反馈
            }
        }
    }

    //来访列表参数
    private fun getRecordListParam(page: Int, searchType: Int): MutableList<Any> {
        val list = ArrayList<Any>()
        val builder = StringBuilder()
        if (searchType == 1) {
            builder.append(" and CustomerName like '%").append(view.searchContent).append("%'")
        } else if (searchType == 2) {
            builder.append(" and CompanyName like '%").append(view.searchContent).append("%'")
        }
        if ((view.chooseTime1 ?: "").isNotBlank() && (view.chooseTime2 ?: "").isNotBlank()) {
            builder.append(" and VisitTime BETWEEN '").append(view.chooseTime1).append("'")
            builder.append(" AND '").append(DateUtil.getOldDate(1, view!!.chooseTime2)).append("'")
        }
        list.add(builder.toString())
        list.add(page.toString())
        list.add("10")
        list.add(true)
        return list
    }

    //客户反馈参数
    private fun getFeedbackParam(page: Int, searchType: Int) = mutableListOf<Any>().apply {
        add("1")
        val builder = StringBuilder()
        if (searchType == 1) {
            builder.append(" Phone like '%").append(view!!.searchContent).append("%'")
        }
        if ((view.chooseTime1 ?: "").isNotBlank() && (view.chooseTime2 ?: "").isNotBlank()) {
            builder.append(" InsertTime BETWEEN '").append(view!!.chooseTime1).append("'")
            builder.append(" AND '").append(DateUtil.getOldDate(1, view!!.chooseTime2)).append("'")
        }
        add(builder.toString())
        add(page.toString())
        add("10")
        add("InsertTime DESC")
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                type -> {
                    val temp: MutableList<VisitRecord> = if (!data?.data.isNullOrBlank()) {
                        when (type) {
                            0 -> JSON.parseArray<VisitRecord>(data?.data, VisitRecord::class.java)
                            1 -> {
                                val jsonArray = JSONArray.parseArray(data?.data) ?: JSONArray()
                                model.getInviteData(jsonArray)
                            }
                            2 -> {
                                val jsonArray = JSONArray.parseArray(data?.data) ?: JSONArray()
                                model.getFeedbackData(jsonArray)
                            }
                            else -> mutableListOf()
                        }
                    } else {
                        mutableListOf()
                    }
                    if (temp.size == 0) {
                        if (homePage == 0) {
                            view.showMsg("没有相关的数据!")
                            view.getVisitRecords().clear()
                            view.refreshChange()
                        } else {
                            view.showMsg("没有更多的数据!")
                        }
                    } else {
                        if (view.pull) {
                            view.getVisitRecords().addAll(temp)
                        } else {
                            view.getVisitRecords().clear()
                            view.getVisitRecords().addAll(temp)
                        }
                        view.refreshChange()
                    }
                    view.completeRefresh()
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun getVisitTitles() = mutableListOf<CommonItem<Any>>().apply {
        for (i in 0..1) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> {
                        isClick = true
                        name = "来访记录"
                    }
                    1 -> name = "邀约记录"
                }
            })
        }
        val permits10 = CFSUtils.getPostPermitValue(Hawk.get<String>("PermitValue", ""), "810")
        if (permits10 != null && permits10.contains("EA")) {
            add(CommonItem<Any>().apply { name = "客户反馈" })
        }
    }
}