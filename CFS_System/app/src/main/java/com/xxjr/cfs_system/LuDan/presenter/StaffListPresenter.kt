package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.staff_training.StaffListActivity
import entity.StaffInfo

class StaffListPresenter : BasePresenter<StaffListActivity, ModelImp>() {
    private var homePage = 0

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        getStaffListData(0)
    }

    fun getStaffListData(page: Int) {
        this.homePage = page
        getData(0, model.getMoreParam(mutableListOf<Any>().apply {
            add(page)
            add("10")
            add(view.getTrainingId())
            add("")
            add("")
            add("")
            add("")
            add(view.searchContent)
        }, "EmployeeRecord", "GetList"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.returnDataSet ?: JSONObject()).isNotEmpty()) {
                        val stringArray = data?.returnDataSet?.getString("Table1") ?: ""
                        if (stringArray.isNotBlank()) {
                            val temp = JSONArray.parseArray(stringArray, StaffInfo::class.java)
                            if (temp.size == 0) {
                                if (homePage == 0) {
                                    view.showMsg("没有人员数据!")
                                    view.staffListInfo.clear()
                                    view.refreshChange()
                                } else {
                                    view.showMsg("没有更多人员数据!")
                                }
                            } else {
                                if (view.pull) {
                                    view.staffListInfo.addAll(temp)
                                } else {
                                    view.staffListInfo.clear()
                                    view.staffListInfo.addAll(temp)
                                }
                                view.refreshChange()
                            }
                            view.completeRefresh()
                        }
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

}