package com.xxjr.cfs_system.LuDan.view.viewinter

import android.content.Context
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.Cost
import entity.LoanInfo

interface CostListVInter : BaseViewInter {
    fun getCost(): MutableList<Cost>

    fun getType(): Int

    fun getFrgContext(): Context

    fun initRecycler()

    fun getPull(): Boolean

    fun getIsFirst(): Boolean

    fun setIsFirst(isFirst: Boolean)

    fun refreshChange()

    fun completeRefresh()
}