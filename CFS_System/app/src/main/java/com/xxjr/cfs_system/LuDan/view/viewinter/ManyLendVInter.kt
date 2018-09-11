package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem
import entity.LoanInfo

/**
 * Created by Administrator on 2017/11/17.
 */
interface ManyLendVInter : BaseViewInter {
    fun getLoanInfo(): LoanInfo

    fun initRecycler(list: MutableList<CommonItem<Any>>)

    fun refreshItem(position: Int, text: String)

    fun complete()
}