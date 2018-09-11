package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.ReportChartMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.ReportChartVInter
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import rx.Subscription
import java.util.ArrayList

/**
 * Created by Administrator on 2018/1/31.
 */
class ReportChartP : BasePresenter<ReportChartVInter, ReportChartMImp>() {
    private var dateType = 1 //时间类型
    private var dataType = 0//选择类型
    private var selectType = 0//区域
    private var clickMonth = "本月"
    private var selectIds = ""//选择城市id
    val defaultZone = Hawk.get<String>("UserZone", "") ?: ""
    private val defaultCompany = Hawk.get<String>("CompanyFullName")
    private lateinit var subscription: Subscription

    override fun getModel(): ReportChartMImp = ReportChartMImp()

    override fun setDefaultValue() {
        view.initTabType(model.getTabTypes())
        subscription = RxBus.getInstance().toObservable(0, ChooseType::class.java).subscribe { s ->
            view.setDataTypeRefresh(s.id)
        }
    }

    fun initDateType(dateType: Int) {
        view.refreshDateType(model.getDateTypes(dateType))
    }

    fun getDefaultData(selectType: Int): String = when (selectType) {
        0 -> model.getZoneName(defaultZone)
        1 -> defaultCompany
        2 -> "深圳"
        3 -> "电销门店"
        else -> ""
    }

    fun getChartData(dataType: Int, chooseTime: String, selectType: Int, selectIds: String, dateType: Int) {
        this.selectIds = selectIds
        this.dataType = dataType
        this.dateType = dateType
        this.selectType = selectType
        getData(0, model.getParam(getChartList(dataType, chooseTime, selectType, selectIds, dateType), "StoreLoanStatistics"), true)
    }

    private fun getChartList(dataType: Int, chooseTime: String, selectType: Int, selectIds: String, dateType: Int): MutableList<Any> {
        if (dateType == 1) {
            clickMonth = chooseTime
            if (clickMonth == "本月") {
                clickMonth = model.getTabText(0)
            }
        }
        val list = mutableListOf<Any>()
        val map = mutableMapOf<Any, Any>()
        map.put("MethondName", "GetAppStatistics")
        map.put("Type", dataType.toString())
        map["StartDate"] = chooseTime.let {
            when (dateType) {
                1 -> "${Utils.FormatTime(clickMonth, "yyyy-M月", "yyyy-MM")}-01 00:00:00"
                2 -> "${Utils.FormatTime(it, "yyyy年", "yyyy")}-01-01 00:00:00"
                else -> ""
            }
        }
        map["EndDate"] = chooseTime.let {
            when (dateType) {
                1 -> "${Utils.FormatTime(clickMonth, "yyyy-M月", "yyyy-MM")}-${model.getDayOfMonth(clickMonth)} 23:59:59"
                2 -> "${Utils.FormatTime(it, "yyyy年", "yyyy")}-12-31 23:59:59"
                else -> ""
            }
        }
        list.add(JSON.toJSONString(map))
        list.add(selectType.toString())
        list.add(selectIds)
        list.add(dateType.toString())
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            if ((data?.returnDataSet ?: JSONObject()).isNotEmpty()) {
                view.setData(model.getChartData(dataType, dateType, selectIds, data?.returnDataSet
                        ?: JSONObject()), if ((data?.returnDataSet ?: JSONObject()).isEmpty()) ""
                else JSONObject.toJSONString(data?.returnDataSet ?: JSONObject()))
                view.refreshChartDate(model.getChartDataPro(dataType, selectType, dateType, selectIds, data?.returnDataSet
                        ?: JSONObject()))
            } else {
                view.setData(mutableListOf(), "")
                view.refreshChartDate(mutableListOf())
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun showPop(parent: View, title: TextView) {
        PopChoose.showChooseType(view as Context, parent, title, 0, getPopType())
    }

    private fun getPopType(): ArrayList<ChooseType> {
        val menus = ArrayList<ChooseType>()
        var menu: ChooseType
        for (i in 0..3) {
            menu = ChooseType()
            menu.id = i
            when (i) {
                0 -> menu.content = "签单量"
                1 -> menu.content = "批复金额"
                2 -> menu.content = "放款金额"
                3 -> menu.content = "回款量"
            }
            menus.add(menu)
        }
        return menus
    }

    fun getYearXText(value: Float): String {
        return (0..11)
                .firstOrNull { it.toFloat() == value }
                ?.let { "${it + 1}月" }
                ?: ""
    }

    fun getMonthXText(value: Float): String {
        if (clickMonth == "本月") {
            clickMonth = model.getTabText(0)
        }
        return (0 until model.getDayOfMonth(clickMonth))
                .firstOrNull { it.toFloat() == value }
                ?.let { "${it + 1}日" }
                ?: ""
    }

    fun getWeekXText(value: Float): String {
        return (0..6)
                .firstOrNull { it.toFloat() == value }
                ?.let { "${it + 1}周" }
                ?: ""
    }

    fun rxDeAttach() {
        if (!subscription.isUnsubscribed) {
            subscription.unsubscribe()
        }
    }
}