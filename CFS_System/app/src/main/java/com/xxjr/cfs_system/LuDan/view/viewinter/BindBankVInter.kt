package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.Bank
import entity.CommonItem

interface BindBankVInter : BaseViewInter {
    fun initRecycler(bankCards: MutableList<Bank>?)
}