package com.xxjr.cfs_system.LuDan.view.activitys.borrow.presenter

import android.content.Context
import android.view.View
import android.widget.PopupWindow
import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.BorrowMImp
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.BorrowVInter
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import entity.BorrowInfo
import entity.ChooseType
import rx.Subscription
import timeselector.TimeSelector

class BorrowPresenter : BasePresenter<BorrowVInter, BorrowMImp>() {
    private var borrowInfo: BorrowInfo? = null
    private var popWindow: PopupWindow? = null
    private var borrowSubscription: Subscription? = null
    private var returnSubscription: Subscription? = null
    private var timeSelector: TimeSelector? = null
    private var timePos = -1

    override fun getModel(): BorrowMImp = BorrowMImp()

    override fun setDefaultValue() {
        borrowInfo = view.getBorrow() ?: BorrowInfo()
        initTimeSelector()
        initRX()
        view.initRecycler(model.getRVData(borrowInfo!!, view.getIsUpdate()))
    }

    fun addBorrowInfo(contractId: String) {
        if (borrowInfo?.borrowCheck(view as Context) == true) {
            val action: String = if (view.getIsUpdate()) "UPD" else "ADD"
            getData(0, model.getParam(getAddParamList(contractId), "ChangeLendRecord", action), true)
        }
    }

    fun clickChoose(pos: Int, parent: View) {
        timePos = pos
        when (pos) {
            0 -> {
                popWindow = PopChoose.showChooseType(view as Context, parent, "拆借类型",
                        Utils.getTypeDataList("LendingType"), 111, false)
            }
            1, 4 -> timeSelector?.show()
            6 -> {
                popWindow = PopChoose.showChooseType(view as Context, parent, "还款方式",
                        Utils.getTypeDataList("RePayType"), 222, false)
            }
        }
    }

    fun editChange(pos: Int, text: String) {
        when (pos) {
            2 -> {
                if (text.isNotBlank()) {
                    borrowInfo?.borrowAmount = text.toDouble()
                    showIncome(borrowInfo?.borrowAmount ?: 0.0, borrowInfo?.borrowInterest ?: 0.0,
                            borrowInfo?.borrowDate ?: "", borrowInfo?.predictBorrowDate ?: "")
                } else {
                    borrowInfo?.borrowAmount = 0.0
                }
            }
            3 ->
                if (text.isNotBlank()) {
                    borrowInfo?.borrowInterest = text.toDouble()
                    showIncome(borrowInfo?.borrowAmount ?: 0.0, borrowInfo?.borrowInterest ?: 0.0,
                            borrowInfo?.borrowDate ?: "", borrowInfo?.predictBorrowDate ?: "")
                } else {
                    borrowInfo?.borrowInterest = 0.0
                }
            8 -> borrowInfo?.borrowRemark = text
        }
    }

    private fun showIncome(amount: Double, income: Double, date1: String, date2: String) {
        if (amount != 0.0 && income != 0.0 && date1.isNotBlank() && date2.isNotBlank()) {
            var num = (DateUtil.getTimeLong(date2) - DateUtil.getTimeLong(date1)) / (1000 * 60 * 60 * 24)
            if (num >= 0) {
                if (num == 0L) num = 1
                borrowInfo?.predictBorrowIncome = (amount / 100) * income * num
                view.refreshItemData(5, Utils.parseMoney(borrowInfo?.predictBorrowIncome ?: 0.0))
            } else {
                borrowInfo?.predictBorrowIncome = 0.0
                view.refreshItemData(5, "")
            }
        }
    }

    private fun initTimeSelector() {
        if (isViewAttached) {
            timeSelector = TimeSelector(view as Context, TimeSelector.ResultHandler { time ->
                when (timePos) {
                    1 -> borrowInfo?.borrowDate = time
                    4 -> borrowInfo?.predictBorrowDate = time
                }
                showIncome(borrowInfo?.borrowAmount ?: 0.0, borrowInfo?.borrowInterest ?: 0.0,
                        borrowInfo?.borrowDate ?: "", borrowInfo?.predictBorrowDate ?: "")
                view.refreshItemData(timePos, time)
            }, "1900-01-01", DateUtil.getCurDate())
            timeSelector?.setScrollUnit(TimeSelector.SCROLLTYPE.YEAR, TimeSelector.SCROLLTYPE.MONTH, TimeSelector.SCROLLTYPE.DAY)
        }
    }

    private fun initRX() {
        borrowSubscription = RxBus.getInstance().toObservable(111, ChooseType::class.java).subscribe { chooseType ->
            hidePop()
            borrowInfo?.borrowTypeId = chooseType.id
            view.refreshItemData(timePos, chooseType.content)
        }
        returnSubscription = RxBus.getInstance().toObservable(222, ChooseType::class.java).subscribe { chooseType ->
            hidePop()
            borrowInfo?.rePayTypeId = chooseType.id
            view.refreshItemData(timePos, chooseType.content)
        }
    }

    private fun getAddParamList(contractId: String): MutableList<Any> {
        val list = ArrayList<Any>()
        val map1 = HashMap<String, Any>()
        map1.put("LendId", borrowInfo?.borrowId ?: 0)
        map1.put("ContractID", contractId)
        map1.put("LendType", borrowInfo?.borrowTypeId ?: 0)
        map1.put("LendDate", borrowInfo?.borrowDate ?: "")
        map1.put("LendAmount", borrowInfo?.borrowAmount ?: 0)
        map1.put("LendInterest", borrowInfo?.borrowInterest ?: 0)
        map1.put("PredictSettleDate", borrowInfo?.predictBorrowDate ?: "")
        map1.put("PredictLendIncome", borrowInfo?.predictBorrowIncome ?: 0)
        map1.put("RePayType", borrowInfo?.rePayTypeId ?: 0)
        map1.put("LendProposer", Hawk.get<String>("UserID"))
        map1.put("LendState", 1)
        map1.put("LendRemark", borrowInfo?.borrowRemark ?: "")
        val s = JSON.toJSONString(map1)
        list.add(s)
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            view.completeAdd()
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    private fun hidePop() {
        if (popWindow != null && popWindow?.isShowing!!) {
            popWindow?.dismiss()
        }
    }

    fun rxDeAttach() {
        if (popWindow != null && popWindow?.isShowing!!) {
            popWindow?.dismiss()
            popWindow = null
        }
        if (timeSelector != null) {
            timeSelector?.dismiss()
            timeSelector = null
        }
        if (borrowSubscription != null && !borrowSubscription!!.isUnsubscribed) {
            borrowSubscription!!.unsubscribe()
        }
        if (returnSubscription != null && !returnSubscription!!.isUnsubscribed) {
            returnSubscription!!.unsubscribe()
        }
    }
}