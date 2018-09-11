package com.xxjr.cfs_system.LuDan.view.viewinter

import android.view.View
import android.widget.EditText
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem
import entity.LoanInfo

interface CostDetailsVInter : BaseViewInter {
    fun getLoanInfo(): LoanInfo

    fun getIsAdd(): Boolean

    fun initRecycler(commonItems: MutableList<CommonItem<*>>)

    fun refreshItem(content: String, pos: Int)

    fun refreshItem(content: String, ishow: Boolean)

    fun refreshData(commonItems: MutableList<CommonItem<*>>)

    fun complete()
}