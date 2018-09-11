package com.xxjr.cfs_system.LuDan.view.viewinter

import com.flyco.tablayout.listener.CustomTabEntity
import com.github.mikephil.charting.data.Entry
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem

/**
 * Created by Administrator on 2018/1/31.
 */
interface ReportChartVInter : BaseViewInter {
    fun initTabType(tabTypes: MutableList<CustomTabEntity>)

    fun refreshDateType(dateTypes: MutableList<CommonItem<*>>)

    fun refreshChartDate(dataTypes: MutableList<CommonItem<*>>)

    fun setData(datas: MutableList<CommonItem<Entry>>,jsonData: String)

    fun setDataTypeRefresh(dataType: Int)
}