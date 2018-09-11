package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem
import entity.Cost

interface AuditCostVInter : BaseViewInter {
    fun getLoanInfo(): Cost

    fun initRecycler(commonItems: MutableList<CommonItem<*>>)

    fun getRemark(): String

    fun complete()
}