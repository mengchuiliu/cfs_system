package com.xxjr.cfs_system.LuDan.view.viewinter

import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import entity.CommonItem

/**
 * Created by Administrator on 2018/1/16.
 */
interface AgreementVInter : BaseViewInter {
    fun initRV(commonItems: MutableList<CommonItem<Any>>)

    fun refreshItem(position: Int, text: String)

    fun scanCard()

    fun complete(isConfirm: Boolean, telphone: String, signCode: String, aisleType: String, isSend: String)
}