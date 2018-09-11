package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.view.View
import com.alibaba.fastjson.JSON
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.modelimp.PactDataMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.PactDataVInter
import com.xxjr.cfs_system.tools.DateUtil
import entity.CommonItem
import entity.PactData
import timeselector.TimesChoose

/**
 * Created by Administrator on 2017/11/24.
 */
class PactDataPresenter : BasePresenter<PactDataVInter, PactDataMImp>() {
    private var timesChoose: TimesChoose? = null
    private var page: Int = 0
    private var curDelPosition = -1
    private var state = 0
    private var startTime = ""
    private var endTime = ""

    override fun getModel(): PactDataMImp = PactDataMImp()

    override fun setDefaultValue() {
        initTimePicker()
        getPactData(0, 0, true)
    }

    private fun initTimePicker() {
        if (isViewAttached) {
            timesChoose = TimesChoose(view as Context, TimesChoose.TimeResultHandler { time, endtime ->
                startTime = "$time 00:00:00"
                endTime = "$endtime 23:59:59"
                view.setPull(false)
                getPactData(0, state, true)
            }, "1900-01-01", DateUtil.getCurDate())
            timesChoose?.setScrollUnit(TimesChoose.SCROLLTYPE.YEAR, TimesChoose.SCROLLTYPE.MONTH, TimesChoose.SCROLLTYPE.DAY)
        }
    }

    fun cleanTime() {
        startTime = ""
        endTime = ""
    }

    fun showTime(parent: View) = timesChoose?.show(parent)

    fun getPactData(page: Int, state: Int, isShow: Boolean) {
        this.page = page
        this.state = state
        getData(0, model.getParam(getPactDataList(state), "GET_LOANFILELIST"), isShow)
    }

    fun delPactData(ID: String, position: Int) {
        curDelPosition = position
        getData(1, model.getParam(getDelList(ID), "UpdateLoanFileDelMarker"), true)
    }

    private fun getDelList(ID: String): MutableList<Any> {
        val list = mutableListOf<Any>()
        list.add(ID)
        list.add("1")
        return list
    }

    private fun getPactDataList(state: Int): MutableList<Any> {
        val list = mutableListOf<Any>()
        val map = hashMapOf<Any, Any>()
        map["ContractID"] = view.getContractId()
        map["UploadStartTime"] = startTime
        map["UploadEndTime"] = endTime
        if (state == 3) {
            map["IsValid"] = "0"
        } else {
            map["HaveView"] = when (state) {
                1 -> "0"
                2 -> "1"
                else -> ""
            }//（1已查看 0未查看）
        }
        list.add(JSON.toJSONString(map))
        list.add((page + 1).toString())
        list.add("10")
        list.add("1")
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    val temp = model.getPactData(data?.data ?: "")
                    if (temp.size == 0) {
                        if (page == 0) {
                            view.getPactDatas().clear()
                            view.refreshData()
                        } else {
                            view.showMsg("没有更多的合同资料了!")
                        }
                    } else {
                        if (view.getPull()) {
                            view.getPactDatas().addAll(temp)
                        } else {
                            view.getPactDatas().clear()
                            view.getPactDatas().addAll(temp)
                        }
                        view.refreshData()
                    }
                    view.completeRefresh()
                }
                1 -> {
                    view.showMsg("文件删除成功!")
                    view.removeItem(curDelPosition)
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun getTitles(state: Int): MutableList<CommonItem<Any>> {
        val titles = mutableListOf<CommonItem<Any>>()
        var commonItem: CommonItem<Any>
        for (i in 0..3) {
            commonItem = CommonItem()
            commonItem.isClick = state == i
            when (i) {
                0 -> commonItem.name = "  全部  "
                1 -> commonItem.name = "未审核资料"
                2 -> commonItem.name = "已审核资料"
                3 -> commonItem.name = "不合规"
            }
            titles.add(commonItem)
        }
        return titles
    }

    fun getItemData(pact: PactData): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var item: CommonItem<Any>
        for (i in 0..4) {
            item = CommonItem()
            when (i) {
                0 -> {
                    item.name = "客户姓名："
                    item.content = pact.customer ?: ""
                }
                1 -> {
                    item.name = "文件类型："
                    item.content = pact.fileType ?: ""
                }
                2 -> {
                    item.name = "文件名称："
                    item.content = pact.fileName ?: ""
                    item.isLineShow = true
                }
                3 -> {
                    item.name = "文件大小："
                    item.content = pact.fileSize ?: ""
                    if (!pact.reason.isNullOrBlank()) item.isEnable = false
                }
                4 -> {
                    item.name = "上 传 者  ："
                    item.content = pact.uploader ?: ""
                    item.isLineShow = true
                }
            }
            list.add(item)
        }
        return list
    }

    fun rxDeAttach() {
        if (timesChoose != null) {
            timesChoose = null
        }
    }
}