package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.PopupWindow
import com.bigkoo.pickerview.TimePickerView
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.MortgageScoreMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.MortgageScoreVInter
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.DateUtil
import entity.ChooseType
import entity.CommonItem
import rx.Subscription
import timeselector.TimesChoose
import java.util.*

/**
 * Created by Administrator on 2018/3/7.
 */
class MortgageScoreP : BasePresenter<MortgageScoreVInter, MortgageScoreMImp>() {
    private var subscription: Subscription? = null
    private var remarkSubscription: Subscription? = null
    private var timesChoose: TimesChoose? = null
    private var pvTime: TimePickerView? = null
    private var popWindow: PopupWindow? = null
    private var mortgageId: String = ""
    private var date: String = ""
    private var date1: String = ""
    private var page: Int = 0
    private var type: Int = 0
    private var remarkType: Int = 0

    override fun getModel(): MortgageScoreMImp = MortgageScoreMImp()

    override fun setDefaultValue() {
        initData()
    }

    private fun initData() {
        if (isViewAttached) {
            subscription = RxBus.getInstance().toObservable(Constants.MORTGAGE_CODE, ChooseType::class.java).subscribe { chooseType ->
                mortgageId = chooseType.id.toString()
                view.setMortgageId(mortgageId)
                getScoreData(mortgageId, date, date1, 0, type)
            }

            remarkSubscription = RxBus.getInstance().toObservable(11, ChooseType::class.java).subscribe { chooseType ->
                if (popWindow != null && popWindow?.isShowing!!) {
                    popWindow?.dismiss()
                }
                remarkType = chooseType.id
                view.setPull(false)
                getScoreData(mortgageId, date, date1, 0, type)
            }

            timesChoose = TimesChoose(view as Context, TimesChoose.TimeResultHandler { time, endtime ->
                date = time
                date1 = endtime
                view.setDateView(date, date1)
                getScoreData(mortgageId, date, date1, 0, type)
            }, "1900-01-01", DateUtil.getCurDate())
            timesChoose?.setScrollUnit(TimesChoose.SCROLLTYPE.YEAR, TimesChoose.SCROLLTYPE.MONTH, TimesChoose.SCROLLTYPE.DAY)

            val selectedDate = Calendar.getInstance()
            val startDate = Calendar.getInstance()
            val endDate = Calendar.getInstance()
            //正确设置方式 原因：注意事项有说明
            startDate.set(2013, 0, 1)
            pvTime = TimePickerView.Builder(view as Context, TimePickerView.OnTimeSelectListener { clickDate, tvTime ->
                //选中事件回调
                date = DateUtil.formatChooseDate(clickDate)
                date1 = ""
                view.setDateView(date, date1)
                getScoreData(mortgageId, date, date1, 0, type)
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

    fun setRemarkType(remarkType: Int) {
        this.remarkType = remarkType
    }

    fun showTime(parent: View) {
        if (isViewAttached)
            if (type == 0) pvTime?.show(parent) else timesChoose?.show(parent)
    }

    fun showRemark(parent: View) {
        popWindow = PopChoose.showChooseType(view as Context, parent, "备注筛选",
                model.getTypeDataList(), 11, false)
    }

    fun getScoreData(mortgageId: String, date: String, date1: String, page: Int, type: Int) {
        this.mortgageId = mortgageId
        this.page = page
        this.date = date
        this.date1 = date1
        this.type = type
        when (type) {
            0 -> getData(type, model.getParam(getMortgageScoreListParam(), "GetMortgagePoints"), true)
            1 -> getData(type, model.getScoreParam(getScoreListParam()), true)
            2 -> getData(type, model.getParam(getCountMortgageEvaluationParam(), "CountMortgageEvaluation"), true)
        }
    }

    private fun getMortgageScoreListParam(): MutableList<Any> {
        if (date.isBlank()) date = DateUtil.formatChooseDate(Date())
        val list = mutableListOf<Any>()
        list.add("1")//0->不分页  1->分页
        list.add("$date-01")//时间
        val where = StringBuilder()
        where.append(" 1=1 ")
        if (mortgageId.isNotBlank()) {
            where.append(" and ").append("UserId").append("=").append(mortgageId)
        }
        where.append(" and ").append("YearMonth").append("=").append("'").append(date).append("'")
        list.add(where.toString())//条件
        list.add(page.toString())
        list.add("10")
        list.add(" YearMonth desc ")//排序
        return list
    }

    //获取服务评分
    private fun getScoreListParam(): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add("")
        list.add("t_mortgate_evaluation")//总结分和服务评分
        list.add("")
        list.add("")//主键字段
        list.add("*")//UserID  UserRealName
        val where = StringBuilder()
        if (mortgageId.isNotBlank()) {
            where.append("Mortgage").append("=").append(mortgageId)
            if ((date.isNotBlank() && date1.isNotBlank()) || remarkType != 0) where.append(" and ")
        }
        if (date.isNotBlank() && date1.isNotBlank()) {
            where.append("UpdateTime").append(">=").append("'").append(date).append("'")
            where.append(" and ")
            where.append("UpdateTime").append("<=").append("'").append(date1).append("'")
            if (remarkType != 0) {
                where.append(" and ")
            }
        }
        when (remarkType) {
            1 -> where.append(" Remark <> '' ")
            2 -> where.append(" Remark ='' ")
        }
        list.add(where.toString())
        //排序字段
        list.add("Score")
        //排序 0->asc 1->desc
        list.add("0")
        list.add("10")//条数
        list.add(page.toString())//页数
        list.add("")
        list.add("0")//0->代表分页 1->代表不分页
        return list
    }

    //获取服务评分统计
    private fun getCountMortgageEvaluationParam(): MutableList<Any> {
        return mutableListOf<Any>().apply {
            val where = StringBuilder()
            if (mortgageId.isNotBlank()) {
                where.append("UserId").append("=").append(mortgageId)
            }
            add(where.toString())
            add(page.toString())
            add("10")
            add(" AvgScore DESC,UserID DESC ")
            add(if (date.isBlank()) "${DateUtil.formatChooseDate(Date())}-01" else date)
            add(if (date1.isBlank()) "${DateUtil.formatChooseDate(Date())}-${DateUtil.getCurrentMonthDay()}" else date1)
        }
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0, 1, 2 -> {
                    val temp = model.getScoresData(data?.data ?: "", type)
                    if (temp.size == 0) {
                        if (page == 0) {
                            view.getScores().clear()
                            view.refreshData()
                        } else {
                            view.showMsg("没有更多数据!")
                        }
                    } else {
                        if (view.getPull()) {
                            view.getScores().addAll(temp)
                        } else {
                            view.getScores().clear()
                            view.getScores().addAll(temp)
                        }
                        view.refreshData()
                    }
                    view.completeRefresh()
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun getTitles(): List<CommonItem<*>> {
        val titles = ArrayList<CommonItem<*>>()
        var commonItem: CommonItem<*>
        for (i in 0..2) {
            commonItem = CommonItem<Any>()
            when (i) {
                0 -> {
                    commonItem.isClick = true
                    commonItem.setName("按揭积分")
                }
                1 -> commonItem.setName("服务评分")
                2 -> commonItem.setName("服务评分统计")
            }
            titles.add(commonItem)
        }
        return titles
    }

    fun rxDeAttach() {
        if (timesChoose != null) {
            timesChoose = null
        }
        if (popWindow != null) {
            popWindow?.dismiss()
            popWindow = null
        }
        if (subscription != null && !subscription?.isUnsubscribed!!) {
            subscription?.unsubscribe()
            subscription = null
        }
        if (remarkSubscription != null && !remarkSubscription?.isUnsubscribed!!) {
            remarkSubscription?.unsubscribe()
            remarkSubscription = null
        }
    }
}