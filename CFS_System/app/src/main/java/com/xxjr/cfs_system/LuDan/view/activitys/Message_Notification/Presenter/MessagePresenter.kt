package com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.Presenter

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.View.MessageActivity
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem

class MessagePresenter : BasePresenter<MessageActivity, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        getData(0, model.getMoreParam(mutableListOf<Any>().apply {
            add(JSON.toJSONString(hashMapOf<Any, Any>().apply {
                put("Receiver", Hawk.get<String>("UserID"))
            }))
        }, "GetNotificationInfoList", "App"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.data ?: "").isNotBlank()) {
                        val jsonArray: JSONArray = JSON.parseArray(data?.data ?: "")
                        if (jsonArray.isNotEmpty()) {
                            view.refreshData(getItemData(jsonArray))
                        } else view.showMsg("空消息")
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun getItemData(jsonArray: JSONArray) = mutableListOf<CommonItem<Any>>().apply {
        for (i in jsonArray.indices) {
            val jsonObject = jsonArray.getJSONObject(i)
            add(CommonItem<Any>().apply {
                type = jsonObject.getIntValue("CategoryId")
                when (type) {
                    3 -> icon = R.mipmap.message_meeting //会议
                    5 -> icon = R.mipmap.message_training//培训
                }
                name = jsonObject.getString("CategoryName") ?: ""
                content = jsonObject.getString("Title") ?: ""
                date = getFormDate(jsonObject.getString("LastSendTime"))
                position = jsonObject.getIntValue("NotReadCount")
            })
        }
    }

    private fun getFormDate(time: String): String {
        val curDate = Utils.getTime(time)
        return when (curDate) {
            DateUtil.getCurDate() -> "今天"
            DateUtil.getOldDate(-1, DateUtil.getCurDate()) -> "昨天"
            else -> curDate
        }
    }
}