package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem

/**
 * Created by Administrator on 2017/11/16.
 */
interface RankDetailsVInter : BaseViewInter {
    fun initRV(data: MutableList<CommonItem<Any>>, details: MutableList<CommonItem<Any>>)

    fun getCommonItem(): CommonItem<Any>?
}