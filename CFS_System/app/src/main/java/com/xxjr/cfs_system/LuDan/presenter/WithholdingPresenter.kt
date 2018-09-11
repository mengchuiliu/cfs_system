package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.withholding_agreement.WithholdingList
import com.xxjr.cfs_system.services.CacheProvide
import entity.ChooseType
import entity.SignInfo

class WithholdingPresenter : BasePresenter<WithholdingList, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        getData(0, model.getParam(mutableListOf<Any>("0", "DelMarker = 0", "", "", "InsertTime DESC"), "GetProtocolList"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.data ?: "").isNotBlank()) {
                        val jsonArray = JSONArray.parseArray(data?.data)
                        view.freshRv(getItemData(jsonArray))
                    } else {
                        view.freshRv(mutableListOf())
                        view.showMsg("暂无数据")
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    private fun getItemData(jsonArray: JSONArray): MutableList<SignInfo> = mutableListOf<SignInfo>().apply {
        val bankList = getTypeDataList("PaymentBank")//签约银行
        val signerList = getTypeDataList("SignerType")//签约者身份
        val accountTypeList = getTypeDataList("PayBankCardType")//银行卡类型
        val payAccountPropList = getTypeDataList("PayAccountProp")//账户类型
        val aisleTypeList = getTypeDataList("PayAisleType")//代扣平台
        for (i in jsonArray.indices) {
            val jsonObject = jsonArray.getJSONObject(i)
            add(SignInfo().apply {
                aisleType = getTypeValue(aisleTypeList, jsonObject.getString("AisleType") ?: "")
                val AisleType = if (jsonObject.getString("AisleType") ?: "" == "5") "1"
                else jsonObject.getString("AisleType") ?: ""
                companyName = jsonObject.getString("CompanyName") ?: ""
                zoneName = jsonObject.getString("ZoneName") ?: ""
                accountProp = getTypeValue(payAccountPropList, jsonObject.getString("AccountProp")
                        ?: "")
                accountType = getTypeValue(accountTypeList, AisleType, jsonObject.getString("AccountType")
                        ?: "")
                accountName = jsonObject.getString("CardName") ?: ""
                bankCode = getTypeValue(bankList, AisleType, jsonObject.getString("BankCode") ?: "")
                signer = getTypeValue(signerList, jsonObject.getString("SignerType") ?: "")
                telPhone = (jsonObject.getString("ReservePhone")
                        ?: "").replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2")
                bankRule = jsonObject.getString("BankRule") ?: ""
                accountNo = getTextShow(jsonObject.getString("BankCardNo") ?: "", 1) //银行卡号码
                IDCardNo = getTextShow(jsonObject.getString("IDCardNo") ?: "", 2) //证件号码
                protocolNo = jsonObject.getString("ProtocolNo") ?: ""
                protocolState = if (jsonObject.getIntValue("Status") == 1) "正常" else "冻结"
                remark = jsonObject.getString("FaildRemark")//冻结原因
            })
        }
    }

    /**
     * @param type 数据类型json键
     * @return 返回选择数据类型列表
     */
    fun getTypeDataList(type: String): List<ChooseType> {
        val list = java.util.ArrayList<ChooseType>()
        val jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey(type), ""))
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                chooseType = ChooseType()
                val `object` = jsonArray.getJSONObject(i)
                chooseType.ids = `object`.getString("Value")
                chooseType.content = `object`.getString("Name")
                chooseType.type = `object`.getString("PayAisleType")
                list.add(chooseType)
            }
        }
        return list
    }


    fun getTypeValue(types: List<ChooseType>, value: String): String {
        for (chooseType in types) {
            if (chooseType.ids == value) {
                return chooseType.content
            }
        }
        return ""
    }

    fun getTypeValue(types: List<ChooseType>, payAisleType: String, value: String): String {
        for (chooseType in types) {
            if (chooseType.type == payAisleType) {
                if (chooseType.ids == value) {
                    return chooseType.content
                }
            }
        }
        return ""
    }

    private fun getTextShow(text: String, type: Int): String {
        if (text.length > 9) {
            return text.substring(0, 5) + (if (type == 2) "*******" else "**********") + text.subSequence(text.length - 4, text.length)
        } else {
            return text
        }
    }
}