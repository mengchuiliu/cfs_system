package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem

/**
 * Created by Administrator on 2018/3/20.
 */
interface AddVisitVInter : BaseViewInter {
    fun initRV(commonItems: MutableList<CommonItem<Any>>)

    fun refreshItem(position: Int, text: String)

    fun complete()
}