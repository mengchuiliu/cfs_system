package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.PopupWindow
import com.alibaba.fastjson.JSON
import com.bigkoo.pickerview.TimePickerView
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.AddVisitMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.AddVisitVInter
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.PersonalInfo
import entity.VisitRecord
import rx.Subscription
import java.util.*

/**
 * Created by Administrator on 2018/3/20.
 */
class AddVisitPresenter : BasePresenter<AddVisitVInter, AddVisitMImp>() {
    private var visitor = VisitRecord()
    private var popWindow: PopupWindow? = null
    private var pvTime: TimePickerView? = null
    private var loanSubscription: Subscription? = null//贷款类型

    override fun getModel(): AddVisitMImp = AddVisitMImp()

    override fun setDefaultValue() {
        initData()
        visitor.VisitTime = DateUtil.getFormatDateHH(Date())
        visitor.CompanyId = Hawk.get<String>("CompanyID")
        visitor.SalesName = Hawk.get("UserRealName", "")
        visitor.SalesPhoneNumber = Hawk.get<PersonalInfo>("PersonalInfo").phone ?: ""
        view.initRV(model.getVisitData(visitor.SalesName ?: "", visitor.SalesPhoneNumber ?: ""))
    }

    private fun initData() {
        if (isViewAttached) {
            loanSubscription = RxBus.getInstance().toObservable(22, ChooseType::class.java).subscribe { s ->
                if (popWindow != null && popWindow?.isShowing!!) {
                    popWindow?.dismiss()
                }
                visitor.LoanType = s.id
                view.refreshItem(5, s.content)
            }

            val selectedDate = Calendar.getInstance()
            val startDate = Calendar.getInstance()
            val endDate = Calendar.getInstance()
            //正确设置方式 原因：注意事项有说明
            startDate.set(2013, 0, 1)
            pvTime = TimePickerView.Builder(view as Context, TimePickerView.OnTimeSelectListener { clickDate, tvTime ->
                //选中事件回调
                visitor.VisitTime = DateUtil.getFormatDateHH(clickDate)
                view.refreshItem(0, visitor.VisitTime ?: "")
            })
                    .setType(booleanArrayOf(true, true, true, true, true, false))// 默认全部显示
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

    fun clickChoose(position: Int, parent: View) {
        if (isViewAttached) {
            when (position) {
                0 -> pvTime?.show(parent)
                5 -> popWindow = PopChoose.showChooseType(view as Context, parent, "贷款类型",
                        Utils.getTypeDataList("LoansType"), 22, false)
            }
        }
    }

    fun editContent(position: Int, text: String) {
        if (isViewAttached) {
            when (position) {
                2 -> visitor.CustomerName = text
                3 -> visitor.MobilePhone = text
                4 -> visitor.DemandPrice = if (text.isNotBlank()) Utils.getBigLong(text.replace(",".toRegex(), "").trim { it <= ' ' }) else 0.0
                7 -> visitor.SalesPhoneNumber = text
            }
            if (position > 4) {
                view.refreshItem(0, visitor.VisitTime ?: "")
            }
        }
    }

    fun getData() {
        if (isViewAttached) {
            if (visitor.check(view as Context))
                getData(0, model.getVisitParam(getListParam()), true)
        }
    }

    private fun getListParam(): String {
        val arrayMap = hashMapOf<String, Any>()
        arrayMap["VisitTime"] = visitor.VisitTime ?: ""
        arrayMap["CompanyId"] = visitor.CompanyId ?: ""
        arrayMap["CustomerName"] = visitor.CustomerName ?: ""
        arrayMap["MobilePhone"] = visitor.MobilePhone ?: ""
        arrayMap["DemandPrice"] = visitor.DemandPrice ?: 0.0
        arrayMap["LoanType"] = visitor.LoanType ?: -1
        arrayMap["SalesName"] = visitor.SalesName ?: ""//业务员姓名
        arrayMap["SalesPhoneNumber"] = visitor.SalesPhoneNumber ?: "" //业务员电话
        arrayMap["ServicePeople"] = Hawk.get<String>("UserID")
        return JSON.toJSONString(arrayMap)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            view.complete()
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun rxDeAttach() {
        if (popWindow != null) {
            popWindow?.dismiss()
            popWindow = null
        }
        if (loanSubscription != null && !loanSubscription!!.isUnsubscribed) {
            loanSubscription!!.unsubscribe()
        }
    }
}