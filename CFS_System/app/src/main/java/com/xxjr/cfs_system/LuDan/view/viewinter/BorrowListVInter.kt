package com.xxjr.cfs_system.LuDan.view.viewinter

import android.content.Context
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.BorrowInfo

/**
 * Created by Administrator on 2017/10/27.
 */
interface BorrowListVInter : BaseViewInter {
    fun getBorrowInfos(): MutableList<BorrowInfo>

    fun getType(): Int

    fun getFrgContext(): Context

    fun initRecycler()

    fun getPull(): Boolean

    fun getIsFirst(): Boolean

    fun setIsFirst(isFirst: Boolean)

    fun refreshChange()

    fun completeRefresh()

    fun delSucceed()
}