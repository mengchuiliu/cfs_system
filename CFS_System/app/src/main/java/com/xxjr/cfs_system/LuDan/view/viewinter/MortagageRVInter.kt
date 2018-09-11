package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem

/**
 * Created by Administrator on 2017/12/12.
 */
interface MortagageRVInter : BaseViewInter {
    fun initRv()

    fun refreshData(commonItems: MutableList<CommonItem<Any>>)

    fun setZoneText(zoneName: String)
}