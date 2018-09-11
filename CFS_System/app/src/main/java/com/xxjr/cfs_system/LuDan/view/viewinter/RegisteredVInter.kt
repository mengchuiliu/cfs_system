package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem
import entity.GoldRegisteredInfo

/**
 * Created by Administrator on 2018/3/9.
 */
interface RegisteredVInter : BaseViewInter {
    fun getRegisteredType(): Int

    fun getAccountType(): Int

    fun initRV(commonItems: MutableList<CommonItem<Any>>)

    fun refreshItem(position: Int, text: String)

    fun chooseCity()

    fun scanCard()

    fun complete(register: GoldRegisteredInfo)
}