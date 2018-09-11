package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.bigkoo.pickerview.TimePickerView
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.GoldDetailMImp
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.DetailActivity
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import java.util.*

/**
 * Created by Administrator on 2018/3/13.
 */
class GoldDetailPresenter : BasePresenter<DetailActivity, GoldDetailMImp>() {
    private var pvTime: TimePickerView? = null
    //    private var maxDay = 0
    private var date = DateUtil.formatChooseDate(Date())

    override fun getModel(): GoldDetailMImp = GoldDetailMImp()

    override fun setDefaultValue() {
        initTimer()
//        maxDay = getCurrentMonthDay(Date())
        getDetailsData()
    }

    private fun initTimer() {
        if (isViewAttached) {
            val selectedDate = Calendar.getInstance()
            val startDate = Calendar.getInstance()
            val endDate = Calendar.getInstance()
            //正确设置方式 原因：注意事项有说明
            startDate.set(2013, 0, 1)
            pvTime = TimePickerView.Builder(view as Context, TimePickerView.OnTimeSelectListener { clickDate, tvTime ->
                //选中事件回调
                date = DateUtil.formatChooseDate(clickDate)
//                maxDay = getCurrentMonthDay(clickDate)
                (tvTime as TextView).text = "${DateUtil.getChooseDate(clickDate)}明细"
                getDetailsData()
            })
                    .setType(booleanArrayOf(true, true, false, false, false, false))// 默认全部显示
                    .setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setContentSize(18)//滚轮文字大小
                    .setTitleText("选择时间")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .setTitleColor(Color.BLACK)//标题文字颜色
                    .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
                    .setSubmitColor((view as Context).resources.getColor(R.color.font_home))//确定按钮文字颜色
                    .setCancelColor((view as Context).resources.getColor(R.color.font_home))//取消按钮文字颜色
                    .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(startDate, endDate)//起始终止年月日设定
                    .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(false)//是否显示为对话框样式
                    .build()
        }
    }

    fun showTime(parent: View) {
        if (isViewAttached)
            pvTime?.show(parent)
    }

    fun getDetailsData() {
        getData(0, model.getDataParam(getDetailsParam(), "GoldAccountShZhQry"), true)
    }

    private fun getDetailsParam(): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add(view.getUserNo())
        list.add("${date}-01 00:00:00")
        list.add(Hawk.get<String>("UserID"))
        list.add(Hawk.get<String>("CompanyID"))
        list.add(4)
//        list.add("${date}${if (maxDay < 10) "0$maxDay" else maxDay}")
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            if ((data?.returnString ?: "").isNotBlank()) {
                val jsonArray = JSON.parseArray(data?.returnString ?: "")
                if (jsonArray != null && jsonArray.size != 0) {
                    view.initRv(model.getDetailData(jsonArray))
                } else {
                    view.initRv(mutableListOf())
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    /**
     * 获取当月的 天数
     */
//    fun getCurrentMonthDay(date: Date): Int {
//        val a = Calendar.getInstance()
//        a.time = date
//        a.set(Calendar.DATE, 1)
//        a.roll(Calendar.DATE, -1)
//        val maxDate = a.get(Calendar.DATE)
//        return maxDate
//    }
}