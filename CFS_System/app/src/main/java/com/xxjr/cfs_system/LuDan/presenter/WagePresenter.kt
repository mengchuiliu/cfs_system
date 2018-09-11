package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.view.View
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.WageMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.WageVInter
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.DateUtil
import entity.ChooseType
import entity.CommonItem
import rx.Subscription
import timeselector.TimesChoose
import java.util.ArrayList

class WagePresenter : BasePresenter<WageVInter, WageMImp>() {
    private var subscription: Subscription? = null
    private var timesChoose: TimesChoose? = null
    private var date: String = ""
    private var date1: String = ""
    private var state: String? = ""
    private var page: Int = 0
    private var mortgage: String = ""

    override fun getModel(): WageMImp = WageMImp()

    override fun setDefaultValue() {
        initTimePicker()
    }

    fun getWageDetails(mortgage: String, date: String, date1: String, state: String, page: Int) {
        this.mortgage = mortgage
        this.page = page
        this.date = date
        this.date1 = date1
        this.state = state
        getData(0, model.getParam(getListParam(mortgage, date, date1, state), "GetAppMortgageList"), true)
    }

    fun confirmWage(wageId: Int) {
        getData(1, model.getParam(getConfirmListParam(wageId), "UserConfirmMortgage"), true)
    }

    fun refuseWage(wageId: Int, remark: String) {
        getData(2, model.getParam(getRefuseListParam(wageId, remark), "UserRefuseMortgage"), true)
    }

    private fun getListParam(mortgage: String, date: String, date1: String, state: String): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add(mortgage)
        list.add(date)
        list.add(date1)
        list.add(state)
        list.add(page.toString())
        list.add("10")
        return list
    }

    private fun getConfirmListParam(wageId: Int): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add(wageId)
        return list
    }

    private fun getRefuseListParam(wageId: Int, remark: String): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add(wageId)
        list.add(remark)
        return list
    }

    fun getTitles(): List<CommonItem<*>> {
        val titles = ArrayList<CommonItem<*>>()
        var commonItem: CommonItem<*>
        for (i in 0..4) {
            commonItem = CommonItem<Any>()
            when (i) {
                0 -> {
                    commonItem.isClick = true
                    commonItem.setName("员工未确认")
                }
                1 -> commonItem.setName("财务未确认")
                2 -> commonItem.setName("财务已确认")
                3 -> commonItem.setName("已发放")
            }
            titles.add(commonItem)
        }
        return titles
    }

    private fun initTimePicker() {
        if (isViewAttached) {
            subscription = RxBus.getInstance().toObservable(Constants.MORTGAGE_CODE, ChooseType::class.java).subscribe { chooseType ->
                mortgage = chooseType.id.toString()
                view.setMortgage(mortgage)
                getWageDetails(mortgage, date, date1, state ?: "", 0)
            }

            timesChoose = TimesChoose(view as Context, TimesChoose.TimeResultHandler { time, endtime ->
                date = time
                date1 = endtime
                view.setDate(date, date1)
                getWageDetails(mortgage, date, date1, state ?: "", 0)
            }, "1900-01-01", DateUtil.getCurDate())
            timesChoose?.setScrollUnit(TimesChoose.SCROLLTYPE.YEAR, TimesChoose.SCROLLTYPE.MONTH, TimesChoose.SCROLLTYPE.DAY)
        }
    }

    fun showTime(parent: View) = timesChoose?.show(parent)

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    val temp = model.getWageDetails(data?.data ?: "")
                    if (temp.size == 0) {
                        if (page == 0) {
                            view.getWageDetails().clear()
                            view.refreshData()
                        } else {
                            view.showMsg("没有更多要确认的提成!")
                        }
                    } else {
                        if (view.getPull()) {
                            view.getWageDetails().addAll(temp)
                        } else {
                            view.getWageDetails().clear()
                            view.getWageDetails().addAll(temp)
                        }
                        view.refreshData()
                    }
//                    view.completeRefresh()
                }
                1 -> {
                    view.showMsg("提成已确认")
                    getWageDetails(mortgage, date, date1, state ?: "", 0)
                }
                2 -> {
                    view.showMsg("提成已拒绝")
                    getWageDetails(mortgage, date, date1, state ?: "", 0)
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun rxDeAttach() {
        if (timesChoose != null) {
            timesChoose = null
        }
        if (subscription != null && !subscription?.isUnsubscribed!!) {
            subscription?.unsubscribe()
            subscription = null
        }
    }
}