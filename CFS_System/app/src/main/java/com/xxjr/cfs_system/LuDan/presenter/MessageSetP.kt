package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.MessageSetActivity
import entity.CommonItem
import java.util.HashMap

class MessageSetP : BasePresenter<MessageSetActivity, ModelImp>() {
    var pos = -1

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        isRemindService("", "GET", 1, false)
    }

    //获取和更新推送通知
    fun isRemindService(json: String, action: String, code: Int, show: Boolean) {
        getData(code, getRemindParam(json, action, "JPushFunc"), show)
    }

    private fun getRemindParam(json: String, action: String, tranName: String): String {
        val map = HashMap<String, Any>()
        map["UserId"] = Hawk.get<String>("UserID")
        map["Action"] = action
        map["DBMarker"] = "DB_CFS"
        map["TranName"] = tranName
        map["Json"] = json
        Logger.e("==推送==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        when (resultCode) {
            1 -> {//获取推送
                if ((data?.returnString ?: "").isNotBlank()) {
                    val strings = (data?.returnString
                            ?: "").split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (strings.isNotEmpty()) {
                        for (i in view.commonItems.indices) {
                            view.commonItems[i].isEnable = false
                            for (item in strings) {//返回的消息标记
                                if (view.commonItems[i].remark == item) {
                                    view.commonItems[i].isEnable = true
                                    break
                                }
                            }
                        }
                    }
                }
                view.refreshData(view.commonItems)
            }
            2 -> {//设置推送
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) {
        view.showMsg(msg)
        when (resultCode) {
            1 -> view.refreshData(view.commonItems)
            2 -> if (pos != -1) view.refreshItem(pos, !view.commonItems[pos].isEnable)
        }
    }

    fun getItemData(menus: Array<String>) = mutableListOf<CommonItem<Any>>().apply {
        if (menus.contains("818")) {
            add(CommonItem<Any>().apply {
                name = "还款提醒"
                remark = "J03"
            })
        }
        if (menus.contains("821")) {
            add(CommonItem<Any>().apply {
                name = "提成提醒"
                remark = "J05"
            })
        }
    }

    fun getMenus(permissions: String): Array<String> {
        val strings = permissions.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (strings.size > 1) {
            val str = strings[1].split("M".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (str.isNotEmpty()) {
                for (s in str) {
                    if (s.startsWith("80")) {
                        val str1 = s.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (str1.isNotEmpty()) {
                            val s1 = str1[1]
                            return s1.split("F".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        }
                    }
                }
            }
        }
        return arrayOf()
    }
}