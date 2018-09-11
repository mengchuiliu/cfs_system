package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.LoanInfo

/**
 * Created by Administrator on 2018/1/9.
 */
interface RatingVInter : BaseViewInter {
    fun getLoanInfo(): LoanInfo

    fun initStar(score: Float, improvementIds: MutableList<Int>, remark: String)

    fun getRemark(): String

    fun complete()
}