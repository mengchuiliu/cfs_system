package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.widgets.PopWindow
import com.xxjr.cfs_system.LuDan.model.modelimp.BindBankMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.BindBankVInter
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.tools.Utils
import entity.Bank

class BindBankPresenter : BasePresenter<BindBankVInter, BindBankMImp>() {
    private var popWindow: PopupWindow? = null
    private var bank: Bank? = null

    override fun getModel(): BindBankMImp = BindBankMImp()

    override fun setDefaultValue() {
        getBankCardsData()
    }

    fun getBankCardsData() {
        getData(0, model.getParam(getListParam(), "ManageUserBankCard", "GET"), true)
    }

    fun updateBankCardData() {
        getData(1, model.getParam(getUpListParam(bank?.userID!!, bank?.bankCode!!), "ManageUserBankCard", "UPD"), true)
    }

    fun unBindBankCard() {
        getData(2, model.getParam(getDelListParam(bank?.bankCode!!), "ManageUserBankCard", "DEL"), true)
    }

    private fun getListParam(): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        list.add(Hawk.get("UserID"))
        return list
    }

    private fun getUpListParam(userID: Int, account: String): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        list.add(true)
        list.add(userID)
        list.add(account)
        return list
    }

    private fun getDelListParam(account: String): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        list.add(account)
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if (data?.data == null) {
                        view.initRecycler(null)
                    } else {
                        val jsonArray = JSON.parseArray(data.data)
                        view.initRecycler(model.getCardsData(jsonArray))
                    }
                }
                1 -> getBankCardsData()
                2 -> getBankCardsData()
            }
        }

    }

    override fun onFailed(resultCode: Int, msg: String?) {
        view.showMsg(msg)
    }

    fun showBankClick(parent: View, bank: Bank?) {
        this.bank = bank
        if (popWindow == null) {
            popWindow = PopWindow.showBankPop(view as Context, onClickListener, parent)
        } else {
            popWindow?.showAtLocation(parent, Gravity.CENTER, 0, 0)
        }
    }

    private val onClickListener = View.OnClickListener { p0 ->
        if (popWindow != null && popWindow?.isShowing()!!) {
            popWindow?.dismiss()
        }
        when (p0.id) {
            R.id.photograph -> updateBankCardData()
            R.id.albums -> unBindBankCard()
        }
    }

    fun rxDeAttach() {
        if (popWindow != null && popWindow?.isShowing()!!) {
            popWindow?.dismiss()
            popWindow = null
        }
    }
}