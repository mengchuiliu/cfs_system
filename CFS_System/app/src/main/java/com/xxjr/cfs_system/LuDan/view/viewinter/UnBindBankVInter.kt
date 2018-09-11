package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.Bank
import entity.CommonItem

/**
 * Created by Administrator on 2017/10/18.
 */
interface UnBindBankVInter : BaseViewInter {
    fun initRecycler(bankDetails: MutableList<CommonItem<Any>>)

    fun getBank(): Bank?
}