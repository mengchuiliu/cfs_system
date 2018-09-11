package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.staff_training.SignUpDetailActivity
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.StaffInfo

class SignUpDetailPresenter : BasePresenter<SignUpDetailActivity, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
    }

    //保存修改培训信息
    fun save() {
        getData(0, model.getMoreParam(mutableListOf<Any>().apply {
            add(JSON.toJSONString(mutableListOf<StaffInfo>().apply {
                add(view.staffInfo ?: StaffInfo())
            }))
        }, "EmployeeRecord", "Edit"), true)
    }

    //删除培训信息
    fun delete() {
        getData(0, model.getMoreParam(mutableListOf<Any>().apply {
            add(view.staffInfo?.ID ?: -1)
        }, "EmployeeRecord", "Delete"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> view.complete(false)
                1 -> view.complete(true)
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun getItemData() = mutableListOf<CommonItem<Any>>().apply {
        add(CommonItem<Any>().apply {
            type = 1
            name = "培训项目"
        })
        for (choose in view.projects) {
            add(CommonItem<Any>().apply {
                type = 2
                content = choose.content
                position = choose.id
                for (id in view.projectIds) {
                    if (id.isNotBlank()) {
                        if (choose.id == id.toInt()) {
                            isLineShow = true
                            break
                        }
                    }
                }
            })
        }
        for (i in 0..3) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> {
                        type = 0
                        isLineShow = true
                    }
                    1 -> type = 0
                    2 -> {
                        type = 1
                        name = "是否通过"
                    }
                    3 -> {
                        type = 3
                        position = view.staffInfo?.IsPass ?: -3
                    }
                }
            })
        }
        for (i in 0..3) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> type = 0
                    1 -> {
                        type = 2
                        content = "不适宜剧烈运动"
                        isLineShow = view.staffInfo?.IsGreatOperation ?: false
                        position = -1
                    }
                    2 -> {
                        type = 2
                        content = "需要培训前一天入住"
                        isLineShow = view.staffInfo?.IsNeedCheckInBefore ?: false
                        position = -2
                    }
                    3 -> {
                        type = 2
                        content = "需要统一乘车"
                        isLineShow = view.staffInfo?.IsNeedUnifiedRiding ?: false
                        position = -3
                    }
                }
            })
        }

        if (view.honors.isEmpty()) {
            add(CommonItem<Any>().apply {
                type = 5
                position = 0
                name = "获得荣誉"
                content = ""
                isClick = true
            })
        } else {
            for (i in view.honors.indices) {
                add(CommonItem<Any>().apply {
                    type = 5
                    position = i
                    name = "获得荣誉"
                    content = view.honors[i]
                    isClick = (i == view.honors.size - 1)
                })
            }
        }
        add(CommonItem<Any>().apply {
            type = 4
            name = "人员成绩"
            content = view.staffInfo?.Achievement ?: ""
        })
        add(CommonItem<Any>().apply {
            type = 4
            isEnable = CFSUtils.isAudit(Hawk.get("UserID"), "TrainingCostEditPermit")
            name = "培训费用" + if (isEnable) "" else "(不可编辑)"
            content = view.staffInfo?.Cost ?: ""

        })
    }
}