package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.services.CacheProvide

import entity.SetMenu
import java.util.*

class SetMImp : ModelImp() {

    fun getIncreaseCacheParam(list: List<Any>, tranName: String): String {
        val map = HashMap<String, Any>()
        map.put("Function", "Mobile")
        map.put("TranName", tranName)
        val map1 = HashMap<String, Any>()
        map1.put("Version", -1)
        map1.put("CompanyGroup", Hawk.get(CacheProvide.getCacheKey("CompanyGroup")))
        map1.put("TableGroup", list)
        map1.put("ToTableGroup", list)
        map.put("Json", JSON.toJSONString(map1))
        Logger.e("==缓存参数==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    fun getItemList(isRemind: Boolean): MutableList<SetMenu> {
        val list = ArrayList<SetMenu>()
        var menu: SetMenu
        for (i in 0..8) {
            menu = SetMenu()
            menu.type = i
            when (i) {
                0 -> {
                    menu.icon = R.mipmap.icon_qr
                    menu.contentName = "来访登记二维码"
                }
                1 -> {
                    menu.icon = R.mipmap.idcard
                    menu.contentName = "个人信息"
                }
                2 -> {
                    menu.icon = R.mipmap.yinhangka
                    menu.contentName = "银行卡"
                }
                3 -> {
                    menu.icon = R.mipmap.icon_synchrodata
                    menu.contentName = "数据同步"
                }
                4 -> {
                    menu.icon = R.mipmap.remind
                    menu.contentName = "通知设置"
                    menu.isShow = isRemind
                }
                5 -> {
                    menu.icon = R.mipmap.icon_password
                    menu.contentName = "手势密码"
                    menu.isShow = false
                }
                6 -> {
                    menu.icon = R.mipmap.icon_password
                    menu.contentName = "修改密码"
                }
                7 -> {
                    menu.icon = R.mipmap.icon_upgrade
                    menu.contentName = "检测更新"
                    menu.isShow = false
                }
                8 -> {
                    menu.icon = R.mipmap.icon_log_off
                    menu.contentName = "退出登录"
                }
            }
            list.add(menu)
        }
        return list
    }

    fun saveCacheData(returnMsg: String) {
        val jsonObject = JSONObject.parseObject(returnMsg)
        Hawk.put(CacheProvide.getCacheKey("Version"), jsonObject.getIntValue("Version"))
        Hawk.put(CacheProvide.getCacheKey("CompanyGroup"), jsonObject.getJSONArray("CompanyGroup"))
        Hawk.put(CacheProvide.getCacheKey("TableGroup"), jsonObject.getJSONArray("TableGroup"))
        val jsonData = JSONObject()
        jsonData.putAll(jsonObject.getJSONObject("ConfigData"))
        jsonData.putAll(jsonObject.getJSONObject("HQData"))
        Hawk.put(CacheProvide.getCacheKey("SexType"), saveJSON(jsonData.getJSONArray("SexType")))
        Hawk.put(CacheProvide.getCacheKey("IDType"), saveJSON(jsonData.getJSONArray("IDType")))
        Hawk.put(CacheProvide.getCacheKey("LoansType"), saveJSON(jsonData.getJSONArray("LoansType")))
        Hawk.put(CacheProvide.getCacheKey("ContractStatus"), saveJSON(jsonData.getJSONArray("ContractStatus")))
        Hawk.put(CacheProvide.getCacheKey("LoanStateType"), saveJSON(jsonData.getJSONArray("LoanStateType")))
        Hawk.put(CacheProvide.getCacheKey("CreditLoanOperateType"), saveJSON(jsonData.getJSONArray("CreditLoanOperateType")))
        Hawk.put(CacheProvide.getCacheKey("CompanyInfoType"), saveJSON(jsonData.getJSONArray("CompanyInfoType")))
        Hawk.put(CacheProvide.getCacheKey("MortgageUserType"), saveJSON(jsonData.getJSONArray("MortgageUserType")))
        Hawk.put(CacheProvide.getCacheKey("LoanBankType"), saveJSON(jsonData.getJSONArray("LoanBankType")))
        Hawk.put(CacheProvide.getCacheKey("LoanProductType"), saveJSON(jsonData.getJSONArray("LoanProductType")))
        Hawk.put(CacheProvide.getCacheKey("LoanCostType"), saveJSON(jsonData.getJSONArray("LoanCostType")))
        Hawk.put(CacheProvide.getCacheKey("BookTypes"), saveJSON(jsonData.getJSONArray("BookTypes")))
        Hawk.put(CacheProvide.getCacheKey("EnterAccountType"), saveJSON(jsonData.getJSONArray("EnterAccountType")))
        Hawk.put(CacheProvide.getCacheKey("T_Config"), saveJSON(jsonData.getJSONArray("T_Config")))
//        Hawk.put(CacheProvide.getCacheKey("WithholdPlatf"), saveJSON(jsonData.getJSONArray("WithholdPlatf")))
        Hawk.put(CacheProvide.getCacheKey("LendingType"), saveJSON(jsonData.getJSONArray("LendingType")))
        Hawk.put(CacheProvide.getCacheKey("LendStateType"), saveJSON(jsonData.getJSONArray("LendStateType")))
        Hawk.put(CacheProvide.getCacheKey("RePayType"), saveJSON(jsonData.getJSONArray("RePayType")))
        Hawk.put(CacheProvide.getCacheKey("ImageDataType"), saveJSON(jsonData.getJSONArray("ImageDataType")))
        Hawk.put(CacheProvide.getCacheKey("ZoneType"), saveJSON(jsonData.getJSONArray("ZoneType")))
        Hawk.put(CacheProvide.getCacheKey("ImprovementType"), saveJSON(jsonData.getJSONArray("ImprovementType")))

        Hawk.put(CacheProvide.getCacheKey("PayAisleType"), saveJSON(jsonData.getJSONArray("PayAisleType")))
        Hawk.put(CacheProvide.getCacheKey("PaymentBank"), saveJSON(jsonData.getJSONArray("PaymentBank")))
        Hawk.put(CacheProvide.getCacheKey("PayBankCardType"), saveJSON(jsonData.getJSONArray("PayBankCardType")))
        Hawk.put(CacheProvide.getCacheKey("PayAccountProp"), saveJSON(jsonData.getJSONArray("PayAccountProp")))
        Hawk.put(CacheProvide.getCacheKey("PayCardType"), saveJSON(jsonData.getJSONArray("PayCardType")))
        Hawk.put(CacheProvide.getCacheKey("SignerType"), saveJSON(jsonData.getJSONArray("SignerType")))

        Hawk.put(CacheProvide.getCacheKey("EchelonType"), saveJSON(jsonData.getJSONArray("EchelonType")))
        Hawk.put(CacheProvide.getCacheKey("MortgageScoreType"), saveJSON(jsonData.getJSONArray("MortgageScoreType")))
        Hawk.put(CacheProvide.getCacheKey("RepaymentType"), saveJSON(jsonData.getJSONArray("RepaymentType")))
    }

    private fun saveJSON(jsonArray: JSONArray?): String {
        return if (jsonArray == null) {
            ""
        } else {
            jsonArray.toJSONString()
        }
    }

    fun getIsRemind(permissions: String): Boolean {
        val strings = permissions.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (strings.size > 1) {
            val str = strings[1].split("M".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (str.isNotEmpty()) {
                for (s in str) {
                    if (s.startsWith("80")) {
                        val str1 = s.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (str1.isNotEmpty()) {
                            val s1 = str1[1]
                            val myPermissions = s1.split("F".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            if (myPermissions.contains("818") || myPermissions.contains("821")) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

}
