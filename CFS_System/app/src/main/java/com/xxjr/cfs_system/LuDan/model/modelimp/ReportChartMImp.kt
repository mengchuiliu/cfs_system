package com.xxjr.cfs_system.LuDan.model.modelimp

import android.annotation.SuppressLint
import android.graphics.Color
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.flyco.tablayout.listener.CustomTabEntity
import com.github.mikephil.charting.data.Entry
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Administrator on 2018/1/31.
 */
@SuppressLint("SimpleDateFormat")
class ReportChartMImp : ModelImp() {
    fun getTabTypes(): MutableList<CustomTabEntity> {
        val list = mutableListOf<CustomTabEntity>()
        for (i in 0..3) {
            when (i) {
                0 -> list.add(TabEntity("区域"))
                1 -> list.add(TabEntity("门店"))
                2 -> list.add(TabEntity("梯队"))
                3 -> list.add(TabEntity("销售来源"))
            }
        }
        return list
    }

    fun getDateTypes(dateType: Int): MutableList<CommonItem<*>> {
        val list = mutableListOf<CommonItem<*>>()
        when (dateType) {
            0 -> list.addAll(getWeekTabs())
            1 -> list.addAll(getMonthTabs())
            2 -> list.addAll(getYearTabs())
        }
        return list
    }

    //获取折线图数据
    fun getChartData(dataType: Int, dateType: Int, selectIds: String, jsonObject: JSONObject): MutableList<CommonItem<Entry>> {
        val commonItems = mutableListOf<CommonItem<Entry>>()
        val ids = selectIds.split(",")
        for (j in ids.indices) {
            val id = ids[j]
            val jsonArray = jsonObject.getJSONArray(id)
            val commonItem = CommonItem<Entry>()
            if (id == "all") commonItem.name = "全国" else commonItem.name = jsonArray.getJSONObject(0).getString("Name")
            commonItem.icon = getLineColor(j)
            val entrys = mutableListOf<Entry>()
            for (i in jsonArray.indices) {
                val x = when (dateType) {
                    1 -> if (jsonArray.getJSONObject(i).getFloatValue("Day") - 1 < 0) 0f else (jsonArray.getJSONObject(i).getFloatValue("Day") - 1)
                    2 -> if (jsonArray.getJSONObject(i).getFloatValue("Months") - 1 < 0) 0f else (jsonArray.getJSONObject(i).getFloatValue("Months") - 1)
                    else -> 0f
                }
                val y = when (dataType) {
                    0 -> jsonArray.getJSONObject(i).getFloatValue("LoanCount")
                    1 -> Utils.div(jsonArray.getJSONObject(i).getDoubleValue("ReplyAmount")).toFloat()
                    2 -> Utils.div(jsonArray.getJSONObject(i).getDoubleValue("LendAmount")).toFloat()
                    3 -> Utils.div(jsonArray.getJSONObject(i).getDoubleValue("BackPaymentAmount")).toFloat()
                    else -> 0f
                }
                entrys.add(Entry(x, y))
            }
            commonItem.list = entrys
            commonItems.add(commonItem)
        }
        return commonItems
    }

    //每条线的颜色
    private fun getLineColor(pos: Int): Int = when (pos) {
        0 -> Color.parseColor("#f4af37")
        1 -> Color.parseColor("#54b1fd")
        2 -> Color.parseColor("#f67f7f")
        3 -> Color.parseColor("#57c081")
        4 -> Color.parseColor("#7e65d6")
        else -> Color.parseColor("#00000000")
    }

    //获取显示列表数据
    fun getChartDataPro(dataType: Int, selectType: Int, dateType: Int, selectIds: String, jsonObject: JSONObject): MutableList<CommonItem<*>> {
        val list = mutableListOf<CommonItem<*>>()
        val ids = selectIds.split(",")
        val jsonArray = jsonObject.getJSONArray(ids[0])
        val item = CommonItem<Any>()
        for (i in jsonArray.indices) {
            val commonItem = CommonItem<Any>()
            for (j in ids.indices) {
                val array = jsonObject.getJSONArray(ids[j])
                if (i == 0) item.date = ""
                when (j) {
                    0 -> {
                        if (i == 0) {
                            item.name = if (ids[j] == "all") "全国" else {
                                if (selectType == 1) {
                                    array.getJSONObject(i).getString("ID")
                                } else {
                                    array.getJSONObject(i).getString("Name")
                                }
                            }
                        }
                        when (dateType) {
                            1 -> commonItem.date = "${array.getJSONObject(i).getIntValue("Day")}日"
                            2 -> commonItem.date = "${array.getJSONObject(i).getIntValue("Months")}月"
                        }
                        when (dataType) {
                            0 -> commonItem.name = array.getJSONObject(i).getString("LoanCount")
                            1 -> commonItem.name = "${Utils.div(array.getJSONObject(i).getDoubleValue("ReplyAmount"))}"
                            2 -> commonItem.name = "${Utils.div(array.getJSONObject(i).getDoubleValue("LendAmount"))}"
                            3 -> commonItem.name = "${Utils.div(array.getJSONObject(i).getDoubleValue("BackPaymentAmount"))}"
                        }
                    }
                    1 -> {
                        if (i == 0) {
                            item.content = if (ids[j] == "all") "全国" else {
                                if (selectType == 1) {
                                    array.getJSONObject(i).getString("ID")
                                } else {
                                    array.getJSONObject(i).getString("Name")
                                }
                            }
                        }
                        when (dataType) {
                            0 -> commonItem.content = array.getJSONObject(i).getString("LoanCount")
                            1 -> commonItem.content = "${Utils.div(array.getJSONObject(i).getDoubleValue("ReplyAmount"))}"
                            2 -> commonItem.content = "${Utils.div(array.getJSONObject(i).getDoubleValue("LendAmount"))}"
                            3 -> commonItem.content = "${Utils.div(array.getJSONObject(i).getDoubleValue("BackPaymentAmount"))}"
                        }
                    }
                    2 -> {
                        if (i == 0) {
                            item.hintContent = if (ids[j] == "all") "全国" else {
                                if (selectType == 1) {
                                    array.getJSONObject(i).getString("ID")
                                } else {
                                    array.getJSONObject(i).getString("Name")
                                }
                            }
                        }
                        when (dataType) {
                            0 -> commonItem.hintContent = array.getJSONObject(i).getString("LoanCount")
                            1 -> commonItem.hintContent = "${Utils.div(array.getJSONObject(i).getDoubleValue("ReplyAmount"))}"
                            2 -> commonItem.hintContent = "${Utils.div(array.getJSONObject(i).getDoubleValue("LendAmount"))}"
                            3 -> commonItem.hintContent = "${Utils.div(array.getJSONObject(i).getDoubleValue("BackPaymentAmount"))}"
                        }
                    }
                    3 -> {
                        if (i == 0) {
                            item.remark = if (ids[j] == "all") "全国" else {
                                if (selectType == 1) {
                                    array.getJSONObject(i).getString("ID")
                                } else {
                                    array.getJSONObject(i).getString("Name")
                                }
                            }
                        }
                        when (dataType) {
                            0 -> commonItem.remark = array.getJSONObject(i).getString("LoanCount")
                            1 -> commonItem.remark = "${Utils.div(array.getJSONObject(i).getDoubleValue("ReplyAmount"))}"
                            2 -> commonItem.remark = "${Utils.div(array.getJSONObject(i).getDoubleValue("LendAmount"))}"
                            3 -> commonItem.remark = "${Utils.div(array.getJSONObject(i).getDoubleValue("BackPaymentAmount"))}"
                        }
                    }
                    4 -> {
                        if (i == 0) {
                            item.payType = if (ids[j] == "all") "全国" else {
                                if (selectType == 1) {
                                    array.getJSONObject(i).getString("ID")
                                } else {
                                    array.getJSONObject(i).getString("Name")
                                }
                            }
                        }
                        when (dataType) {
                            0 -> commonItem.payType = array.getJSONObject(i).getString("LoanCount")
                            1 -> commonItem.payType = "${Utils.div(array.getJSONObject(i).getDoubleValue("ReplyAmount"))}"
                            2 -> commonItem.payType = "${Utils.div(array.getJSONObject(i).getDoubleValue("LendAmount"))}"
                            3 -> commonItem.payType = "${Utils.div(array.getJSONObject(i).getDoubleValue("BackPaymentAmount"))}"
                        }
                    }
                }
            }
            if (i == 0) list.add(item)
            list.add(commonItem)
        }
        return list
    }

    //获取进度条总数
    fun getChartDataDouble(dataType: Int, jsonArray: JSONArray): MutableList<Double> {
        val list = mutableListOf<Double>()
        for (i in jsonArray.indices) {
            when (dataType) {
                0 -> list.add(jsonArray.getJSONObject(i).getDoubleValue("LoanCount"))
                1 -> list.add(Utils.div(jsonArray.getJSONObject(i).getDoubleValue("ReplyAmount")))
                2 -> list.add(Utils.div(jsonArray.getJSONObject(i).getDoubleValue("LendAmount")))
                3 -> list.add(Utils.div(jsonArray.getJSONObject(i).getDoubleValue("BackPaymentAmount")))
            }
        }
        return list
    }

    private fun getWeekTabs(): MutableList<CommonItem<*>> {
        val list = mutableListOf<CommonItem<*>>()
        var item: CommonItem<Any>
        for (i in 0..2) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.name = "周一"
                    item.isClick = true
                }
                1 -> {
                    item.name = "周二"
                }
                2 -> {
                    item.name = "周三"
                }
            }
            list.add(item)
        }
        return list
    }

    private fun getMonthTabs(): MutableList<CommonItem<*>> {
        val list = mutableListOf<CommonItem<*>>()
        var item: CommonItem<Any>
        val total = 11
        for (i in 0..11) {
            item = CommonItem()
            when (i) {
                11 -> {
                    item.name = "本月"
                    item.isClick = true
                }
                else -> item.name = getTabText(total - i)
            }
            list.add(item)
        }
        return list
    }

    private fun getYearTabs(): MutableList<CommonItem<*>> {
        val list = mutableListOf<CommonItem<*>>()
        var item: CommonItem<Any>
        for (i in 0..2) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.name = "${DateUtil.getYear() - 2}年"
                }
                1 -> {
                    item.name = "${DateUtil.getYear() - 1}年"
                }
                2 -> {
                    item.name = "${DateUtil.getYear()}年"
                    item.isClick = true
                }
            }
            list.add(item)
        }
        return list
    }

    fun getTabText(position: Int): String {
        val sdf = SimpleDateFormat("yyyy-M月") //设置时间格式
        val calendar = Calendar.getInstance() //得到日历
        calendar.time = Date()//把当前时间赋给日历
        calendar.add(Calendar.MONTH, -position)  //设置为前3月
        return sdf.format(calendar.time)
    }

    //获取选择月份的最大天数
    fun getDayOfMonth(chooseMonth: String): Int {
        val sdf = SimpleDateFormat("yyyy-M月") //设置时间格式
        val calendar = Calendar.getInstance() //得到日历
        calendar.time = sdf.parse(chooseMonth)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getZoneName(zone: String): String {
        var name = ""
        val jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("ZoneType"), ""))
        if (jsonArray != null && jsonArray.size != 0) {
            for (i in jsonArray.indices) {
                val `object` = jsonArray.getJSONObject(i)
                if (zone == `object`.getString("Value")) {
                    name = `object`.getString("Name")
                    break
                }
            }
        }
        return name
    }

    inner class TabEntity(var title: String) : CustomTabEntity {

        override fun getTabUnselectedIcon(): Int = R.mipmap.logo

        override fun getTabSelectedIcon(): Int = R.mipmap.logo

        override fun getTabTitle(): String = title
    }
}