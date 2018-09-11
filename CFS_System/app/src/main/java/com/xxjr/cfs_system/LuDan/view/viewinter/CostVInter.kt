package com.xxjr.cfs_system.LuDan.view.viewinter

import entity.Cost

/**
 * Created by Administrator on 2017/11/29.
 */
interface CostVInter : BaseLsitVInter {
    fun getCost(): MutableList<Cost>
}