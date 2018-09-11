package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.CostListMImp
import com.xxjr.cfs_system.LuDan.view.activitys.CostListActivity
import com.xxjr.cfs_system.LuDan.view.activitys.TaskListActivity
import com.xxjr.cfs_system.LuDan.view.viewinter.CostVInter
import com.xxjr.cfs_system.LuDan.view.viewinter.LoanVInter
import entity.CommonItem
import java.util.ArrayList

class CostListPresenter : BasePresenter<CostListActivity, CostListMImp>() {
    private var homePage = 0
    private var schedulePos = 0 //状态位置
    private var auditPos = 0 //审核状态位置

    override fun getModel(): CostListMImp = CostListMImp()

    override fun setDefaultValue() {
        auditPos = view.getAuditPos()
        schedulePos = view.getSchedulePos()
        view.showChooseItem()
        costRefresh(0, 0, schedulePos, auditPos)
    }

    fun costRefresh(page: Int, searchType: Int, schedulePos: Int, auditPos: Int) {
        if (isViewAttached) {
            homePage = page
            this.schedulePos = schedulePos
            this.auditPos = auditPos
            getData(0, model.getParam(getCostListParam(page, searchType, schedulePos, auditPos), "GetLoanCostList"), false)
            getData(1, model.getParam(getCostListNumParam(searchType), "GetLoanCostInfoStatistic"), true)
        }
    }

    //成本列表参数
    private fun getCostListParam(page: Int, searchType: Int, schedulePos: Int, auditPos: Int): MutableList<Any> {
        val list = ArrayList<Any>()
        val builder = StringBuilder()
        builder.append(" and DelMarker = 0 ")
        if (isViewAttached) {
            if (searchType == 1) {
                builder.append(" and CustomerNames like '%").append(view!!.searchContent).append("%'")
            } else if (searchType == 2) {
                builder.append(" and LoanCode like '%").append(view!!.searchContent).append("%'")
            } else if (searchType == 3) {
                if ((view.searchCompany ?: "").isNotBlank())
                    builder.append(" and CompanyID in ('").append(view.searchCompany).append("')")
            }
            if ((view.chooseTime1 ?: "").isNotBlank() && (view.chooseTime2 ?: "").isNotBlank()) {
                builder.append(" and CONVERT(varchar(100),UpdateTime, 23)>= '").append(view!!.chooseTime1).append("'")
                builder.append(" and CONVERT(varchar(100),UpdateTime, 23)<= '").append(view!!.chooseTime2).append("'")
            }
        }
        builder.append(model.getQuery(schedulePos, auditPos))
        list.add(builder.toString())
        list.add(page.toString())
        list.add("10")
        list.add("1")
        return list
    }

    //标题列表参数
    private fun getCostListNumParam(searchType: Int): MutableList<Any> {
        val list = ArrayList<Any>()
        val builder = StringBuilder()
        builder.append(" and DelMarker = 0 ")
        if (isViewAttached) {
            if (searchType == 1) {
                builder.append(" and CustomerNames like '%").append(view!!.searchContent).append("%'")
            } else if (searchType == 2) {
                builder.append(" and LoanCode like '%").append(view!!.searchContent).append("%'")
            } else if (searchType == 3) {
                if ((view.searchCompany ?: "").isNotBlank())
                    builder.append(" and CompanyID in ('").append(view.searchCompany).append("')")
            }
            if ((view.chooseTime1 ?: "").isNotBlank() && (view.chooseTime2 ?: "").isNotBlank()) {
                builder.append(" and CONVERT(varchar(100),UpdateTime, 23)>= '").append(view!!.chooseTime1).append("'")
                builder.append(" and CONVERT(varchar(100),UpdateTime, 23)<= '").append(view!!.chooseTime2).append("'")
            }
        }
        list.add(builder.toString())
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    val temp = model.getCostList(data?.data ?: "")
                    if (temp.size == 0) {
                        if (homePage == 0) {
                            view.showMsg("没有要审核的成本!")
                            view.getCost().clear()
                            view.refreshChange()
                        } else {
                            view.showMsg("没有更多要审核的成本!")
                        }
                    } else {
                        if (view.pull) {
                            view.getCost().addAll(temp)
                        } else {
                            view.getCost().clear()
                            view.getCost().addAll(temp)
                        }
                        view.refreshChange()
                    }
                    view.completeRefresh()
                }
                1 -> {
                    val jsonArray = JSON.parseArray(data?.data ?: "")
                    if (jsonArray != null && jsonArray.isNotEmpty()) {
                        val jsonObject = jsonArray.getJSONObject(0)
                        (view as CostListActivity).refreshTitleData0(model.getTitleData0(jsonObject, schedulePos))
                        (view as CostListActivity).refreshTitleData1(model.getTitleData1(jsonObject, auditPos))
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) {
        view.showMsg(msg)
    }

    fun getTitles(auditPos: Int): List<CommonItem<*>> {
        val titles = ArrayList<CommonItem<*>>()
        var commonItem: CommonItem<*>
        for (i in 0..4) {
            commonItem = CommonItem<Any>()
            commonItem.isClick = (auditPos == i)
            when (i) {
                0 -> commonItem.setName("全部")
                1 -> commonItem.setName("审核中")
                2 -> commonItem.setName("无需报销")
                3 -> commonItem.setName("未付款")
                4 -> commonItem.setName("已付款")
            }
            titles.add(commonItem)
        }
        return titles
    }

    fun getTitles1(auditPos: Int): List<CommonItem<*>> {
        val titles = ArrayList<CommonItem<*>>()
        var commonItem: CommonItem<*>
        for (i in 0..10) {
            commonItem = CommonItem<Any>()
            commonItem.isClick = (auditPos == i)
            when (i) {
                0 -> commonItem.setName("全部")
                1 -> commonItem.setName("待门店经理审核")
                2 -> commonItem.setName("门店经理审核拒绝")
                3 -> commonItem.setName("待按揭经理审核")
                4 -> commonItem.setName("按揭经理审核拒绝")
                5 -> commonItem.setName("待特定人审核")
                6 -> commonItem.setName("特定人审核拒绝")
                7 -> commonItem.setName("待会计审核")
                8 -> commonItem.setName("会计审核拒绝")
                9 -> commonItem.setName("会计审核通过")
                10 -> commonItem.setName("总部财务添加")
            }
            titles.add(commonItem)
        }
        return titles
    }
}