package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.services.CacheProvide
import entity.ChooseType
import entity.CommonItem

/**
 * Created by Administrator on 2018/1/16.
 */
class AgreementMImp : ModelImp() {

    fun getAgreementData(ids: String): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var item: CommonItem<Any>
        for (i in 0..11) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.type = 0
                    item.name = "所属门店"
                    item.content = getStoreName(ids)
                }
                1 -> {
                    item.type = 1
                    item.name = "持卡人姓名"
                    item.hintContent = "请输入姓名"
                    item.isLineShow = true
                }
                2 -> {
                    item.type = 0
                    item.name = "代扣平台"
//                    item.content = "通联协议"
//                    item.isEnable = false
                }
                3 -> {
                    item.type = 0
                    item.name = "签约银行"
                }
                4 -> {
                    item.type = 0
                    item.name = "银行卡类型"
                }
                5 -> {
                    item.type = 0
                    item.name = "账号属性"
                }
                6 -> {
                    item.type = 2
                    item.name = "银行卡号"
                    item.hintContent = "请输入银行卡号"
                }
                7 -> {
                    item.type = 0
                    item.name = "签约者身份"
                }
                8 -> {
                    item.type = 0
                    item.name = "证件类型"
                }
                9 -> {
                    item.type = 1
                    item.name = "证件号"
                    item.hintContent = "请输入证件号"
                    item.isClick = true
                }
                10 -> {
                    item.type = 1
                    item.name = "预留手机号"
                    item.hintContent = "请输入手机号"
                }
                11 -> {
                    item.type = 1
                    item.name = "亲友身份证"
                    item.hintContent = "将授权该协议共享给亲友使用"
                    item.isClick = true
                }
            }
            list.add(item)
        }
        return list
    }

    /**
     * @param type 数据类型json键
     * @param aisleType 代扣平台类型
     * @return 返回选择数据类型列表
     */
    fun getTypeDataList(type: String, aisleType: String, isId: Boolean): MutableList<ChooseType> {
        val list = ArrayList<ChooseType>()
        val jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey(type)))
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                chooseType = ChooseType()
                val `object` = jsonArray.getJSONObject(i)
                if (`object`.getString("PayAisleType") == aisleType) {
                    if (isId) {
                        chooseType.ids = `object`.getString("ID")
                    } else {
                        chooseType.ids = `object`.getString("Value")
                    }
                    chooseType.content = `object`.getString("Name")
                    list.add(chooseType)
                }
            }
        }
        return list
    }

    /**
     * @param type 数据类型json键
     * @return 返回选择数据类型列表
     */
    fun getTypeDataList(type: String): MutableList<ChooseType> {
        val list = ArrayList<ChooseType>()
        val jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey(type)))
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                chooseType = ChooseType()
                val `object` = jsonArray.getJSONObject(i)
                chooseType.ids = `object`.getString("Value")
                chooseType.content = `object`.getString("Name")
                list.add(chooseType)
            }
        }
        return list
    }

    /**
     * @param type 数据类型json键
     * @return 返回选择数据类型列表
     */
    fun getPayAisleTypeDataList(type: String): MutableList<ChooseType> {
        val list = ArrayList<ChooseType>()
        val jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey(type)))
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                chooseType = ChooseType()
                val `object` = jsonArray.getJSONObject(i)
                val ids = `object`.getString("Value")
                if (ids == "5" || ids == "6") {
                    chooseType.ids = ids
                    chooseType.content = `object`.getString("Name")
                    list.add(chooseType)
                }
            }
        }
        return list
    }

    fun getTypeDataIdList(type: String): MutableList<ChooseType> {
        val list = ArrayList<ChooseType>()
        val jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey(type)))
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                chooseType = ChooseType()
                val `object` = jsonArray.getJSONObject(i)
                chooseType.ids = `object`.getString("ID")
                chooseType.content = `object`.getString("Name")
                list.add(chooseType)
            }
        }
        return list
    }

    private fun getStoreName(id: String): String {
        var store = ""
        val jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey("CompanyInfoType")))
        if (jsonArray != null && jsonArray.size != 0) {
            for (i in jsonArray.indices) {
                val `object` = jsonArray.getJSONObject(i)
                if (`object`.getString("ID") == id) {
                    store = `object`.getString("Name")
                    break
                }
            }
        }
        return store
    }
}