package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.view.View
import android.widget.PopupWindow
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.ManyLendMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.ManyLendVInter
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.ReimburseFormula
import entity.ChooseType
import rx.Subscription
import timeselector.TimeSelector

class ManyLendPresenter : BasePresenter<ManyLendVInter, ManyLendMImp>() {
    private lateinit var jsonArray: JSONArray
    private var popWindow: PopupWindow? = null
    private var subscription: Subscription? = null
    private var timeSelector: TimeSelector? = null
    private var typeSubscription: Subscription? = null
    private var posTime: Int = 0

    override fun getModel(): ManyLendMImp = ManyLendMImp()

    override fun setDefaultValue() {
        initRX()
        jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey("RepaymentType")))
        view.initRecycler(model.getLendingItem(view.getLoanInfo()))
    }

    private fun initRX() {
        if (isViewAttached) {
            subscription = RxBus.getInstance().toObservable(Constants.BANK_MANAGER_CODE, ChooseType::class.java).subscribe { chooseType ->
                view.refreshItem(0, chooseType.content)
                view.getLoanInfo().managerId = chooseType.id
            }

            typeSubscription = RxBus.getInstance().toObservable(11, ChooseType::class.java).subscribe { s ->
                hidePop()
                view.getLoanInfo().paymentMethod = s.id
                view.refreshItem(6, s.content)
            }

            timeSelector = TimeSelector(view as Context, TimeSelector.ResultHandler { time ->
                when (posTime) {
                    1 -> view.getLoanInfo().lendingTime = time
                    4 -> view.getLoanInfo().returnTime = time
                }
                view.refreshItem(posTime, time)
                if (!view.getLoanInfo().lendingTime.isNullOrBlank() && !view.getLoanInfo().returnTime.isNullOrBlank()) {
                    view.getLoanInfo().lendingPeriod = ReimburseFormula.getMonthSpace(view.getLoanInfo().lendingTime, view.getLoanInfo().returnTime)
                    view.refreshItem(8, "${view.getLoanInfo().lendingPeriod}")
                }
            }, "1900-01-01", DateUtil.getCurDate())
            timeSelector?.setScrollUnit(TimeSelector.SCROLLTYPE.YEAR, TimeSelector.SCROLLTYPE.MONTH, TimeSelector.SCROLLTYPE.DAY)
        }
    }

    fun showTimeChoose(position: Int) {
        posTime = position
        timeSelector?.show()
    }

    private fun hidePop() {
        if (popWindow != null && popWindow?.isShowing!!) {
            popWindow?.dismiss()
        }
    }

    fun showReimburseType(parent: View) {
        if (isViewAttached) {
            popWindow = PopChoose.showChooseType(view as Context, parent, "还款类型", model.getChooseDatas(jsonArray), 11, false)
        }
    }

    fun saveData(isUpdate: Boolean) {
        when {
            view.getLoanInfo().lendingAmount < 1000 -> {
                view.showMsg("放款金额不能小于1000!")
                return
            }
            view.getLoanInfo().lendingTime.isNullOrBlank() -> {
                view.showMsg("放款日期不能为空!")
                return
            }
            view.getLoanInfo().monthAmount <= 0 -> {
                view.showMsg("月供金额必须大于0!")
                return
            }
            view.getLoanInfo().monthAmount > view.getLoanInfo().lendingAmount -> {
                view.showMsg("月供金额不能大于放款金额!")
                return
            }
            view.getLoanInfo().monthDate.isNullOrBlank() -> {
                view.showMsg("月还款日不能为空!")
                return
            }
            view.getLoanInfo().monthDate.toInt() > 31 || view.getLoanInfo().monthDate.toInt() < 1 -> {
                view.showMsg("请正确的填写月还款日(1~31)!")
                return
            }
            view.getLoanInfo().returnTime.isNullOrBlank() -> {
                view.showMsg("还款日期不能为空!")
                return
            }
            DateUtil.getTimeLong(view.getLoanInfo().returnTime) < DateUtil.getTimeLong(view.getLoanInfo().lendingTime) -> {
                view.showMsg("还款日期不能小于放款日期!")
                return
            }
            view.getLoanInfo().interestRate <= 0.0 || view.getLoanInfo().interestRate > 100 -> {
                view.showMsg("请正确的填写年利率(0~100)")
                return
            }
            view.getLoanInfo().paymentMethod <= -1 -> {
                view.showMsg("请选择还款类型!")
                return
            }
            view.getLoanInfo().otherAmount > 0.0 -> {
                if (view.getLoanInfo().otherPayRemark.isNullOrBlank()) {
                    view.showMsg("请填写其他月供说明!")
                    return
                }
            }
        }
        getData(0, model.AddLendParam(getListParam(isUpdate), "LendingInfo", if (isUpdate) "UPD" else "ADD"), true)
    }

    private fun getListParam(isUpdate: Boolean): String {
        val map = HashMap<String, Any>()
        if (isUpdate) map["ID"] = view.getLoanInfo().lendingId ?: ""
        map["LoanId"] = view.getLoanInfo().loanId ?: ""
        map["LoanCode"] = view.getLoanInfo().loanCode ?: ""
        map["LendAmount"] = view.getLoanInfo().lendingAmount
        map["Remark"] = view.getLoanInfo().note ?: ""
        map["MonthPay"] = view.getLoanInfo().monthAmount
        map["MonthPayDay"] = view.getLoanInfo().monthDate
        map["PayDeadline"] = view.getLoanInfo().returnTime
        map["LendDate"] = view.getLoanInfo().lendingTime
        map["InterestRate"] = view.getLoanInfo().interestRate
        map["PaymentMethod"] = view.getLoanInfo().paymentMethod
        map["OtherPay"] = view.getLoanInfo().otherAmount
        map["OtherPayRemark"] = view.getLoanInfo().otherPayRemark ?: ""
        map["RepaymentPeriods"] = view.getLoanInfo().lendingPeriod
        return JSON.toJSONString(map)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        view.complete()
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun rxDeAttach() {
        if (subscription != null && !subscription!!.isUnsubscribed) {
            subscription!!.unsubscribe()
        }
        if (typeSubscription != null && !typeSubscription!!.isUnsubscribed) {
            typeSubscription!!.unsubscribe()
        }
    }
}