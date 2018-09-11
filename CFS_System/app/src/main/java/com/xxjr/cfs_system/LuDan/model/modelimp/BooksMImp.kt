package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.services.CacheProvide
import entity.ChooseType
import entity.CommonItem
import entity.LoanInfo

/**
 * Created by Administrator on 2017/9/19.
 * 出入账，转总部入账
 */
class BooksMImp : ModelImp() {

    fun getAccountParam(list: MutableList<Any>, Action: String, tranName: String): String {
        val map = HashMap<String, Any>()
        map.put("Action", Action)
        map.put("DBMarker", "CFS_Loan")
        map.put("Marker", "HQServer")
        map.put("IsUseZip", false)
        map.put("ParamString", list)
        map.put("TranName", tranName)
        Logger.e("==账户信息数据==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    fun getItemData(loanInfo: LoanInfo, type: Int): MutableList<CommonItem<Any>> {
        val list: MutableList<CommonItem<Any>> = ArrayList()
        var item: CommonItem<Any>
        for (i in 0..25) {
            item = CommonItem()
            when (i) {
                0 -> item.type = 0
                1 -> {
                    item.type = 1
                    item.name = "基本信息"
                    item.icon = R.mipmap.icon_basic_information
                }
                2 -> {
                    item.type = 2
                    item.isClick = true
                    if (type == 1) {
                        item.name = "收入类型"
                    } else {
                        item.name = "支出类型"
                    }
                }
                3 -> {
                    item.type = 2
                    item.name = "支付方式"
                    item.isClick = true
                }
                4 -> {
                    item.type = 3
                    item.name = "支付金额"
                    item.hintContent = "金额"
                    item.isClick = true
                }
                5 -> {
                    item.type = 3
                    item.name = "手续费"
                    item.hintContent = "金额"
                    item.isClick = true
                }
                6 -> {
                    item.type = 2
                    item.name = "支付时间"
                    item.isClick = true
                }
                7 -> {
                    item.type = 2
                    item.name = "付款/收款"
                    if (type == 1) item.content = "收款方" else item.content = "付款方"
                }
                8 -> {
                    item.type = 2
                    item.name = "经办人"
                    item.content = Hawk.get("UserRealName", "")
                }
                9 -> {
                    item.type = 2
                    item.name = "所属门店"
                    item.content = Hawk.get("CompanyName", "")
                }
                10 -> {
                    item.type = 3
                    item.name = "摘要"
                    item.isLineShow = true
                }
                11 -> item.type = 0
                12 -> {
                    item.type = 1
                    item.name = "账号信息"
                    item.icon = R.mipmap.icon_account_information
                }
                13 -> {
                    item.type = 2
                    if (type == 1) item.name = "收款方户名" else item.name = "付款方户名"
                    item.isClick = true
                }
                14 -> {
                    item.type = 2
                    if (type == 1) item.name = "收款方账号" else item.name = "付款方账号"
                }
                15 -> {
                    item.type = 2
                    if (type == 1) item.name = "选择付款方" else item.name = "选择收款方"
                    item.isClick = true
                }
                16 -> {
                    item.type = 3
                    if (type == 1) item.name = "付款方户名" else item.name = "收款方户名"
                }
                17 -> {
                    item.type = 3
                    if (type == 1) item.name = "付款方账号" else item.name = "收款方账号"
                    item.isLineShow = true
                }
                18 -> item.type = 0
                19 -> {
                    item.type = 1
                    item.name = "所属信息"
                    item.icon = R.mipmap.icon_belongs
                }
                20 -> {
                    item.type = 2
                    item.name = "合同编号"
                    item.content = loanInfo.pactCode
                }
                21 -> {
                    item.type = 2
                    item.name = "所属客户"
                    item.content = loanInfo.customer
                }
                22 -> {
                    item.type = 2
                    item.name = "所属业务员"
                    item.content = loanInfo.clerkName
                }
                23 -> {
                    item.type = 2
                    item.name = "贷款流水号"
                    item.content = loanInfo.loanCode
                }
                24 -> {
                    item.type = 2
                    item.name = "所属按揭员"
                    item.content = CacheProvide.getMortgageName(loanInfo.mortgage)
                }
                25 -> {
                    item.type = 3
                    item.name = "合同备注"
                    item.isLineShow = true
                }
            }
            list.add(item)
        }
        return list
    }

    fun getTypeDataList(type: String, booksType: Int): MutableList<ChooseType> {
        val list: MutableList<ChooseType> = ArrayList()
        val jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey(type)))
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                chooseType = ChooseType()
                val json = jsonArray.getJSONObject(i)
                if (booksType == 1) {
                    if (json.getIntValue("ParentID") == 1 && json.getIntValue("Value") != 16 && json.getIntValue("Value") != 5) {
                        chooseType.id = json.getIntValue("Value")
                        if (chooseType.id == 4) {
                            chooseType.content = "回款/尾款"
                        } else {
                            chooseType.content = json.getString("Name")
                        }
                        list.add(chooseType)
                    }
                } else {
                    if (json.getIntValue("ParentID") == 2) {//7,17
                        if (json.getIntValue("Value") == 7 || json.getIntValue("Value") == 17) {
                            chooseType.id = json.getIntValue("Value")
                            chooseType.content = json.getString("Name")
                            list.add(chooseType)
                        }
                    }
                }
            }
        }
        return list
    }

    /**
     * @param type 数据类型json键 id过滤出账代扣
     * @return 返回选择数据类型列表
     */
    fun getCostDataList(type: String, id: Int): MutableList<ChooseType> {
        val list = ArrayList<ChooseType>()
        val jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey(type)))
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                chooseType = ChooseType()
                val `object` = jsonArray.getJSONObject(i)
                if (`object`.getIntValue("Value") == 3) {
                    if (id != 2) {//出账代扣过滤，实际还是线下转账
                        chooseType.id = `object`.getIntValue("Value")
                        chooseType.content = `object`.getString("Name")
                        list.add(chooseType)
                    }
                } else {
                    chooseType.id = `object`.getIntValue("Value")
                    chooseType.content = `object`.getString("Name")
                    list.add(chooseType)
                }
            }
        }
        return list
    }

    //获取代扣平台
    fun getPayAisles(): MutableList<ChooseType> {
        val list = mutableListOf<ChooseType>()
        val jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("PayAisleType"), ""))
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                chooseType = ChooseType()
                val `object` = jsonArray.getJSONObject(i)
                chooseType.content = `object`.getString("Name")
                chooseType.ids = `object`.getString("Value")
                list.add(chooseType)
            }
        }
        return list
    }

    //获取协议平台
    fun getProtocols(protocols: MutableList<ChooseType>, type: String): MutableList<ChooseType> {
        val list = mutableListOf<ChooseType>()
        for (chooseType in protocols) {
            if (chooseType.type == type) {
                list.add(chooseType)
            }
        }
        return list
    }
}