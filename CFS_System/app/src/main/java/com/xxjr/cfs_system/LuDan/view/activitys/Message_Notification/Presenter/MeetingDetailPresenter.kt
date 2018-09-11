package com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.Presenter

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.View.MeetingDetailActivity
import entity.PactData

class MeetingDetailPresenter : BasePresenter<MeetingDetailActivity, ModelImp>() {
    private var type = -1 //通知编号

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        getData(0, model.getParam(mutableListOf<Any>().apply {
            add(view.getNotifyId())
        }, "GetNotificationDetail"), true)
    }

    //已读消息反馈
    fun readFeedMessage() {
        getData(1, model.getParam(mutableListOf<Any>().apply {
            add(type)
            add(Hawk.get<String>("UserID"))
        }, "SureReadNotification"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.returnDataSet ?: JSONObject()).isNotEmpty()) {
                        val jsonArray = data?.returnDataSet?.getJSONArray("T_Notification")
                                ?: JSONArray()
                        if (jsonArray.isNotEmpty()) {
                            val jsonObject = jsonArray.getJSONObject(0)
                            type = jsonObject.getIntValue("Id")
                            view.setTitle(jsonObject.getString("Title"))
                            view.setMessageContent(jsonObject.getString("Content"))
                        }
                        val fileJsonArray = data?.returnDataSet?.getJSONArray("T_Notification_Attachment")
                                ?: JSONArray()
                        view.refreshFiles(getFliesData(fileJsonArray))
                    }
                }
                1 -> view.setConfirmEnable()//已读消息反馈
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    private fun getFliesData(jsonArray: JSONArray) = mutableListOf<PactData>().apply {
        if (jsonArray.isNotEmpty()) {
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                add(PactData().apply {
                    fileName = "${jsonObject.getString("FileName")
                            ?: ""}${jsonObject.getString("FileType")}"
                    fileGuid = jsonObject.getString("FileGUID") ?: ""
                    fileSize = jsonObject.getString("FileSize") ?: ""
                })
            }
        }
    }
}