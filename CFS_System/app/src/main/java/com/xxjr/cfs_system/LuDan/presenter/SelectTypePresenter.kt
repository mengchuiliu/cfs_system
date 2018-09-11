package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.SelectTypeActivity
import com.xxjr.cfs_system.services.CacheProvide
import entity.ChooseType

/**
 * Created by Administrator on 2018/2/2.
 */
class SelectTypePresenter : BasePresenter<SelectTypeActivity, ModelImp>() {
    private lateinit var jsonArray: JSONArray
    private lateinit var ids: List<String>
    private var isAll = false
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        ids = view.getIds().split(",")
        if (ids.contains("all")) isAll = true
        when (view.getType()) {
            0 -> {
                view.getListData().add(ChooseType().apply {
                    ids = "all"
                    content = "全国"
                    isChoose = isAll
                })
                view.setTitle("区域")
                jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("ZoneType"), ""))
                getDataInit(jsonArray)
            }
            1 -> {
                view.setTitle("门店")
                jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey("CompanyInfoType")))
                getDataInit(jsonArray)
            }
            2 -> {
                view.setTitle("梯队")
                jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey("EchelonType")))
                getDataInit(jsonArray)
            }
            3 -> {
                view.setTitle("销售来源")
                getSaleData()
            }
        }
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    private fun getDataInit(jsonArray: JSONArray?) {
        if (jsonArray != null && jsonArray.isNotEmpty()) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                chooseType = ChooseType()
                val jsonObject = jsonArray.getJSONObject(i)
                chooseType.content = jsonObject.getString("Name")
                when (view.getType()) {
                    0 -> chooseType.ids = jsonObject.getString("Value")
                    1, 2 -> chooseType.ids = jsonObject.getString("ID")
                }
                for (id in ids) {
                    if (id.isNotBlank() && id == chooseType.ids) {
                        chooseType.isChoose = true
                        break
                    }
                }
                if (view.getType() == 2) {//梯队过滤
                    if (chooseType.ids != "1" && chooseType.ids != "2" && chooseType.ids != "3") {
                        view.getListData().add(chooseType)
                    }
                } else {
                    view.getListData().add(chooseType)
                }
            }
        }
        view.initRV()
    }

    private fun getSaleData() {
        var chooseType: ChooseType
        for (i in 0..2) {
            chooseType = ChooseType()
            when (i) {
                0 -> {
                    chooseType.content = "电销门店"
                    chooseType.ids = "4"
                }//4
                1 -> {
                    chooseType.content = "网销门店"
                    chooseType.ids = "5"
                }//5
                2 -> {
                    chooseType.content = "行销门店"
                    chooseType.ids = "6"
                }//6
            }
            for (id in ids) {
                if (id.isNotBlank() && id == chooseType.ids) {
                    chooseType.isChoose = true
                    break
                }
            }
            view.getListData().add(chooseType)
        }
        view.initRV()
    }
}