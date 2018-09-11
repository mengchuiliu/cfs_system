package com.xxjr.cfs_system.LuDan.view.activitys.visit_record.presenter

import android.content.Context
import android.graphics.Color
import android.view.View
import com.bigkoo.pickerview.TimePickerView
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.activitys.visit_record.AddInvitationActivity
import com.xxjr.cfs_system.tools.DateUtil
import entity.CommonItem
import entity.PersonalInfo
import java.util.*

class AddInvitationPresenter : BasePresenter<AddInvitationActivity, ModelImp>() {
    private var pvTime: TimePickerView? = null
    private var customerName = ""
    private var phone = ""
    private var visitTime = ""
    private var remark = ""

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        initDate()
    }

    private fun initDate() {
        if (isViewAttached) {
            val selectedDate = Calendar.getInstance()
            val startDate = Calendar.getInstance()
            val endDate = Calendar.getInstance()
            //正确设置方式 原因：注意事项有说明
            startDate.set(2013, 0, 1)
            endDate.set(2100, 11, 30)
            pvTime = TimePickerView.Builder(view as Context, TimePickerView.OnTimeSelectListener { clickDate, tvTime ->
                //选中事件回调
                visitTime = DateUtil.getFormatDateHH(clickDate)
                view.refreshItem(2, visitTime)
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

    fun timeShow(parent: View) {
        pvTime?.show(parent)
    }

    fun editContent(position: Int, text: String) {
        when (position) {
            0 -> customerName = text
            1 -> phone = text
            3 -> remark = text
        }
    }

    fun submit() {
        if (isViewAttached) {
            when {
                customerName.isBlank() -> view.showMsg("客户名不能为空！")
                phone.isBlank() -> view.showMsg("手机号不能为空！")
                phone.length != 11 -> view.showMsg("手机号不能少于11位!")
                visitTime.isBlank() -> view.showMsg("请选择预约上门时间！")
                else -> {
                    getData(0, model.getMoreParam(mutableListOf<Any>().apply {
                        add(customerName)
                        add(phone)
                        add(visitTime)
                        add(remark)
                    }, "Invitation", "Add"), true)
                }
            }
        }
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> view.complete()
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) {
    }

    fun getItemData() = mutableListOf<CommonItem<Any>>().apply {
        for (i in 0..7) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> {
                        type = 3
                        name = "客户名称"
                    }
                    1 -> {
                        type = 3
                        name = "客户手机"
                        isClick = true
                    }
                    2 -> {
                        type = 1
                        name = "预约上门时间"
                        isClick = true
                    }
                    3 -> {
                        type = 4
                        name = "备注"
                        position = 500
                    }
                    4 -> {
                        type = 0
                        position = 1
                        icon = R.color.store_2
                        isLineShow = true
                    }
                    5 -> {
                        type = 1
                        name = "业务员"
                        content = Hawk.get<String>("UserRealName", "")
                    }
                    6 -> {
                        type = 1
                        name = "业务员手机号"
                        content = Hawk.get<PersonalInfo>("PersonalInfo").phone ?: ""
                    }
                    7 -> {
                        type = 1
                        name = "所属门店"
                        content = Hawk.get<String>("CompanyName", "")
                    }
                }
            })
        }
    }
}