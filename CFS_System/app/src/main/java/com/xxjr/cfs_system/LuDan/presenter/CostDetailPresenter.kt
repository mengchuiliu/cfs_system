package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.CostDetailMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.CostDetailsVInter
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.LoanInfo
import rx.Subscription
import timeselector.TimeSelector
import kotlin.collections.ArrayList

class CostDetailPresenter : BasePresenter<CostDetailsVInter, CostDetailMImp>() {
    private var popWindow: PopupWindow? = null
    private var timeSelector: TimeSelector? = null
    private var subscription: Subscription? = null
    var costType: Int = -1
    var happenDate: String = ""

    override fun getModel(): CostDetailMImp? = CostDetailMImp()

    override fun setDefaultValue() {
        view.initRecycler(model.getItemData(view.getLoanInfo(), view.getIsAdd(), ArrayList<Any>()))
        getCostListData()
        if (isViewAttached) {
            subscription = RxBus.getInstance().toObservable(1, ChooseType::class.java).subscribe { s ->
                if (popWindow != null && popWindow?.isShowing!!) {
                    popWindow?.dismiss()
                    view.refreshItem(s.content, 12)
                    costType = s.id
                    when (s.id) {
                        3 -> {
                            view.refreshItem("请备注收款账号和评估总值", true)
                        }
                        6, 15, 24 -> {
                            view.refreshItem("请填写成本备注", true)
                        }
                        else -> {
                            view.refreshItem("请填写成本备注", false)
                        }
                    }
                }
            }

            timeSelector = TimeSelector(view as Context, TimeSelector.ResultHandler { time ->
                view.refreshItem(time, 13)
                happenDate = time
            }, "1900-01-01", DateUtil.getCurDate())
            timeSelector?.setScrollUnit(TimeSelector.SCROLLTYPE.YEAR, TimeSelector.SCROLLTYPE.MONTH, TimeSelector.SCROLLTYPE.DAY)
        }
    }

    fun refreshRecyclerData() {
        view.refreshData(model.getItemData(view.getLoanInfo(), view.getIsAdd(), ArrayList<Any>()))
    }

    fun getCostListData() {
        getData(1, model.getParam(getCostParamList(view.getLoanInfo()), "GetLoanCostList"), true)
    }

    //录入成本
    fun saveCost(amount: Double, remark: String) {
        if (costType == -1) {
            view.showMsg("请选择成本类型!")
            return
        } else if (costType == 3 || costType == 6 || costType == 15) {
            if (remark.isBlank()) {
                view.showMsg("请填写成本备注!")
                return
            }
        }
        if (happenDate.isBlank()) {
            view.showMsg("请选择发生日期!")
            return
        }
        if (amount == 0.0) {
            view.showMsg("请填写成本金额!")
            return
        }
        getData(0, model.getSaveParam(getSaveParamList(view.getLoanInfo(), amount, remark)), true)
    }

    fun delCost(delId: Int) {
        getData(4, model.getParam(getDelParamList(delId), "DeleteLoanCost"), true)
    }

    private fun getSaveParamList(loanInfo: LoanInfo, amount: Double, remark: String): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        val map1 = HashMap<String, Any>()
        map1.put("ID", "")
        map1.put("LoanCode", loanInfo.loanCode)
        map1.put("ContractCode", loanInfo.pactCode)
        map1.put("CostType", costType)
        map1.put("Cost", amount)
        map1.put("ReMark", remark)
        map1.put("ServiceID", Hawk.get<Any>("UserID"))
        map1.put("ClerkID", loanInfo.clerkID)
        map1.put("InsertTime", "")
        map1.put("UpdateTime", "")
        map1.put("DelMarker", "")
//        map1.put("DelMark", "")
        map1.put("LoanID", loanInfo.loanId)
        map1.put("ContractID", loanInfo.contractId)
        map1.put("CompanyID", loanInfo.companyID)
        map1.put("HappenDate", happenDate)
        val s = JSON.toJSONString(map1)
        val list1: MutableList<Any> = ArrayList()
        list1.add("")
        list1.add(loanInfo.getLoanId())
        list1.add(amount)
        val s1 = JSON.toJSONString(list1)
        list.add("Mobile")
        list.add(s)
        list.add(s1)
        return list
    }

    private fun getCostParamList(loanInfo: LoanInfo): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        list.add(" AND LoanId = " + loanInfo.getLoanId() + " and DelMarker =0")
        list.add("0")
        list.add("1000")
        list.add("1")
        return list
    }

    private fun getDelParamList(delId: Int): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        list.add(delId)
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData) {
        if (isViewAttached) {
            if (resultCode != 1) RxBus.getInstance().post(Constants.POST_REFRESH_MY_TASK, true)
            when (resultCode) {
                0 -> {
                    costType = -1
                    happenDate = ""
                    view.showMsg("成本录入成功!")
                    view.complete()
                }
                1 -> {
                    if (!data.returnStrings.isNullOrBlank()) {
                        val array = JSON.parseArray(data.returnStrings)
                        if (array.size == 3) {
                            view.getLoanInfo().noAuditCostMoney = array.getDouble(0)
                            view.getLoanInfo().passAuditCostMoney = array.getDouble(1)
                        }
                    }
                    if (!data.data.isNullOrBlank()) {
                        view.refreshData(model.getItemData(view.getLoanInfo(), view.getIsAdd(), model.getCostData(data.data)))
                    }
                }
                4 -> {
                    view.showMsg("删除成功!")
                    getCostListData()
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String) {
        view.showMsg(msg)
    }

    fun showCostType(parent: View) {
        if (isViewAttached) {
            if (popWindow == null)
                popWindow = PopChoose.showChooseType(view as Context, parent, "成本类型",
                        Utils.getTypeDataList("LoanCostType"), 1, false)
            else popWindow?.showAtLocation(parent, Gravity.CENTER, 0, 0)
        }
    }

    fun showTimeChoose() {
        timeSelector?.show()
    }

    fun rxDeAttach() {
        if (subscription != null && !subscription?.isUnsubscribed!!) {
            if (popWindow != null) {
                if (popWindow?.isShowing!!) {
                    popWindow?.dismiss()
                }
                popWindow = null
            }
            subscription?.unsubscribe()
        }
    }
}
