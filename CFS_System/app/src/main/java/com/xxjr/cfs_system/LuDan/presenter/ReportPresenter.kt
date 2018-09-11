package com.xxjr.cfs_system.LuDan.presenter

import android.graphics.Color
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.github.mikephil.charting.data.BarEntry
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.viewinter.ReportVInter
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.CommonItem
import java.util.*

class ReportPresenter : BasePresenter<ReportVInter, ModelImp>() {
    private var contrastType = 0

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        if (isViewAttached) {
            view.initPagerView(getTitles())
        }
    }

    //获取同期对比数据接口
    fun getContrastData(contrastType: Int, companyId: String) {
        this.contrastType = contrastType
        getData(0, model.getParam(mutableListOf<Any>().apply {
            val map1 = HashMap<String, Any>()
            map1.put("MethondName", "GetAppRingRatioStatistics")
            map1.put("Type", contrastType)
            map1.put("CompanyId", companyId)
            map1.put("EndDate", DateUtil.getFormatDate(Date()))
            add(JSON.toJSONString(map1))
        }, "StoreLoanStatistics"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            if ((data?.returnDataSet ?: JSONObject()).isNotEmpty()) {
                view.refreshBarData(getDatas(data?.returnDataSet
                        ?: JSONObject()), if ((data?.returnDataSet
                                ?: JSONObject()).isEmpty()) "" else JSONObject.toJSONString(data?.returnDataSet
                        ?: JSONObject()))
            } else {
                view.refreshBarData(mutableListOf(), "")
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun getCompanyTitles(): List<CommonItem<*>> {
        val titles = java.util.ArrayList<CommonItem<*>>()
        var commonItem: CommonItem<*>
        for (i in 0..10) {
            commonItem = CommonItem<Any>()
            when (i) {
                0 -> {
                    commonItem.isClick = true
                    commonItem.setName(" 全部 ")
                }
                1 -> commonItem.setName("同期对比")
                2 -> commonItem.setName("电销门店")//4
                3 -> commonItem.setName("网销门店")//5
                4 -> commonItem.setName("行销门店")//6
                5 -> commonItem.setName("合作门店")//7
                6 -> commonItem.setName("北京")//
                7 -> commonItem.setName("上海")//
                8 -> commonItem.setName("深圳")//
                9 -> commonItem.setName("广州")//
                10 -> commonItem.setName("第5区")//
            }
            titles.add(commonItem)
        }
        return titles
    }

    fun getContrastTitles(): List<CommonItem<*>> {
        val titles = java.util.ArrayList<CommonItem<*>>()
        var commonItem: CommonItem<*>
        for (i in 0..3) {
            commonItem = CommonItem<Any>()
            when (i) {
                0 -> {
                    commonItem.isClick = true
                    commonItem.setName("回款量")
                }
                1 -> commonItem.setName("签单量")
                2 -> commonItem.setName("放款金额")
                3 -> commonItem.setName("放款笔数")
            }
            titles.add(commonItem)
        }
        return titles
    }

    private fun getTitles(): MutableList<String> {
        val list: MutableList<String> = ArrayList()
        list.add("回款量")
        list.add("签单量")
        list.add("回款签单排名")
        list.add("门店放款排名")
        list.add("员工回款排名")
        list.add("员工签单排名")
        list.add("门店签单排名")
        list.add("来访量统计")
        return list
    }

    fun getTypeDataList(): List<ChooseType> {
        val list = java.util.ArrayList<ChooseType>()
        val chooseType1 = ChooseType()
        chooseType1.content = "全部"
        chooseType1.ids = ""
        list.add(chooseType1)
        val jsonArray = JSONArray.parseArray(Hawk.get<String>(CacheProvide.getCacheKey("CompanyInfoType")))
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                val `object` = jsonArray.getJSONObject(i)
                chooseType = ChooseType()
                chooseType.content = `object`.getString("Name")
                chooseType.ids = `object`.getString("ID")
                list.add(chooseType)
            }
        }
        return list
    }

    fun getStoreName(id: String): String {
        if (id.isBlank()) {
            return "全部"
        } else {
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

    //获取同期对比数据
    fun getDatas(jsonObject: JSONObject) = mutableListOf<CommonItem<BarEntry>>().apply {
        for (i in 2 downTo 0) {
            add(CommonItem<BarEntry>().apply {
                val jsonArray = jsonObject.getJSONArray(DateUtil.getBeforeMonth(-i))
                name = "${DateUtil.getBeforeMonth(-i)}月"
                when (i) {
                    2 -> icon = Color.parseColor("#54b1fd")
                    1 -> icon = Color.parseColor("#9cd632")
                    0 -> icon = Color.parseColor("#fe6caa")
                }
                list = mutableListOf<BarEntry>().apply {
                    if (jsonArray != null && jsonArray.isNotEmpty()) {
                        for (j in jsonArray.indices) {
                            val json = jsonArray.getJSONObject(j)
                            add(BarEntry(json.getFloat("Day"),
                                    if (contrastType == 0 || contrastType == 2) Utils.div(json.getDouble("TotalValue")).toFloat()
                                    else json.getFloat("TotalValue")))
                        }
                    }
                }
            })
        }
    }
}