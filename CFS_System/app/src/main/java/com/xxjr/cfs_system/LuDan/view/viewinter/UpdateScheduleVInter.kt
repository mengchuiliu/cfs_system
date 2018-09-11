package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem
import entity.LoanInfo

interface UpdateScheduleVInter : BaseViewInter {
    fun getLoanInfo(): LoanInfo

    fun initRecycler(list: MutableList<CommonItem<Any>>)

    fun refreshItem(position: Int, text: String)

    fun refreshItem(position: Int, show: Boolean)

    fun refreshAdapter(list: MutableList<CommonItem<Any>>)

    fun getRemark(): String

    fun getNewLend(): Boolean

    fun complete()
}