package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.MortagageRMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.MortagageRVInter
import com.xxjr.cfs_system.tools.DateUtil
import com.bigkoo.pickerview.TimePickerView
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.tools.Utils
import entity.*
import rx.Subscription
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Administrator on 2017/12/12.
 */
class MortagageRPresenter : BasePresenter<MortagageRVInter, MortagageRMImp>() {
    private var stores: MutableList<CommonItem<Any>>? = null
    private var mortagages: MutableList<CommonItem<Any>>? = null
    private var type = 0 //0->门店 1->按揭员
    private var pvTime: TimePickerView? = null
    private var chooseTime = DateUtil.getFormatDate(Date())
    private var popWindow: PopupWindow? = null
    private var subscription: Subscription? = null
    private var zones: String = ""
    private var companyZone: String = ""


    override fun getModel(): MortagageRMImp = MortagageRMImp()

    override fun setDefaultValue() {
        zones = Hawk.get<String>("ZonePowers", "")
        initTimeChoose()
        getReportData()
    }

    fun getReportData() {
        getData(0, model.getParam(getListParam(), "StoreLoanStatistics"), true)
    }

    private fun getListParam(): MutableList<Any> {
        val list = mutableListOf<Any>()
        val map = hashMapOf<Any, Any>()
        map.put("MethondName", "GetMortgageLoanLendingStatistics")
        map.put("CompanyZone_id", companyZone)
        map.put("StartDate", "$chooseTime 00:00:00")
        map.put("EndDate", "$chooseTime 23:59:59")
        list.add(JSON.toJSONString(map))
        if (Hawk.get<String>("UserType") == "22") {
            list.add(Hawk.get("UserID"))
        } else {
            list.add("")
        }
        return list
    }

    fun setType(type: Int) {
        this.type = type
        when (type) {
            0 -> view.refreshData(stores ?: ArrayList())
            1 -> view.refreshData(mortagages ?: ArrayList())
        }
    }

    fun timeShow(textView: TextView) {
        if (isViewAttached) {
            pvTime?.show(textView)
        }
    }

    fun showZone(parent: View) {
        if (isViewAttached) {
            if (popWindow == null)
                popWindow = PopChoose.showChooseType(view as Context, parent, "所属地区", model.getZoneDataList(zones), 1, false)
            else popWindow?.showAtLocation(parent, Gravity.CENTER, 0, 0)
        }
    }

    private fun initTimeChoose() {
        if (isViewAttached) {
            subscription = RxBus.getInstance().toObservable(1, ChooseType::class.java).subscribe { s ->
                if (popWindow != null && popWindow?.isShowing!!) {
                    popWindow?.dismiss()
                    view.setZoneText(s.content)
                    companyZone = s.ids
                    getReportData()
                }
            }

            val selectedDate = Calendar.getInstance()
            val startDate = Calendar.getInstance()
            val endDate = Calendar.getInstance()
            //正确设置方式 原因：注意事项有说明
            startDate.set(2013, 0, 1)
//        endDate.set(2020, 11, 31)

            pvTime = TimePickerView.Builder(view as Context, TimePickerView.OnTimeSelectListener { date, tvTime ->
                //选中事件回调
                (tvTime as TextView).text = Utils.FormatTime(DateUtil.getFormatDate(date), "yyyy-MM-dd", "yyyy/MM/dd")
                chooseTime = DateUtil.getFormatDate(date)
                getReportData()
            })
                    .setType(booleanArrayOf(true, true, true, false, false, false))// 默认全部显示
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

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            if (Hawk.get<String>("UserType") == "22") {
                view.refreshData(model.getMortgages(data?.data ?: ""))
            } else {
                if (data?.returnDataSet != null) {
                    stores = model.getStoresDatas(data.returnDataSet.getJSONArray("CompanyLoanLending"))
                    mortagages = model.getMortgageDatas(data.returnDataSet.getJSONArray("MortgageLoanLending"))
                    when (type) {
                        0 -> view.refreshData(stores ?: ArrayList())
                        1 -> view.refreshData(mortagages ?: ArrayList())
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun rxDeAttach() {
        if (subscription != null && !subscription?.isUnsubscribed!!) {
            subscription?.unsubscribe()
            subscription = null
        }
    }
}