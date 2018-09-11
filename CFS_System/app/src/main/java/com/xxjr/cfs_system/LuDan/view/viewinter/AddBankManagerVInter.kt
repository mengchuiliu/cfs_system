package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.BankManager
import entity.CommonItem

/**
 * Created by Administrator on 2017/12/1.
 */
interface AddBankManagerVInter : BaseViewInter {
    fun getManager(): BankManager?

    fun initRv(commonItems: MutableList<CommonItem<Any>>)

    fun refreshItem(pos: Int, text: String)

    fun complete()
}