package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem

/**
 * Created by Administrator on 2017/12/13.
 */
interface VisitDetailVInter : BaseViewInter {
    fun initStoreRV(commonItems: MutableList<CommonItem<Any>>)

    fun initClerkRV(commonItems: MutableList<CommonItem<Any>>)

    fun getCompanyId(): String

    fun getCurYear(): Int

    fun getCurMonth(): Int
}