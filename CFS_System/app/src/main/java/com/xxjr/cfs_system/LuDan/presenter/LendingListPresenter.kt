package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.lending_list.LendingListActivity
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.CommonItem
import entity.LoanInfo

class LendingListPresenter : BasePresenter<LendingListActivity, ModelImp>() {
    private var homePage = 0
    private lateinit var repaymentTypes: MutableList<ChooseType>
    private lateinit var bankArray: JSONArray
    private lateinit var productArray: JSONArray

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        repaymentTypes = Utils.getTypeDataList("RepaymentType")
        bankArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey("LoanBankType")))
        productArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey("LoanProductType")))
        getLendingListData(0, 0)
    }

    fun getLendingListData(page: Int, searchType: Int) {
        if (isViewAttached) {
            homePage = page
            getData(0, model.getParam(mutableListOf<Any>().apply {
                add("1")//是否分页
                val sb = StringBuilder()
                sb.append(" 1=1 ")
                when (view.type) {
                    1 -> sb.append("and ConfirmedState = '-1' ")
                    2 -> sb.append("and ConfirmedState = '1' ")
                    else -> sb.append("and ConfirmedState in ('0' ,'-2' )")
                }
                if (view.searchContent.isNotBlank()) {
                    if (searchType == 1) {
                        sb.append(" and CustomerNames like '%${view.searchContent}%' ")
                    } else if (searchType == 2) {
                        sb.append(" and LoanCode like '%${view.searchContent}%' ")
                    }
                }
                if (searchType == 3 && (view.searchCompany ?: "").isNotBlank()) {
                    sb.append(" and CompanyID in ('").append(view.searchCompany).append("')")
                }
                if ((view.chooseTime1 ?: "").isNotBlank() && (view.chooseTime2
                                ?: "").isNotBlank()) {
                    sb.append(" and (UpdateTime between '${view.chooseTime1}' and '${view.chooseTime2} 23:59:59') ")
                }
                add(sb.toString())//条件
                add(homePage)
                add("10")
                add("UpdateTime DESC")
            }, "GetAdvanceList"), true)
        }
    }


    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.data ?: "").isNotBlank()) {
                        val jsonArray = JSONArray.parseArray(data?.data ?: "")
                        val temp = getItemData(jsonArray ?: JSONArray())
                        if (temp.size == 0) {
                            if (homePage == 0) {
                                view.showMsg("还没相关数据!")
                                view.loanInfos.clear()
                                view.refreshChange()
                            } else {
                                view.showMsg("没有更多数据!")
                            }
                        } else {
                            if (view.pull) {
                                view.loanInfos.addAll(temp)
                            } else {
                                view.loanInfos.clear()
                                view.loanInfos.addAll(temp)
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

    fun getLendingTitles() = mutableListOf<CommonItem<Any>>().apply {
        for (i in 0..2) {
            add(CommonItem<Any>().apply {
                isClick = i == view.type
                when (i) {
                    0 -> name = "未确认"
                    1 -> name = "有异议"
                    2 -> name = "已确认"
                }
            })
        }
    }

    private fun getItemData(jsonArray: JSONArray) = mutableListOf<LoanInfo>().apply {
        for (i in jsonArray.indices) {
            val jsonObject = jsonArray.getJSONObject(i)
            add(LoanInfo().apply {
                //ConfirmedState 确认状态 -2->已修改    -1->有异议  0->未确认   1-> 已确认
                loanCode = jsonObject.getString("LoanCode")
                lendingId = jsonObject.getString("ID")
                loanId = jsonObject.getString("LoanID")
                updateTime = Utils.FormatTime(jsonObject.getString("UpdateTime"), "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")
                customer = jsonObject.getString("CustomerNames")
                companyID = jsonObject.getString("CompanyName")
                paymentMethod = jsonObject.getIntValue("PaymentMethod")
                paymentName = Utils.getTypeValue(repaymentTypes, jsonObject.getIntValue("PaymentMethod"))
                mortgageName = jsonObject.getString("MortgageName")
                clerkName = jsonObject.getString("ClerkName")
                loanDescription = "【${CacheProvide.getBank(bankArray, jsonObject.getIntValue("LoanBank"))}·" +
                        "${CacheProvide.getProduct(productArray, jsonObject.getIntValue("Product"))}】"
                amount = jsonObject.getDoubleValue("ApplyMoney")
                lendingAmount = jsonObject.getDoubleValue("LendAmount")
                lendingTime = Utils.getTime(jsonObject.getString("LendDate"))
                interestRate = jsonObject.getDoubleValue("InterestRate")
                lendingPeriod = jsonObject.getIntValue("RepaymentPeriods")
                monthAmount = jsonObject.getDoubleValue("MonthPay")
                otherAmount = jsonObject.getDoubleValue("OtherPay")
                monthDate = jsonObject.getString("MonthPayDay")
                returnTime = Utils.getTime(jsonObject.getString("PayDeadline"))
                scheduleId = jsonObject.getIntValue("ConfirmedState")
                otherPayRemark = jsonObject.getString("OtherPayRemark") ?: ""
                note = jsonObject.getString("Remark") ?: ""
                disagreeReason = jsonObject.getString("DisagreeReason") ?: ""
            })
        }
    }
}