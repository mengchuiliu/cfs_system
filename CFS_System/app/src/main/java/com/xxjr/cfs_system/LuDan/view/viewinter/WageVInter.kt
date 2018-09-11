package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem
import entity.WageDetail

/**
 * Created by Administrator on 2017/11/20.
 */
interface WageVInter : BaseViewInter {
    fun initRvWageDetails()

    fun getEdRemark(): String

    fun refreshData()

    fun setMortgage(mortgage: String)

    fun setDate(date: String, date1: String)

    fun getWageDetails(): MutableList<WageDetail>

    fun getPull(): Boolean

    fun completeRefresh()
}