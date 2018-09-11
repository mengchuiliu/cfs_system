package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.BorrowInfo
import entity.CommonItem

/**
 * Created by Administrator on 2017/10/24.
 */
interface BorrowVInter : BaseViewInter {
    fun initRecycler(dataList: MutableList<CommonItem<Any>>)

    fun refreshItemData(pos: Int, content: String)

    fun getBorrow(): BorrowInfo?

    fun getIsUpdate(): Boolean

    fun completeAdd()
}