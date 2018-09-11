package com.xxjr.cfs_system.LuDan.view.viewinter

import android.content.Context
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.SetMenu

/**
 * Created by Administrator on 2017/11/8.
 */
interface PageMenuVInter : BaseViewInter {
    fun getPageContext(): Context

    fun initRecycler(menus: MutableList<SetMenu>)

    fun setMortgageScore(score: String)

    fun loginOut()
}