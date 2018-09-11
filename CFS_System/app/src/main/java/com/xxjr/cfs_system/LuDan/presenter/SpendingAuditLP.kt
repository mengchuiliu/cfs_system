package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.orhanobut.logger.Logger
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.model.modelimp.SpendingMImp
import com.xxjr.cfs_system.LuDan.view.activitys.spending_audit.SpendingAuditListActivity
import com.xxjr.cfs_system.tools.Constants
import entity.ChooseType
import entity.CommonItem
import entity.SpendingInfo
import rx.Subscription
import java.util.*

class SpendingAuditLP : BasePresenter<SpendingAuditListActivity, SpendingMImp>() {
    private var homePage = 0

    override fun getModel(): SpendingMImp = SpendingMImp()

    override fun setDefaultValue() {
        refreshSpendingData(homePage, view.auditPos)
    }

    fun refreshSpendingData(page: Int, auditPos: Int) {
        if (isViewAttached) {
            homePage = page
            getData(0, model.getSpendParam(mutableListOf<Any>().apply {
                add("1")
                add(homePage)
                add("10")
                add(view.searchCompany)
                if (view.chooseTime1.isNullOrBlank() && view.chooseTime2.isNullOrBlank()) {
                    add("")
                    add("")
                } else {
                    add(view.chooseTime1)
                    add(view.chooseTime2)
                }
                add(model.getAuditState(auditPos))
            }, "ConstantPayout"), true)
        }
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.returnDataSet ?: JSONObject()).isNotEmpty()) {
                        if ((data?.returnDataSet?.getString("Table1") ?: "").isNotBlank()) {
                            val temp = JSON.parseArray(data?.returnDataSet?.getString("Table1"), SpendingInfo::class.java)
                            if (temp.size == 0) {
                                if (homePage == 0) {
                                    view.showMsg("还没有支出数据!")
                                    view.spendingInfo.clear()
                                    view.refreshChange()
                                } else {
                                    view.showMsg("没有更多支出!")
                                }
                            } else {
                                if (view.pull) {
                                    view.spendingInfo.addAll(temp)
                                } else {
                                    view.spendingInfo.clear()
                                    view.spendingInfo.addAll(temp)
                                }
                                view.refreshChange()
                            }
                            view.completeRefresh()
                        }
                        if ((data?.returnDataSet?.getJSONArray("Table")
                                        ?: JSONArray()).isNotEmpty()) {
                            view.refreshTitleData0(getTitles(data?.returnDataSet?.getJSONArray("Table")!!.getJSONObject(0), view.auditPos))
                        }
                    } else {
                        view.showMsg("支出数据获取失败!")
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun getTitles(jsonObject: JSONObject, auditPos: Int): List<CommonItem<*>> {
        val titles = ArrayList<CommonItem<*>>()
        var commonItem: CommonItem<*>
        for (i in 0..5) {
            commonItem = CommonItem<Any>()
            commonItem.isClick = (auditPos == i)
            when (i) {
                0 -> commonItem.setName("全部" + if (jsonObject.isEmpty()) "" else "(${jsonObject.getIntValue("total")})")
                1 -> commonItem.setName("待区域主管审核" + if (jsonObject.isEmpty()) "" else "(${jsonObject.getIntValue("state1")})")
                2 -> commonItem.setName("待总部财务审核" + if (jsonObject.isEmpty()) "" else "(${jsonObject.getIntValue("state2")})")
                3 -> commonItem.setName("待付款" + if (jsonObject.isEmpty()) "" else "(${jsonObject.getIntValue("state_1")})")
                4 -> commonItem.setName("已付款" + if (jsonObject.isEmpty()) "" else "(${jsonObject.getIntValue("state_3")})")
                5 -> commonItem.setName("已拒绝" + if (jsonObject.isEmpty()) "" else "(${jsonObject.getIntValue("state_2")})")
            }
            titles.add(commonItem)
        }
        return titles
    }
}