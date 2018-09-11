package com.xxjr.cfs_system.LuDan.view.viewinter

import android.content.Context
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem

interface RankVInter : BaseViewInter {
    fun getType(): Int

    fun getTimeType(): Int

    fun getRankType(): Int

    fun getCompanyId(): String
    
    fun getCompanyType(): String

    fun initRecycler(mutableList: MutableList<CommonItem<*>>, doubles: MutableList<Double>, maxN: Int)

    fun getFrgContext(): Context
}