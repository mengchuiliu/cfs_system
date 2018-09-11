package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.view.View
import android.widget.PopupWindow
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.UpdateScheduleMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.UpdateScheduleVInter
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.ReimburseFormula
import entity.ChooseType
import entity.CommonItem
import rx.Subscription
import timeselector.TimeSelector

class UpdateSchedulePresenter : BasePresenter<UpdateScheduleVInter, UpdateScheduleMImp>() {
    private lateinit var jsonArray: JSONArray
    private var popWindow: PopupWindow? = null
    private var subscription: Subscription? = null
    private var timeSelector: TimeSelector? = null
    private var typeSubscription: Subscription? = null
    private var posTime: Int = 0

    override fun getModel(): UpdateScheduleMImp = UpdateScheduleMImp()

    override fun setDefaultValue() {
        initRX()
        jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey("RepaymentType")))
        view.initRecycler(model.getItemData(view.getLoanInfo(), false, view.getNewLend()))
    }

    fun refreshAdapterData(isback: Boolean) {
        view.refreshAdapter(model.getItemData(view.getLoanInfo(), isback, view.getNewLend()))
    }

    private fun initRX() {
        if (isViewAttached) {
            subscription = RxBus.getInstance().toObservable(Constants.BANK_MANAGER_CODE, ChooseType::class.java).subscribe { chooseType ->
                view.refreshItem(0, chooseType.content)
                view.getLoanInfo().managerId = chooseType.id
            }

            if (view.getNewLend()) {
                typeSubscription = RxBus.getInstance().toObservable(11, ChooseType::class.java).subscribe { s ->
                    hidePop()
                    view.getLoanInfo().paymentMethod = s.id
                    view.refreshItem(6, s.content)
                }
            }

            timeSelector = TimeSelector(view as Context, TimeSelector.ResultHandler { time ->
                when (view.getLoanInfo().scheduleId) {
                    3, 103 -> view.getLoanInfo().approvalTime = time
                    4, 108 -> {
                        when (posTime) {
                            1 -> view.getLoanInfo().lendingTime = time
                            4 -> view.getLoanInfo().returnTime = time
                        }
                        if (!view.getLoanInfo().lendingTime.isNullOrBlank() && !view.getLoanInfo().returnTime.isNullOrBlank()) {
                            view.getLoanInfo().lendingPeriod = ReimburseFormula.getMonthSpace(view.getLoanInfo().lendingTime, view.getLoanInfo().returnTime)
                            view.refreshItem(8, "${view.getLoanInfo().lendingPeriod}")
                        }
                    }
                }
                view.refreshItem(posTime, time)
            }, "1900-01-01", DateUtil.getCurDate())
            timeSelector?.setScrollUnit(TimeSelector.SCROLLTYPE.YEAR, TimeSelector.SCROLLTYPE.MONTH, TimeSelector.SCROLLTYPE.DAY)
        }
    }

    fun showReimburseType(parent: View) {
        if (isViewAttached) {
            popWindow = PopChoose.showChooseType(view as Context, parent, "还款类型", model.getChooseDatas(jsonArray), 11, false)
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

    //检测并上传数据
    fun checkAndUpdate(isback: Boolean) {
        if (!isback) {
            when (view.getLoanInfo().scheduleId) {
                1 -> {
                    if (view.getLoanInfo().managerId == 0) {
                        view.showMsg("请选择银行经理!")
                        return
                    }
                }
                3, 103 -> {
                    when {
                        view.getLoanInfo().replyAmount < 1000 -> {
                            view.showMsg("批复金额不能小于1000!")
                            return
                        }
                        view.getLoanInfo().accountName.isNullOrBlank() -> {
                            view.showMsg("请填写收款人!")
                            return
                        }
                        view.getLoanInfo().account.isNullOrBlank() -> {
                            view.showMsg("请填写收款账号!")
                            return
                        }
                        view.getLoanInfo().offer.isNullOrBlank() -> {
                            view.showMsg("请填写提供者!")
                            return
                        }
                        view.getLoanInfo().approvalTime.isNullOrBlank() -> {
                            view.showMsg("批复日期不能为空!")
                            return
                        }
                    }
                }
                4, 108 -> {
                    when {
                        view.getLoanInfo().lendingAmount < 1000 -> {
                            view.showMsg("放款金额不能小于1000!")
                            return
                        }
//                        view.getLoanInfo().lendingAmount > view.getLoanInfo().replyAmount -> {
//                            view.showMsg("放款金额不能大于批复金额!")
//                            return
//                        }
                        view.getLoanInfo().lendingTime.isNullOrBlank() -> {
                            view.showMsg("放款日期不能为空!")
                            return
                        }
                        DateUtil.getTimeLong(view.getLoanInfo().lendingTime) < DateUtil.getTimeLong(view.getLoanInfo().approvalTime) -> {
                            view.showMsg("放款日期不能小于批复日期!")
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
                            view.showMsg("还款期限不能为空!")
                            return
                        }
                        DateUtil.getTimeLong(view.getLoanInfo().returnTime) < DateUtil.getTimeLong(view.getLoanInfo().lendingTime) -> {
                            view.showMsg("还款期限不能小于放款日期!")
                            return
                        }
                        view.getLoanInfo().interestRate <= 0.0 || view.getLoanInfo().interestRate > 100 -> {
                            view.showMsg("请正确的填写年利率(0~100)!")
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
                }
            }
        }
        update(if (isback) 2 else 1)
    }

    //上传
    private fun update(OperateType: Int) {
        getData(0, getModel().getParam(getParamLsit(OperateType), if (view.getLoanInfo().loanType == 1) "UpdateLoanMortgage" else "UpdateLoanCredit"), true)
    }

    private fun getParamLsit(OperateType: Int): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        val map1 = HashMap<String, Any>()
        map1.put("LoanId", view.getLoanInfo().loanId)
        map1.put("LoanCode", view.getLoanInfo().loanCode)
        map1.put("ContractID", view.getLoanInfo().contractId)
        if (view.getLoanInfo().scheduleId == -3 || view.getLoanInfo().scheduleId == -4) {
            map1.put("LoanStatus", -2)
        } else {
            map1.put("LoanStatus", view.getLoanInfo().scheduleId)
        }
        map1.put("OperateType", OperateType)
        map1.put("ServicePeople", Hawk.get("UserID"))
        map1.put("MortgageSehedule", if (view.getLoanInfo().loanType == 1) 2 else 1)
        map1.put("LastOpetateTime", view.getLoanInfo().updateTime)
        map1.put("LoanFollowDescription", view.getRemark())
        if (view.getLoanInfo().loanType == 1) {//抵押贷
            map1.put("IsForeclosureFloor", view.getLoanInfo().isForeclosureFloor)//是否赎楼
        }
        map1.put("BankManagerID", view.getLoanInfo().managerId)
        map1.put("ReplyMoney", view.getLoanInfo().replyAmount)
        map1.put("ReplyReciver", view.getLoanInfo().accountName)
        map1.put("ReplyBankAccount", view.getLoanInfo().account)
        map1.put("ReplyProvider", view.getLoanInfo().offer)
        map1.put("BankReplyTime", view.getLoanInfo().approvalTime)//银行批复时间
        map1.put("LendingMoney", view.getLoanInfo().lendingAmount)
        map1.put("LendingDate", view.getLoanInfo().lendingTime)
        map1.put("MonthPayMoney", view.getLoanInfo().monthAmount)
        map1.put("MonthPayDay", view.getLoanInfo().monthDate)
        map1.put("ReimbursementDeadline", view.getLoanInfo().returnTime)
        map1.put("CaseDate", view.getLoanInfo().overTime)
        map1.put("CaseRemark", "")
        when (view.getLoanInfo().scheduleId) {
            4, 108 -> {
                map1.put("InterestRate", view.getLoanInfo().interestRate)
                map1.put("PaymentMethod", view.getLoanInfo().paymentMethod)
                map1.put("OtherPay", view.getLoanInfo().otherAmount)
                map1.put("OtherPayRemark", view.getLoanInfo().otherPayRemark ?: "")
                map1["RepaymentPeriods"] = view.getLoanInfo().lendingPeriod
            }
        }
        val str = JSON.toJSONString(map1)
        list.add(str)
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        view.complete()
    }

    override fun onFailed(resultCode: Int, msg: String?) {
        view.showMsg(msg)
    }

    fun rxDeAttach() {
        if (subscription != null && !subscription!!.isUnsubscribed) {
            subscription!!.unsubscribe()
        }
        if (typeSubscription != null && !typeSubscription!!.isUnsubscribed) {
            typeSubscription!!.unsubscribe()
        }
    }
}