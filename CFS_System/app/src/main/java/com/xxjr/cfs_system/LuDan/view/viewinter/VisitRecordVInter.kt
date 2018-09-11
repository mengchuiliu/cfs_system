package com.xxjr.cfs_system.LuDan.view.viewinter

import entity.VisitRecord

/**
 * Created by Administrator on 2017/12/7.
 */
interface VisitRecordVInter : BaseLsitVInter {
    fun getVisitRecords(): MutableList<VisitRecord>
}