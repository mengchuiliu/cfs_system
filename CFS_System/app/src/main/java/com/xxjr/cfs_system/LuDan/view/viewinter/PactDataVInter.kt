package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.PactData

interface PactDataVInter : BaseViewInter {
    fun initRVPact()

    fun getPull(): Boolean

    fun setPull(isPull: Boolean)

    fun getPactDatas(): MutableList<PactData>

    fun getContractId(): Int

    fun completeRefresh()

    fun refreshData()

    fun removeItem(position: Int)

    fun refreshItem(position: Int)
}