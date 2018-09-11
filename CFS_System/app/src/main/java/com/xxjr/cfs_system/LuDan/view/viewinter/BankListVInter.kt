package com.xxjr.cfs_system.LuDan.view.viewinter

import entity.BankManager

/**
 * Created by Administrator on 2017/12/1.
 */
interface BankListVInter : BaseLsitVInter {
    fun getManagers(): MutableList<BankManager>
}