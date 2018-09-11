package com.xxjr.cfs_system.LuDan.view.viewinter

import android.content.Context
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem

/**
 * Created by Administrator on 2017/12/12.
 */
interface VisitFVInter : BaseViewInter {
    fun getFrgContext(): Context

    fun initRv(commonItems: MutableList<CommonItem<Any>>)
}