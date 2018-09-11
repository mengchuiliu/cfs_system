package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.OverApplyMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.OverApplyVInter
import com.xxjr.cfs_system.tools.DateUtil
import entity.LoanInfo
import timeselector.TimeSelector
import java.util.HashMap

class OverApplyPresenter : BasePresenter<OverApplyVInter, OverApplyMImp>() {
    private var timeSelector: TimeSelector? = null
    var remarkShow = false
    var curOperateType: Int = 0

    override fun getModel(): OverApplyMImp = OverApplyMImp()

    override fun setDefaultValue() {
        initTimeChoose()
        when (view.getLoanInfo().scheduleId) {
            109, 5, -3, -4, -5 -> {
                if (view.getPermits().contains("CE")) {
                    remarkShow = true
                    view.showButton(true, "回退上一步", "发起结案")
                    if (view.getLoanInfo().isCase == 1) view.isCase(true) else view.isCase(false)
                    when (view.getLoanInfo().scheduleId) {
                        -3, -4, -5 -> view.showBackStep(false)
                        else -> view.showBackStep(true)
                    }
                }
            }
            1090, 50 -> {
                if (view.getPermits().contains("CV")) {
                    remarkShow = false
                    view.showButton(true, "不同意", "同意")
                }
            }
        }
        view.initRecycler(model.getItemData(view.getLoanInfo(), remarkShow, null))
        getOverData()
    }

    //获取结案数据
    fun getOverData() {
        getData(0, model.getParam(getParamList(view.getLoanInfo().loanId), "GetLoanCase"), true)
    }

    //删除出入账
    fun delBooksData(lendId: Int) {
        getData(1, model.getParam(getBooksParamList(lendId), "DeleteBookRecord"), true)
    }

    //跟进/回退
    fun scheduleUpdateOrBack(operateType: Int) {
        curOperateType = operateType
        getData(2, model.getParam(getBackParamList(view.getLoanInfo(), operateType), if (view.getLoanInfo().loanType == 1) "UpdateLoanMortgage" else "UpdateLoanCredit"), true)
    }

    private fun initTimeChoose() {
        if (isViewAttached) {
            timeSelector = TimeSelector(view as Context, TimeSelector.ResultHandler { time ->
                view.getLoanInfo().overTime = time
                view.refreshItem(26, time)
            }, "1900-01-01", DateUtil.getCurDate())
            timeSelector?.setScrollUnit(TimeSelector.SCROLLTYPE.YEAR, TimeSelector.SCROLLTYPE.MONTH, TimeSelector.SCROLLTYPE.DAY)
        }
    }

    fun showTimeChoose() {
        timeSelector?.show()
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        when (resultCode) {
            0 -> {
                if (data?.returnString.isNullOrBlank()) {
                    view.showMsg("null数据")
                } else {
                    val jsonObject = JSON.parseObject(data?.returnString)
                    view.getLoanInfo().currentRevenue = if (jsonObject["ToHeadquarterMoney"] == null) 0.0 else jsonObject.getDoubleValue("ToHeadquarterMoney")
                    val apply = model.getOverApplyData(jsonObject)
                    view.refreshAdapter(model.getItemData(view.getLoanInfo(), remarkShow, apply))
                    view.getLoanInfo().clerkName = apply.salesmanName
                    if (view.getLoanInfo().pactCode.isNullOrBlank()) {
                        view.getLoanInfo().pactCode = apply.contractCode
                    }
                    if (view.getLoanInfo().contractId == 0) {
                        view.getLoanInfo().contractId = apply.contractId ?: 0
                    }
                }
            }
            1 -> {
                view.showMsg("删除成功!")
                getOverData()
            }
            2 -> {//跟进回退
                when (view.getLoanInfo().scheduleId) {
                    109, 5, -3, -4, -5 -> {
                        when (curOperateType) {
                            1 -> view.showMsg("结案申请已发送成功!")
                            2 -> view.showMsg("回退成功!")
                        }
                    }
                    1090, 50 -> {
                        when (curOperateType) {
                            1 -> view.showMsg("结案申请已同意!")
                            2 -> view.showMsg("结案申请已拒绝!")
                        }
                    }
                }
                view.complete()
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) {
        view.showMsg(msg)
    }

    private fun getParamList(loanId: String): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        list.add(loanId)
        return list
    }

    private fun getBooksParamList(lendId: Int): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        list.add(lendId)
        return list
    }

    private fun getBackParamList(loanInfo: LoanInfo, operateType: Int): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        val map1 = HashMap<String, Any>()
        map1.put("LoanId", loanInfo.loanId)
        map1.put("LoanCode", loanInfo.loanCode)
        map1.put("ContractID", loanInfo.contractId)
        map1.put("LoanStatus", loanInfo.scheduleId)
        map1.put("OperateType", operateType)//1->跟进 ，2->回退
        map1.put("ServicePeople", Hawk.get("UserID"))
        map1.put("MortgageSehedule", if (loanInfo.loanType == 1) 2 else 1)
        map1.put("LastOpetateTime", loanInfo.updateTime)
        map1.put("LoanFollowDescription", view.getEdRemark())
        if (loanInfo.loanType == 1) {//抵押贷
            map1.put("IsForeclosureFloor", loanInfo.isForeclosureFloor)//是否赎楼
        }
        map1.put("BankManagerID", loanInfo.managerId)
        map1.put("ReplyMoney", loanInfo.replyAmount)
        map1.put("ReplyReciver", loanInfo.accountName)
        map1.put("ReplyBankAccount", loanInfo.account)
        map1.put("ReplyProvider", loanInfo.offer)
        map1.put("BankReplyTime", loanInfo.approvalTime)//银行批复时间
        map1.put("LendingMoney", loanInfo.lendingAmount)
        map1.put("LendingDate", loanInfo.lendingTime)
        map1.put("MonthPayMoney", loanInfo.monthAmount)
        map1.put("MonthPayDay", loanInfo.monthDate)
        map1.put("ReimbursementDeadline", loanInfo.returnTime)
        map1.put("CaseDate", loanInfo.overTime)
        map1.put("CaseRemark", view.getOverNote())
        val str = JSON.toJSONString(map1)
        list.add(str)
        return list
    }
}