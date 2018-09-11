package com.xxjr.cfs_system.LuDan.view.viewinter

import android.content.Context
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.LoanInfo


interface LoanOverVIntrer : BaseViewInter {
    fun getFrgContext(): Context

    fun getType(): Int

    fun initRecycler()
    
    fun setIsFirst(isFirst: Boolean)

    fun getPage(): Int

    fun getLoanInfos(): MutableList<LoanInfo>

    fun getPull(): Boolean

    fun refreshChange()

    fun completeRefresh()
}