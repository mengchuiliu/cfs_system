package com.xxjr.cfs_system.LuDan.view.activitys.borrow.presenter

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.fragments.BorrowListFragment
import entity.BorrowDetail

class ListPresenter : BasePresenter<BorrowListFragment, ModelImp>() {
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {}

    fun getBorrowItemData(searchType: Int, query1: String, query2: String, query3: String) {
        if (isViewAttached) {
            view.setIsFirst(false)
            getFragmentData(view.context, 0, model.getMoreParam(mutableListOf<Any>().apply {
                add(view.page)
                add("10")
                add("")
                add("")
                val builder = StringBuilder(" 1=1 ")
                when (searchType) {
                    1 -> builder.append(" and CustomerName like '%").append(query1).append("%'")
                    2 -> builder.append(" and ApplicantName like '%").append(query1).append("%'")
                    3 -> if (query1.isNotBlank()) builder.append(" and CompanyID in ('").append(query1).append("')")
                }
                if (query2.isNotBlank() && query3.isNotBlank()) {
                    builder.append(" and CONVERT(varchar(100),StartDate, 23) >= '").append(query2).append("'")
                    builder.append(" and CONVERT(varchar(100),StartDate, 23) <= '").append(query3).append("'")
                }
                add(builder.toString())
                add(if (view.state != 0) getQuery(view.state) else "")
            }, "BorrowContract", "GetList"), true)
        }
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.returnDataSet ?: JSONObject()).isNotEmpty()) {
                        val table2 = data?.returnDataSet?.getJSONArray("Table2") ?: JSONArray()
                        if (table2.isNotEmpty()) {
                            for (i in table2.indices) {
                                val jsonObject: JSONObject = table2.getJSONObject(i)
                                when (jsonObject.getIntValue("State")) {
                                    0 -> view.titles[0] = "全部(${jsonObject.getIntValue("Count")})"
                                    -99 -> view.titles[1] = "未提交(${jsonObject.getIntValue("Count")})"
                                    -1 -> view.titles[2] = ("资料不全(${jsonObject.getIntValue("Count")})")//-1
                                    1 -> view.titles[3] = ("已提交(${jsonObject.getIntValue("Count")})")//1
                                    2 -> view.titles[4] = ("已进件待授信(${jsonObject.getIntValue("Count")})")//2
                                    3 -> view.titles[5] = ("已授信待审批(${jsonObject.getIntValue("Count")})")//3
                                    4 -> view.titles[6] = ("审批中(${jsonObject.getIntValue("Count")})")//4
                                    5 -> view.titles[7] = ("已拆借待放款(${jsonObject.getIntValue("Count")})")//5
                                    6 -> view.titles[8] = ("已放款(${jsonObject.getIntValue("Count")})")//6
                                    -2 -> view.titles[9] = ("授信审核未通过(${jsonObject.getIntValue("Count")})")//-2
                                    -3 -> view.titles[10] = ("审批未通过(${jsonObject.getIntValue("Count")})")//-3
                                }
                            }
                            view.refreshBorrowTitle()
                        }
                        val stringArray = data?.returnDataSet?.getString("Table1") ?: ""
                        if (stringArray.isNotBlank()) {
                            val temp = JSONArray.parseArray(stringArray, BorrowDetail::class.java)
                            if (temp.size == 0) {
                                if (view.page == 0) {
                                    view.showMsg("没有拆借数据!")
                                    view.borrowLists.clear()
                                    view.refreshChange()
                                } else {
                                    view.showMsg("没有更多拆借数据!")
                                }
                            } else {
                                if (view.isPull) {
                                    view.borrowLists.addAll(temp)
                                } else {
                                    view.borrowLists.clear()
                                    view.borrowLists.addAll(temp)
                                }
                                view.refreshChange()
                            }
                            view.completeRefresh()
                        }
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    //进度
    private fun getQuery(state: Int) = when (state) {
        1 -> -99
        2 -> -1//资料不全
        3 -> 1//已提交
        4 -> 2//已进件待授信
        5 -> 3//已授信待审批
        6 -> 4//审批中
        7 -> 5//已拆借待放款
        8 -> 6//已放款
        9 -> -2//授信审核未通过
        10 -> -3//审批未通过
        else -> 0//全部
    }
}