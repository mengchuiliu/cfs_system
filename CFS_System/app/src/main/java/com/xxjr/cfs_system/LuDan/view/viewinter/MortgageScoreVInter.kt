package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.MortgageScore
import entity.WageDetail

/**
 * Created by Administrator on 2018/3/7.
 */
interface MortgageScoreVInter : BaseViewInter {
    fun setMortgageId(mortgageId: String)

    fun setDateView(date: String, date1: String)

    fun refreshData()

    fun getScores(): MutableList<MortgageScore>

    fun getPull(): Boolean

    fun setPull(pull: Boolean)

    fun completeRefresh()
}