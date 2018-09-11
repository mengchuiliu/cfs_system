package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.Utils
import entity.LoanInfo
import java.util.ArrayList

class LoanOverMImp : ModelImp() {
    private var bankArray: JSONArray? = null
    private var productArray: JSONArray? = null
    private var loanTypeArray: JSONArray? = null
    private var loanStateArray: JSONArray? = null

    fun getArray() {
        if (bankArray == null) {
            bankArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanBankType"), ""))
        }
        if (productArray == null) {
            productArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanProductType"), ""))
        }
        if (loanTypeArray == null) {
            loanTypeArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoansType"), ""))
        }
        if (loanStateArray == null) {
            loanStateArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanStateType"), ""))
        }
    }

    fun getQuery(query: Int): String {
        var str = ""
        when (query) {
            1 -> str = " AND Status IN (5,109,-3,-4,-5) "
            2 -> str = " AND Status IN (50,1090) "
            3 -> str = " AND Status IN (8,112,-1,-2,-6) "
            4 -> str = " AND FallBackType IN (1,2,3) "
        }
        return str
    }

    fun getLoanList(jsonArray: JSONArray): MutableList<LoanInfo> {
        val temp = ArrayList<LoanInfo>()
        var loanInfo: LoanInfo
        for (i in jsonArray.indices) {
            val `object` = jsonArray.getJSONObject(i)
            loanInfo = LoanInfo()
            loanInfo.yellowTime = if (`object`.getString("YellowTime") == null) "" else `object`.getString("YellowTime")
            loanInfo.redTime = if (`object`.getString("RedTime") == null) "" else `object`.getString("RedTime")
            loanInfo.isForeclosureFloor = if (`object`.getBoolean("IsForeclosureFloor") == null) false else `object`.getBoolean("IsForeclosureFloor")
            loanInfo.foreclosureTime = if (`object`["ForeclosureTime"] == null) null else `object`.getString("ForeclosureTime")
            loanInfo.isPrepayment = if (`object`.getBoolean("IsPrepayment") == null) false else `object`.getBoolean("IsPrepayment")
            loanInfo.updateTime = `object`.getString("UpdateTime")
            loanInfo.loanCode = `object`.getString("LoanCode")
            loanInfo.pactCode = `object`.getString("ContractCode")
            loanInfo.clerkID = `object`.getIntValue("S3")
            loanInfo.companyID = `object`.getString("CompanyID")
            loanInfo.bankId = `object`.getIntValue("L2")
            loanInfo.bankName = CacheProvide.getBank(bankArray, `object`.getIntValue("L2"))
            loanInfo.amount = `object`.getDoubleValue("L4")
            loanInfo.productId = `object`.getIntValue("L3")
            loanInfo.productName = CacheProvide.getProduct(productArray, `object`.getIntValue("L3"))
            loanInfo.chargeWay = `object`.getIntValue("L6")
            loanInfo.loanTypeName = CacheProvide.getLoanType(loanTypeArray, `object`.getIntValue("L1"))
            loanInfo.loanType = `object`.getIntValue("L1")
            loanInfo.scheduleId = `object`.getIntValue("Status")
            loanInfo.schedule = CacheProvide.getLoanStatus(loanStateArray, `object`.getIntValue("Status"))
            loanInfo.loanId = `object`.getIntValue("ID").toString()
            loanInfo.poundage = `object`.getDoubleValue("L8")
            loanInfo.customer = `object`.getString("CustomerNames")
            loanInfo.customerId = `object`.getString("CustomerIDs")
            loanInfo.mortgage = `object`.getIntValue("L9")
            loanInfo.note = `object`.getString("L11")
            loanInfo.percent = `object`.getFloatValue("L7")
            loanInfo.contractId = `object`.getIntValue("ContractID")
            if (`object`["L16"] == null) {
                loanInfo.managerId = 0
            } else {
                loanInfo.managerId = `object`.getIntValue("L16")
            }

            loanInfo.member = if (`object`["L13"] == null) 0 else `object`.getIntValue("L13")
            if (`object`["L14"] != null && `object`.getBooleanValue("L14")) {
                loanInfo.isNotary = "1"
            } else {
                loanInfo.isNotary = "0"
            }
            loanInfo.notaryTime = if (`object`.getString("L15") == null) "" else Utils.getTime(`object`.getString("L15"))
            loanInfo.notaryNote = `object`.getString("L19")
            loanInfo.replyAmount = if (`object`["A1"] == null) 0.0 else `object`.getDouble("A1")
            loanInfo.accountName = `object`.getString("A2")
            loanInfo.account = `object`.getString("A3")
            loanInfo.offer = `object`.getString("A4")
            loanInfo.lendingAmount = if (`object`["A16"] == null) 0.0 else `object`.getDouble("A16")
            loanInfo.lendingTime = if (`object`.getString("A17") == null) "" else Utils.getTime(`object`.getString("A17"))
            loanInfo.monthAmount = if (`object`["A5"] == null) 0.0 else `object`.getDouble("A5")
            loanInfo.monthDate = if (`object`["A6"] == null) "1" else `object`.getIntValue("A6").toString()
            loanInfo.returnTime = if (`object`.getString("A7") == null) "" else Utils.getTime(`object`.getString("A7"))
            loanInfo.overTime = if (`object`.getString("A10") == null) "" else Utils.getTime(`object`.getString("A10"))
            loanInfo.overNote = `object`.getString("A13")
            loanInfo.approvalTime = if (`object`.getString("BankReplyTime") == null) "" else Utils.getTime(`object`.getString("BankReplyTime"))
            loanInfo.isCase = if (`object`["IsCase"] == null) 0 else `object`.getIntValue("IsCase")
            temp.add(loanInfo)
        }
        return temp
    }
}