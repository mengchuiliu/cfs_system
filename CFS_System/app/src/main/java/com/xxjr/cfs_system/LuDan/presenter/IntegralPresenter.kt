package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.MortgageScoreMImp
import com.xxjr.cfs_system.LuDan.view.activitys.mortgage_score.IntegralDetailActivity
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import rx.Subscription

/**
 * Created by Administrator on 2018/3/8.
 */
class IntegralPresenter : BasePresenter<IntegralDetailActivity, MortgageScoreMImp>() {
    override fun getModel(): MortgageScoreMImp = MortgageScoreMImp()

    override fun setDefaultValue() {
        getIntegralData()
    }

    //获取数据
    fun getIntegralData() {
        getData(0, model.getParam(getListParam(), "SumMortgagePoint"), true)
//        getData(0, model.getScoreParam(getIntegralListParam()), true)
    }

    private fun getListParam(): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add("${view.getYearMonth()}-01")
        list.add("${view.getYearMonth()}-01")
        list.add(view.getUserID())
        return list
    }
    //查详情
//    private fun getIntegralListParam(): MutableList<Any> {
//        val list = mutableListOf<Any>()
//        list.add("")
//        list.add("t_mortgage_integral_detail")
//        list.add("")
//        list.add("")//主键字段
//        list.add("*")//UserID  UserRealName
//        list.add("UserID = '${view.getUserID()}' and YearMonth = '${view.getYearMonth()}' ${if (integralId == 0) "" else "and ScoreType = '$integralId'"}")
//        list.add("UpdateTime")//排序字段
//        list.add("1")//排序 0->asc 1->desc
//        list.add("10")//条数
//        list.add(page.toString())//页数
//        list.add("")
//        list.add("0")//0->代表分页 1->代表不分页
//        return list
//    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    val temp = model.getScoresData(data?.data ?: "", 20)
                    view.getScores().addAll(temp)
                    view.refreshData()
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
}