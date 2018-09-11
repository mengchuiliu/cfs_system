package com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.Presenter

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.View.TrainingNotifyActivity
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.TrainingList

class TrainingNotifyPresenter : BasePresenter<TrainingNotifyActivity, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()
    private var position = 0

    override fun setDefaultValue() {
        getNotifyData()
    }

    //获取消息通知列表
    fun getNotifyData() {
        getData(0, model.getParam(mutableListOf<Any>().apply {
            add(JSON.toJSONString(hashMapOf<Any, Any>().apply {
                put("Receiver", Hawk.get<String>("UserID"))
                put("CategoryId", view.getCategoryId())
            }))
            add(view.page)
            add("10")
        }, "GetUserNotificationList"), true)
    }

    //已读消息
    fun readMessage(type: Int) {
        getData(1, model.getParam(mutableListOf<Any>().apply {
            add(type)
            add(Hawk.get<String>("UserID"))
        }, "ReadNotification"), false)
    }

    //已读消息反馈
    fun readFeedMessage(type: Int, position: Int) {
        this.position = position
        getData(2, model.getParam(mutableListOf<Any>().apply {
            add(type)
            add(Hawk.get<String>("UserID"))
        }, "SureReadNotification"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    val temp = getItemData(data?.data ?: "")
                    if (temp.size == 0) {
                        if (view.page == 0) {
                            view.messages.clear()
                            view.refreshData()
                        } else {
                            view.showMsg("没有更多数据!")
                        }
                    } else {
                        if (view.pull) {
                            view.messages.addAll(temp)
                        } else {
                            view.messages.clear()
                            view.messages.addAll(temp)
                        }
                        view.refreshData()
                    }
                    view.completeRefresh()
                }
                1 -> {
                }//消息已读
                2 -> view.refreshItem(position)//消息已读反馈
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun getItemData(data: String) = mutableListOf<CommonItem<Any>>().apply {
        val jsonArray = JSONArray.parseArray(data)
        if (jsonArray.isNotEmpty()) {
            view.setTitleText(jsonArray.getJSONObject(0).getString("CategoryName") ?: "")
            for (i in jsonArray.indices) {
                val jsonObject = jsonArray.getJSONObject(i)
                add(CommonItem<Any>().apply {
                    type = jsonObject.getIntValue("Id")//通知编号
                    hintContent = "新的${jsonObject.getString("CategoryName") ?: ""}通知"
                    name = jsonObject.getString("Title") ?: ""
                    content = jsonObject.getString("Summary") ?: ""
                    date = Utils.getTime(jsonObject.getString("SendTime"))
                    isLineShow = jsonObject.getBooleanValue("IsReadFb")//是否需要反馈
                    isEnable = jsonObject.getBooleanValue("IsReadConfirm")//0-未反馈，1-已反馈
                    isClick = jsonObject.getBooleanValue("IsRead")//0-未读，1-已读
                    remark = jsonObject.getString("FuncCode") ?: ""//消息推送编号
                    payType = jsonObject.getString("FuncId") ?: ""//业务id
                })
            }
        }
    }
}