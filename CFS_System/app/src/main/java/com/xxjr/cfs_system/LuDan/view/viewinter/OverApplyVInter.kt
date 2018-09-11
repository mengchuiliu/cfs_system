package com.xxjr.cfs_system.LuDan.view.viewinter

import android.view.View
import android.widget.EditText
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem
import entity.LoanInfo

interface OverApplyVInter : BaseViewInter {
    fun getLoanInfo(): LoanInfo

    fun getPermits(): List<String>

    fun showBackStep(isShow: Boolean)

    fun isCase(isCase: Boolean)

    fun showButton(isShow: Boolean, notText: String, okText: String)

    fun showSoftInput(editText: EditText)//显示软键盘

    fun hideSoftInput(v: View)

    fun initRecycler(list: MutableList<CommonItem<Any>>)

    fun refreshItem(position: Int, text: String)

    fun refreshAdapter(list: MutableList<CommonItem<Any>>)

    fun getEdRemark(): String

    fun getOverNote(): String

    fun complete()
}