package com.xxjr.cfs_system.LuDan.view.viewinter

import android.content.Context
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.ReportCompare

interface QuantityVInter : BaseViewInter {
    fun getType(): Int

    fun refreshData(mutableList: MutableList<ReportCompare>, doubles: MutableList<Double>)

    fun getFrgContext(): Context
}