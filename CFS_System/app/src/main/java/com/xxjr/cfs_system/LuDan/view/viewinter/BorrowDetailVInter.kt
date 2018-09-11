package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.BorrowInfo
import entity.CommonItem

/**
 * Created by Administrator on 2017/10/31.
 */
interface BorrowDetailVInter : BaseViewInter {
    fun initRecycler(mutableList: MutableList<CommonItem<Any>>, borrowInfo: BorrowInfo?)

    fun getBorrowId(): Int

    fun getRemark(): String

    fun refreshItem(pos: Int)

    fun complete()
}