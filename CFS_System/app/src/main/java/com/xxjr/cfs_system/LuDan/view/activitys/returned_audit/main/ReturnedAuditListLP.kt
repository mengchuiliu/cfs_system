package com.xxjr.cfs_system.LuDan.view.activitys.returned_audit.main

import android.app.Activity
import android.content.Intent
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.tools.Constants
import entity.ChooseType
import entity.CommonItem
import entity.ReturnedAuditCountEntity
import entity.ReturnedAuditEntity
import rx.Subscription
import java.util.*
import kotlin.collections.HashMap

/**
 *
 * 回款审核 逻辑处理类
 * @author huangdongqiang
 * @date 25/06/2018
 */
class ReturnedAuditListLP : BasePresenter<ReturnedAuditListActivity, ReturnedAuditListMImp>() {
    private var companySubscription: Subscription? = null
    var schedulePos = 0 // 一级菜单栏点击的位置
    var auditPos = 0    // 二级菜单栏点击的位置
    private var pageSize = 10   //每页数量
    var companyId = ""
    var resultList = mutableListOf<ReturnedAuditEntity>()

    override fun getModel(): ReturnedAuditListMImp = ReturnedAuditListMImp()


    fun getDefaultIntent(intent: Intent) {
        auditPos = intent.getIntExtra("AuditPos", 0)

    }

    override fun setDefaultValue() {
        if (!isViewAttached) {
            return
        }

        companySubscription = RxBus.getInstance().toObservable(Constants.Company_Choose, ChooseType::class.java).subscribe { s ->
            if (isViewAttached) {
                companyId = s.ids
                view.setETContent(s.content)
                resultList.clear()
                view.refreshData(resultList)
                refreshListData()
                refreshCountData()
            }
        }

        refreshListData()
        refreshCountData()
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (!isViewAttached) {
            return
        }


        try {
            when (resultCode) {
                0 -> {
                    if (!data?.data.isNullOrBlank()) {
                        val parseArray = JSON.parseArray(data?.data, ReturnedAuditEntity::class.java)
                        if (parseArray?.size == 0) {
                            if (view.getPageIndex() == 0) {
                                view.showShort(R.string.base_no_data)
                                resultList.clear()
                                view.refreshData(resultList)
                                //view.refreshChange()
                            } else {
                                view.showShort(R.string.base_no_more_data)
                            }
                        } else {
                            if (view.pull) {
                                resultList.addAll(parseArray)
                            } else {
                                resultList.clear()
                                resultList.addAll(parseArray)
                            }

                            view.refreshData(resultList)
                        }

                        view.completeRefresh()
                    }
                }

                100 -> {
                    if (!data?.data.isNullOrBlank()) {
                        val parseArray = JSON.parseArray(data?.data, ReturnedAuditCountEntity::class.java)
                        if (parseArray.size > 0) {

                            view.setNewDataTab1(getTab1ListResult(parseArray[0]))
                        }
                    }
                }

            }
        } catch (ex: Exception) {
            if (isViewAttached) {
                view.showMsg(ex.toString())
            }
        }

    }

    override fun onFailed(resultCode: Int, msg: String?) {
        if (isViewAttached) {
            view.showMsg(msg)
        }
    }


    /**
     * 获取一级菜单+数字
     */
    fun getTab0ListResult(): List<CommonItem<*>> {
        val titles = ArrayList<CommonItem<*>>()
        var commonItem: CommonItem<*>
        if (isViewAttached) {
            val tab0List = view.getTab0List()
            for (i in 0 until tab0List.size) {
                commonItem = CommonItem<Any>()
                commonItem.isClick = (schedulePos == i)
                commonItem.name = tab0List[i]
                titles.add(commonItem)
            }
        }
        return titles
    }

    /**
     * 刷新列表数据
     */
    fun refreshListData() {
        if (!isViewAttached) {
            return
        }

        val startTime = if (view.chooseTime1.isNullOrBlank()) "" else view.chooseTime1
        val endTime = if (view.chooseTime2.isNullOrBlank()) "" else view.chooseTime2

        getData(0, model.getReturnedAuditListParams(mutableListOf<Any>().apply {
            val map = HashMap<String, Any>()
            map[Constants.HTTP_PARAM_PAGE_SIZE] = pageSize
            map[Constants.HTTP_PARAM_PAGE_INDEX] = view.getPageIndex()
            map[Constants.HTTP_PARAM_COMPANY_ID] = companyId
            map[Constants.HTTP_PARAM_PAY_TYPE] = model.getPayType(schedulePos)
            map[Constants.HTTP_PARAM_STATE] = model.getState(schedulePos, auditPos)
            map[Constants.HTTP_PARAM_RETURED_RECORD_TIME_START] = startTime
            map[Constants.HTTP_PARAM_RETURED_RECORD_TIME_END] = endTime
            map[Constants.HTTP_PARAM_BOOK_TYPES] = "4,5,6,16,7,17"
            add(JSONObject.toJSONString(map))
            add(Constants.HTTP_PARAM_APP)
        }), false)


    }

    /**
     * 刷新数目数据
     */
    fun refreshCountData() {
        if (!isViewAttached) {
            return
        }

        val startTime = if (view.chooseTime1.isNullOrBlank()) "" else view.chooseTime1
        val endTime = if (view.chooseTime2.isNullOrBlank()) "" else view.chooseTime2

        getData(100, model.getBooksSiftStatsParams(mutableListOf<Any>().apply {
            val map = HashMap<String, Any>()
            map[Constants.HTTP_PARAM_RETURED_RECORD_TIME_START] = startTime
            map[Constants.HTTP_PARAM_RETURED_RECORD_TIME_END] = endTime
            map[Constants.HTTP_PARAM_COMPANY_ID] = companyId
            map[Constants.HTTP_PARAM_PAY_TYPE] = model.getPayType(schedulePos)
            map[Constants.HTTP_PARAM_BOOK_TYPES] = "4,5,6,16,7,17"
            add(JSONObject.toJSONString(map))
        }), false)
    }

    /**
     * 点击一级菜单
     */
    fun clickTab0(position: Int) {
        if (!isViewAttached) {
            return
        }

        if (position == schedulePos) {
            return
        }

        schedulePos = position
        //二级菜单重置为第一个
        auditPos = 0

        view.refreshTab0(schedulePos)
        view.setNewDataTab1(getTab1ListResult(ReturnedAuditCountEntity()))
        refreshCountData()
        clickTab1(auditPos)
    }


    /**
     * 点击二级菜单
     */
    fun clickTab1(position: Int) {
        if (!isViewAttached) {
            return
        }

        auditPos = position

        resultList.clear()
        view.refreshData(resultList)

        view.setIsPull(false)
        view.setPageIndex(0)
        view.refreshTab1(position)
        refreshListData()


    }

    /**
     * 获取二级菜单+数字
     */
    fun getTab1ListResult(entity: ReturnedAuditCountEntity): List<CommonItem<*>> {
        val titles = ArrayList<CommonItem<*>>()
        var commonItem: CommonItem<*>
        if (isViewAttached) {
            val tab1List = view.getTab1List(schedulePos)
            for (i in 0 until tab1List.size) {
                commonItem = CommonItem<Any>()
                commonItem.isClick = (auditPos == i)

                var suffix = 0

                when (schedulePos) {
                    0 -> {
                        suffix = when (i) {
                            0 -> entity.PreAuditCount
                            1 -> entity.RefuseAuditCount
                            2 -> entity.InAccountCount
                            else -> 0
                        }
                    }
                    1 -> {
                        suffix = when (i) {
                            0 -> entity.PreAuditCount
                            1 -> entity.RefuseAuditCount
                            2 -> entity.AuditCount
                            3 -> entity.ApplyCount
                            4 -> entity.InAccountCount
                            else -> 0
                        }
                    }
                    2 -> {
                        suffix = when (i) {
                            0 -> entity.PreRechargeCount
                            1 -> entity.TradeCount
                            2 -> entity.InAccountCount
                            else -> 0
                        }
                    }
                }
                commonItem.name = tab1List[i] + "(" + suffix + ")"

                titles.add(commonItem)
            }
        }
        return titles
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!isViewAttached) {
            return
        }

        if (requestCode == Constants.REQUEST_CODE_RETURED_AUDIT && resultCode == Activity.RESULT_OK) {
            view.setIsPull(false)
            view.setPageIndex(0)
            view.refreshData(0, 0)
        }
    }

    fun rxDeAttach() {
        if (companySubscription != null && !companySubscription!!.isUnsubscribed) {
            companySubscription!!.unsubscribe()
        }
    }

    /**
     *  基类刷新的时候调用的方法
     */
    fun frameRefresh(page: Int, searchType: Int) {
        if (searchType == 0) {
            companyId = ""
        } else {//重新选择时间
            resultList.clear()
            view.refreshData(resultList)
        }
        refreshListData()
        refreshCountData()
    }

}