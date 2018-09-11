package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter

interface FliterVInter : BaseViewInter {

    fun refreshTitle(titles: MutableList<String>)

    fun customerClick()

    fun loanCodeClick()

    fun companyClick()

    fun setCompanyName(id: String, name: String)

    fun timeClick(time1: String, time2: String)

    fun getContractType(): Int
}