package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.staff_training.TrainingListActivity
import entity.CommonItem
import entity.TrainingList

class TrainingListPresenter : BasePresenter<TrainingListActivity, ModelImp>() {
    private var homePage = 0
    private var trainingState = 0

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
    }

    fun getTrainingListData(page: Int, pos: Int) {
        homePage = page
        trainingState = pos
        getData(0, model.getMoreParam(mutableListOf<Any>().apply {
            add(homePage)
            add("10")
            add(view.searchContent)
            if (view.chooseTime1.isNullOrBlank() && view.chooseTime2.isNullOrBlank()) {
                add("")
                add("")
            } else {
                add(view.chooseTime1)
                add(view.chooseTime2)
            }
            add("")
            add(trainingState != 0)//true->已结束  false->未结束
        }, "TrainingRecord", "GetList"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.data ?: "").isNotBlank()) {
                        val temp = JSON.parseArray(data?.data ?: "", TrainingList::class.java)
                        if (temp.size == 0) {
                            if (homePage == 0) {
                                view.showMsg("还没有培训数据!")
                                view.trainingListInfo.clear()
                                view.refreshChange()
                            } else {
                                view.showMsg("没有更多培训数据!")
                            }
                        } else {
                            if (view.pull) {
                                view.trainingListInfo.addAll(temp)
                            } else {
                                view.trainingListInfo.clear()
                                view.trainingListInfo.addAll(temp)
                            }
                            view.refreshChange()
                        }
                        view.completeRefresh()
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun getTrainTitles() = mutableListOf<CommonItem<Any>>().apply {
        for (i in 0..1) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> {
                        name = "未结束"
                        isClick = true
                    }
                    1 -> name = "已结束"
                }
            })
        }
    }

}