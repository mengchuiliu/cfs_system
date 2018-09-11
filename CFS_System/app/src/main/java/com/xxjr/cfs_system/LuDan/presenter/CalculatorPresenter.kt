package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.PopupWindow
import com.bigkoo.pickerview.TimePickerView
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.loan_calculator.CalculatorActivity
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.CommonItem
import entity.Reimburse
import rx.Subscription
import java.math.BigDecimal
import java.util.*

class CalculatorPresenter : BasePresenter<CalculatorActivity, ModelImp>() {
    private lateinit var pvTime: TimePickerView
    private var popWindow: PopupWindow? = null
    private var subscription: Subscription? = null
    private var position = -1

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        if (isViewAttached) {
            subscription = RxBus.getInstance().toObservable(11, ChooseType::class.java).subscribe { chooseType ->
                if (popWindow != null && popWindow?.isShowing!!) {
                    popWindow?.dismiss()
                }
                view.setReimburseType(chooseType.id)
                view.refreshItem(chooseType.content, position)
            }

            val selectedDate = Calendar.getInstance()
            val startDate = Calendar.getInstance()
            val endDate = Calendar.getInstance()
            //正确设置方式 原因：注意事项有说明
            startDate.set(2013, 0, 1)
            endDate.set(2060, 11, 31)

            pvTime = TimePickerView.Builder(view as Context, TimePickerView.OnTimeSelectListener { date, tvTime ->
                //选中事件回调
                if (position != -1) {
                    view.refreshItem(DateUtil.getFormatDate(date), position)
                }
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

    //时间选择
    fun timeShow(pos: Int) {
        if (isViewAttached) {
            this.position = pos
            pvTime.show()
        }
    }

    fun showReimburseType(parent: View, pos: Int) {
        if (isViewAttached) {
            this.position = pos
            popWindow = PopChoose.showChooseType(view as Context, parent, "还息方式",
                    mutableListOf<ChooseType>().apply {
                        for (i in 0..2) {
                            add(ChooseType().apply {
                                id = i
                                when (i) {
                                    0 -> content = "按月还息"
                                    1 -> content = "按季还息"
                                    2 -> content = "到期还本"
                                }
                            })
                        }
                    },
                    11, false)
        }
    }

    fun getTabTitle() = mutableListOf<String>().apply {
        add("等额本息")
        add("等额本金")
        add("到期还本")
    }

    //0,1->等额本金等额本息  2->到期还本
    fun getItemData(loanType: Int, amount: Double, mouths: Int, rate: Double, otherAmount: Double) = when (loanType) {
        0, 1 -> getEqualData(amount, mouths, rate, otherAmount)
        2 -> getDueData(amount, rate, otherAmount)
        else -> getEqualData(amount, mouths, rate, otherAmount)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {}

    override fun onFailed(resultCode: Int, msg: String?) {}

    //获取等额本金和等额本息item
    private fun getEqualData(amount: Double, mouths: Int, rate: Double, otherAmount: Double) = mutableListOf<CommonItem<Any>>().apply {
        for (i in 0..6) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> {
                        type = 1
                        name = "贷款金额（万）"
                        icon = 11
                        content = if (amount == 0.0) "" else Utils.parseTwoMoney(BigDecimal(amount))
                    }
                    1 -> {
                        type = 1
                        name = "贷款期限（月）"
                        isLineShow = true
                        icon = 12
                        content = if (mouths == 0) "" else mouths.toString()
                    }
                    2 -> {
                        type = 1
                        name = "年利率（%）"
                        icon = 13
                        content = if (rate == 0.0) "" else Utils.parseTwoMoney(BigDecimal(rate))
                    }
                    3 -> {
                        type = 1
                        name = "其他月供（元）"
                        icon = 19
                        content = if (otherAmount == 0.0) "" else Utils.parseTwoMoney(BigDecimal(otherAmount))
                    }
                    4 -> {
                        type = 2
                        name = "《贷款利率参考表》"
                        icon = 14
                    }
                    5 -> {
                        type = 3
                        name = "开始计算"
                        icon = 15
                    }
                    6 -> type = 0
                }
            })
        }
    }

    //到期还本item
    private fun getDueData(amount: Double, rate: Double, otherAmount: Double) = mutableListOf<CommonItem<Any>>().apply {
        for (i in 0..8) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> {
                        type = 1
                        name = "贷款金额（万）"
                        icon = 11
                        content = if (amount == 0.0) "" else Utils.parseTwoMoney(BigDecimal(amount))
                    }
                    1 -> {
                        type = 1
                        name = "借款日期"
                        hintContent = "点击选择日期"
                        icon = 16
                        isClick = true
                    }
                    2 -> {
                        type = 1
                        name = "还款日期"
                        hintContent = "点击选择日期"
                        icon = 17
                        isClick = true
                    }
                    3 -> {
                        type = 1
                        name = "年利率（%）"
                        icon = 13
                        content = if (rate == 0.0) "" else Utils.parseTwoMoney(BigDecimal(rate))
                    }
                    4 -> {
                        type = 1
                        name = "其他月供（元）"
                        icon = 19
                        content = if (otherAmount == 0.0) "" else Utils.parseTwoMoney(BigDecimal(otherAmount))
                    }
                    5 -> {
                        type = 4
                        name = "还息方式"
                        icon = 18
                    }
                    6 -> {
                        type = 2
                        name = "《贷款利率参考表》"
                        icon = 14
                    }
                    7 -> {
                        type = 3
                        name = "开始计算"
                        icon = 15
                    }
                    8 -> {
                        type = 2
                        content = "提示：默认还款日为21日"
                        isLineShow = true
                    }
                }
            })
        }
    }

    //获取账单
    fun getDetailsItem(commonItems: MutableList<CommonItem<Any>>, reimburses: MutableList<Reimburse>, other: Double): MutableList<CommonItem<Any>> {
        val items = mutableListOf<CommonItem<Any>>()
        items.addAll(commonItems)
        items.add(CommonItem<Any>().apply {
            type = 5
            name = "累计支付利息（元）："
            content = if (reimburses.isNotEmpty()) Utils.parseTwoMoney(BigDecimal(reimburses[0].cumulativeInterests)) else "0.0"
        })
        items.add(CommonItem<Any>().apply {
            type = 5
            name = "累计还款总额（元）："
            content = if (reimburses.isNotEmpty()) Utils.parseTwoMoney(BigDecimal(reimburses[0].cumulativeAmount)) else "0.0"
        })
        items.add(CommonItem<Any>().apply {
            type = 0
            isLineShow = true
        })
        items.add(CommonItem<Any>().apply {
            type = 6
            name = "日期"
            content = "本息（元）"
            payType = "剩余本金（元）"
        })
        for (reimburse in reimburses) {
            items.add(CommonItem<Any>().apply {
                type = 6
                isLineShow = true
                name = reimburse.countDate
                content = Utils.parseTwoMoney(BigDecimal((reimburse.reimburseAmount + other)))
                hintContent = "含利息${Utils.parseTwoMoney(BigDecimal(reimburse.interests))}"
                payType = Utils.parseTwoMoney(BigDecimal(reimburse.remainAmount))
                remark = "含其他月供${Utils.parseTwoMoney(BigDecimal(other))}"
            })
        }
        items.add(CommonItem<Any>().apply { type = 0 })
        return items
    }

    fun rxDeAttach() {
        if (subscription != null && !subscription!!.isUnsubscribed) {
            subscription!!.unsubscribe()
        }
    }
}