package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem

/**
 * Created by Administrator on 2017/11/24.
 */
interface PostVInter : BaseViewInter {
    fun initRV(commonItems: MutableList<CommonItem<Any>>)

    fun getCustomer(): String

    fun getCustomerId(): String

    fun getContractId(): Int

    fun getCompanyId(): String

    fun refreshItem(position: Int, text: String)

    fun refreshGvItem()

    fun showIconPop()

    fun postSucceed()

    fun setPostProgress(progress: Int)

    fun hideBar()
}