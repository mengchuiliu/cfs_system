package com.xxjr.cfs_system.LuDan.view.viewinter

import com.github.mikephil.charting.data.BarEntry
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem

interface ReportVInter : BaseViewInter {
    fun initPagerView(titles: MutableList<String>)

    fun refreshBarData(datas: MutableList<CommonItem<BarEntry>>, jsonData: String)
}