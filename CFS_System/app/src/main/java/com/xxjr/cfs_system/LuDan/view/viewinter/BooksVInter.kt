package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem
import entity.LoanInfo

interface BooksVInter : BaseViewInter {
    fun getBooksType(): Int

    fun getLoanInfo(): LoanInfo

    fun initRecycler(list: MutableList<CommonItem<Any>>)

    fun refreshItem(position: Int, text: String)

    fun refreshItem(position: Int, isEnable: Boolean)

    fun refreshItemName(position: Int, text: String)

    fun refreshAdapter()

    fun complete()
}