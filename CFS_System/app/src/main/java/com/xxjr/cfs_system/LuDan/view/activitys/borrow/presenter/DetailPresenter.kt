package com.xxjr.cfs_system.LuDan.view.activitys.borrow.presenter

import com.alibaba.fastjson.JSONArray
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.activitys.borrow.model.DetailMImp
import com.xxjr.cfs_system.LuDan.view.activitys.borrow.view.DetailActivity
import entity.BorrowCustomer
import entity.CommonItem
import entity.PactData

class DetailPresenter : BasePresenter<DetailActivity, DetailMImp>() {
    private var fileData = mutableListOf<CommonItem<Any>>()
    private var borrowCustomer: BorrowCustomer? = null

    override fun getModel(): DetailMImp = DetailMImp()

    override fun setDefaultValue() {
        if (isViewAttached) {
            getData(0, model.getMoreParam(mutableListOf<Any>().apply {
                add(view.borrowDetail.CustomerId)
            }, "BorrowCustomer", "Get"), true)
        }
    }

    //拆借审核
    fun borrowAudit(isPass: Boolean, remark: String) {
        if (isViewAttached) {
            getData(1, model.getMoreParam(mutableListOf<Any>().apply {
                add(view.borrowDetail.Id)
                add(view.borrowDetail.State)
                add(isPass) //通过-True，不通过-False
                add(remark)
                add("4")//访问来源，【1：PC客户端，2:Web前端,3:iOS,4:Android】
            }, "BorrowContract", "Audit"), true)
        }
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.data ?: "").isNotBlank()) {
                        val customers = JSONArray.parseArray(data?.data
                                ?: "", BorrowCustomer::class.java)
                        borrowCustomer = customers[0]
                        view.refreshData(getItemData(false, true, true))
                        //获取影像；列表
                        getData(2, model.getMoreParam(mutableListOf<Any>().apply {
                            add(view.borrowDetail.Id)
                        }, "BorrowImage", "GetList"), true)
                    }
                }
                1 -> view.complete()
                2 -> {
                    if ((data?.data ?: "").isNotBlank()) {
                        val jsonArray = JSONArray.parseArray(data?.data ?: "")
                        if (jsonArray.isNotEmpty()) {
                            for (i in jsonArray.indices) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                fileData.add(CommonItem<Any>().apply {
                                    type = 7
                                    isClick = true
                                    item = PactData().apply {
                                        fileName = jsonObject.getString("FileName") ?: ""
                                        fileSize = jsonObject.getString("Size") ?: ""
                                        remark = jsonObject.getString("Remark") ?: ""
                                        fileGuid = jsonObject.getString("FileGuid") ?: ""
                                        customer = jsonObject.getString("CustomerName") ?: ""
                                        fileType = jsonObject.getString("TypeName") ?: ""
                                        uploader = jsonObject.getString("UploaderName") ?: ""
                                    }
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)
    fun getItemData(isPersonShrink: Boolean, isBorrowShrink: Boolean, isFileShrink: Boolean) = mutableListOf<CommonItem<Any>>().apply {
        addAll(model.getItemData(isPersonShrink, isBorrowShrink, isFileShrink, borrowCustomer
                ?: BorrowCustomer(), view.borrowDetail, fileData))
    }
}