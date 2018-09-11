package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem

interface AddBankVInter : BaseViewInter {
    fun initRecycler(mutableList: MutableList<CommonItem<Any>>)

    fun addComplete()

    fun scanCard()
}