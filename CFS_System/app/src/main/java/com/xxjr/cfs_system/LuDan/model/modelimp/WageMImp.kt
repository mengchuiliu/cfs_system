package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.xxjr.cfs_system.LuDan.model.ModelImp
import entity.WageDetail

class WageMImp : ModelImp() {

    fun getWageDetails(data: String): MutableList<WageDetail> {
        val list = mutableListOf<WageDetail>()
        if (data.isNotBlank()) {
            val jsonArray = JSON.parseArray(data)
            if (jsonArray != null && jsonArray.size != 0) {
                var wage: WageDetail
                for (i in jsonArray.indices) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    wage = WageDetail()
                    wage.wageId = jsonObject.getIntValue("ID")
                    wage.confirmedState = jsonObject.getIntValue("ConfirmedState")
                    wage.state = jsonObject.getString("State")
                    wage.loanCode = jsonObject.getString("LoanCode")
                    wage.customerName = jsonObject.getString("CustomerNames")
                    wage.loanType = jsonObject.getIntValue("LoanType")
                    wage.product = jsonObject.getIntValue("BankProduct")
                    wage.lendAmount = jsonObject.getString("LendAmount")
                    wage.lendDate = jsonObject.getString("LendDate")
                    wage.calcCommission = jsonObject.getString("CalcCommission")
                    wage.commission = jsonObject.getString("Commission")
                    wage.remark = jsonObject.getString("Remark")
                    wage.userFeedBack = jsonObject.getString("UserFeedBack")
                    wage.financialFeedBack = jsonObject.getString("FinancialFeedBack")
                    list.add(wage)
                }
            }
        }
        return list
    }
}