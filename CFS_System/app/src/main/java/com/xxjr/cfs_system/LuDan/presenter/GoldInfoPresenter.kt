package com.xxjr.cfs_system.LuDan.presenter

import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.GoldInfoActivity
import entity.CommonItem
import entity.GoldRegisteredInfo

/**
 * Created by Administrator on 2018/3/22.
 */
class GoldInfoPresenter : BasePresenter<GoldInfoActivity, ModelImp>() {

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        view.initRV(getGoldInfos(view.getGoldUserInfo()))
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {}

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    private fun getGoldInfos(register: GoldRegisteredInfo?): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var commonItem: CommonItem<Any>
        for (i in 0..10) {
            commonItem = CommonItem()
            when (i) {
                0 -> commonItem.type = 1
                1 -> {
                    commonItem.type = 2
                    commonItem.name = register?.customerName ?: ""
                }
                2 -> {
                    commonItem.type = 2
                    commonItem.icon = R.mipmap.gold_phone
                    commonItem.name = register?.telPhone ?: ""
                    commonItem.isLineShow = true
                }
                3 -> {
                    commonItem.type = 4
                    commonItem.name = "身份证"
                    commonItem.content = "      ${register?.IDCardNo ?: ""}"
                }
                4 -> {
                    commonItem.type = 4
                    commonItem.name = "Email"
                    commonItem.content = "      ${register?.email ?: ""}"
                }
                5 -> {
                    commonItem.type = 4
                    commonItem.name = register?.bankName ?: ""
                    commonItem.content = "    ${register?.bankNo ?: ""}"
                }
                6, 7 -> commonItem.type = 0
                8 -> commonItem.type = 1
                9 -> {
                    commonItem.position = 1
                    commonItem.type = 3
                    commonItem.icon = R.mipmap.reset_psw
                    commonItem.name = "   重置支付密码"
                    if (view.getType() == 1) commonItem.isEnable = false
                }
                10 -> {
                    commonItem.position = 2
                    commonItem.type = 3
                    commonItem.icon = R.mipmap.update_phone
                    commonItem.name = "   更换手机号码"
                    if (view.getType() == 1) commonItem.isEnable = false
                }
            }
            list.add(commonItem)
        }
        return list
    }
}