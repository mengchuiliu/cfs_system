package com.xxjr.cfs_system.LuDan.model.modelimp

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.services.CacheProvide
import entity.ChooseType
import entity.CommonItem
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Administrator on 2018/3/9.
 */
class RegisteredMImp : ModelImp() {
    fun getRegisteredParam(json: String): String {
        val map = HashMap<String, Any>()
        map["IsUseZip"] = false
        map["Function"] = ""
        map["ParamString"] = ""
        map["Json"] = json
        map["Action"] = "RegGoldAccount"
        map["TranName"] = "GoldAccount"
        Logger.e("==通用数据参数==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    fun getRegisteredData(type: Int): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        when (type) {
            1 -> {
                val item: CommonItem<Any> = CommonItem()
                item.type = 1
                item.name = "客户姓名"
                item.hintContent = "请输入姓名"
                item.isLineShow = true
                list.add(item)
            }
            2 -> {
                val item1: CommonItem<Any> = CommonItem()
                item1.type = 1
                item1.name = "企业名称"
                item1.hintContent = "请输入企业名称"
                item1.isLineShow = true
                list.add(item1)
                val item2: CommonItem<Any> = CommonItem()
                item2.type = 1
                item2.name = "法人姓名"
                item2.hintContent = "请输入法人姓名"
                item2.isLineShow = true
                list.add(item2)
            }
        }
        list.addAll(getRegisteredOtherData(type))
        return list
    }

    private fun getRegisteredOtherData(type: Int): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var item: CommonItem<Any>
        for (i in 0..7) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.type = 0
                    item.name = "证件类型"
                    item.isEnable = false
                    item.content = "身份证"
                }
                1 -> {
                    item.type = 1
                    item.name = "证件号码"
                    item.hintContent = "请输入证件号"
                    item.isClick = true
                }
                2 -> {
                    item.type = 1
                    item.name = "手机号码"
                    item.hintContent = "招商银行卡必须为预留手机"
                }
                3 -> {
                    item.type = 1
                    item.name = "Email"
                    item.hintContent = "请输入邮箱（选填）"
                    item.isLineShow = true
                }
                4 -> {
                    item.type = 0
                    item.name = "银行名称"
                }
                5 -> {
                    item.type = 0
                    item.name = "开户行地区"
                }
                6 -> {
                    item.type = 1
                    item.name = "支行名"
                    item.hintContent = "请输入支行名称（选填）"
                    item.isLineShow = true
                }
                7 -> {
                    item.type = 2
                    item.name = "银行卡号"
                    item.hintContent = "需开通银联在线支付功能"
                }
            }
            list.add(item)
        }
        return list
    }

    fun getTypeDataList(type: String, isId: Boolean): List<ChooseType> {
        val list = ArrayList<ChooseType>()
        val jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey(type), ""))
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                chooseType = ChooseType()
                val `object` = jsonArray.getJSONObject(i)
                if (`object`.getIntValue("PayAisleType") == 2) {
                    if (isId) {
                        chooseType.id = `object`.getIntValue("ID")
                    } else {
                        chooseType.id = `object`.getIntValue("Value")
                    }
                    chooseType.content = `object`.getString("Name")
                    list.add(chooseType)
                }
            }
        }
        return list
    }

}