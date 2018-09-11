package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.staff_training.TrainingDetailActivity
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.CommonItem
import entity.StaffInfo

class TrainingDetailPresenter : BasePresenter<TrainingDetailActivity, ModelImp>() {
    private var trainings = mutableListOf<CommonItem<Any>>()

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        getData(0, model.getMoreParam(mutableListOf<Any>().apply {
            add(view.getTrainingId())
        }, "TrainingRecord", "Get"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.returnDataSet ?: JSONObject()).isNotEmpty()) {
                        trainings.clear()
                        trainings.addAll(getItemData(data?.returnDataSet ?: JSONObject()))
                        view.refreshData(trainings)
                        getData(1, model.getMoreParam(mutableListOf<Any>().apply {
                            add("0")//页数
                            add("3")//条数
                            add(view.getTrainingId())
                        }, "EmployeeRecord", "GetList"), true)
                    }
                }
                1 -> {
                    if ((data?.returnDataSet ?: JSONObject()).isNotEmpty()) {
                        val personals = data?.returnDataSet?.getJSONArray("Table") ?: JSONArray()
                        if (personals.isNotEmpty()) {
                            val number = personals.getJSONObject(0).getIntValue("Column1")
                            trainings[11].content = "(已报名${number}人)"
                            if (number == 0) trainings[11].position = 0
                        }
                        val stringArray = data?.returnDataSet?.getString("Table1") ?: ""
                        if (stringArray.isNotBlank()) {
                            val temp = JSONArray.parseArray(stringArray, StaffInfo::class.java)
                            for (staff in temp) {
                                trainings.add(CommonItem<Any>().apply {
                                    type = 3
                                    item = staff
                                })
                            }
                            view.refreshData(trainings)
                        }
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun getItemData(jsonObject: JSONObject) = mutableListOf<CommonItem<Any>>().apply {
        val table1 = if ((jsonObject.getJSONArray("T_Training_Record") ?: JSONArray()).isNotEmpty()) {
            jsonObject.getJSONArray("T_Training_Record").getJSONObject(0)
        } else JSONObject()
        view.setApplyVisible(table1.getString("State") ?: "")
        val table2 = jsonObject.getJSONArray("T_Training_Record_Project") ?: JSONArray()
        val table3 = jsonObject.getJSONArray("T_Training_Record_Lecturer") ?: JSONArray()
        for (i in 0..11) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> {
                        type = 1
                        icon = R.mipmap.training
                        name = table1.getString("Title") ?: ""
                        when (table1.getIntValue("State")) {
                            0 -> {
                                hintContent = "报名中"
                                position = R.color.detail2
                            }
                            1 -> {
                                hintContent = "已截止"
                                position = R.color.detail1
                            }
                            2 -> {
                                hintContent = "进行中"
                                position = R.color.detail3
                            }
                            3 -> {
                                hintContent = "已结束"
                                position = R.color.font_c9
                            }
                        }
                    }
                    1 -> {
                        type = 2
                        name = "培训时间："
                        content = "${Utils.getTimeFormat(table1.getString("StartTime"))} ~ ${Utils.getTimeFormat(table1.getString("EndTime"))}"
                    }
                    2 -> {
                        type = 2
                        name = "培训类型："
                        content = table1.getString("CategoryTitle") ?: ""
                    }
                    3 -> {
                        type = 2
                        name = "培训项目："
                        val sb = StringBuilder()
                        if (table2.isNotEmpty()) {
                            list = mutableListOf<ChooseType>() as List<Any>?
                            for (j in table2.indices) {
                                val json = table2.getJSONObject(j)
                                list.add(ChooseType().apply {
                                    id = json.getIntValue("ProjectId")
                                    content = json.getString("ProjectTitle")
                                })
                                sb.append(json.getString("ProjectTitle"))
                                if (j != table2.size - 1) sb.append("\n")
                            }
                        }
                        content = sb.toString()
                    }
                    4 -> {
                        type = 2
                        name = "课题及讲师："
                        val sb = StringBuilder()
                        if (table3.isNotEmpty()) {
                            for (j in table3.indices) {
                                val json = table3.getJSONObject(j)
                                sb.append(json.getString("TopicName")
                                        ?: "").append("（${json.getString("Name")
                                        ?: ""}）")
                                if (j != table3.size - 1) sb.append(",")
                            }
                        }
                        content = sb.toString()
                    }
                    5 -> {
                        type = 2
                        name = "报名截止时间："
                        content = Utils.FormatTime(table1.getString("SignUpDeadline")
                                ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd  HH:mm:ss")
                    }
                    6 -> {
                        type = 2
                        name = "培训地点："
                        content = table1.getString("Site") ?: ""
                    }
                    7 -> {
                        type = 2
                        name = "人员限定："
                        content = table1.getString("Limitations") ?: ""
                    }
                    8 -> {
                        type = 2
                        name = "培训内容："
                        content = table1.getString("Detail") ?: ""
                    }
                    9 -> {
                        type = 0
                        isLineShow = true
                    }
                    10 -> type = 0
                    11 -> {
                        type = 1
                        icon = R.mipmap.signup
                        name = "报名信息"
                        hintContent = "更多"
                        position = R.color.font_c3
                        isClick = true
                    }
                }
            })
        }
    }
}