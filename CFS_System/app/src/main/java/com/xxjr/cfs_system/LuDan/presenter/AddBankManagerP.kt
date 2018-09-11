package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import com.alibaba.fastjson.JSON
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.AddBankManagerMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.AddBankManagerVInter
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.CommonItem
import rx.Subscription

/**
 * Created by Administrator on 2017/12/1.
 */
class AddBankManagerP : BasePresenter<AddBankManagerVInter, AddBankManagerMImp>() {
    private var popWindow: PopupWindow? = null
    private var bankSubscription: Subscription? = null
    private var subscription: Subscription? = null

    override fun getModel(): AddBankManagerMImp = AddBankManagerMImp()

    override fun setDefaultValue() {
        initSubscription()
        view.initRv(model.getAddData(view.getManager()))
    }

    private fun initSubscription() {
        bankSubscription = RxBus.getInstance().toObservable(Constants.BANK_CODE, ChooseType::class.java).subscribe { chooseType ->
            view.getManager()?.belongBankId = chooseType.id
            view.getManager()?.belongBankName = chooseType.content
            view.refreshItem(1, chooseType.content)
        }

        subscription = RxBus.getInstance().toObservable(1, ChooseType::class.java).subscribe { s ->
            if (popWindow != null && popWindow?.isShowing!!) {
                popWindow?.dismiss()
                view.getManager()?.zoneId = s.id
                view.refreshItem(6, s.content)
            }
        }
    }

    fun showZone(parent: View) {
        if (isViewAttached) {
            if (popWindow == null)
                popWindow = PopChoose.showChooseType(view as Context, parent, "所属地区",
                        Utils.getTypeDataList("ZoneType"), 1, false)
            else popWindow?.showAtLocation(parent, Gravity.CENTER, 0, 0)
        }
    }

    fun save() {
        when {
            view.getManager()?.bankManagerName.isNullOrBlank() -> {
                view.showMsg("银行经理名称不能为空")
                return
            }
            (view.getManager()?.belongBankId ?: 0) == 0 -> {
                view.showMsg("请选择所属银行")
                return
            }
            view.getManager()?.phone1.isNullOrBlank() -> {
                view.showMsg("联系电话1不能为空")
                return
            }
            (view.getManager()?.zoneId ?: 0) == 0 -> {
                view.showMsg("所属地区不能为空")
                return
            }
        }
        getData(0, model.getParam(getListParam(), "ADDORUPDATE_LOANBANKMANAGERMETHOD"), true)
    }

    private fun getListParam(): MutableList<Any> {
        val list: MutableList<Any> = ArrayList()
        val map = hashMapOf<Any, Any>()
        map.put("ID", view.getManager()?.id ?: -1)
        map.put("BankManagerName", view.getManager()?.bankManagerName ?: "")
        map.put("BelongBank", view.getManager()?.belongBankId ?: 0)
        map.put("DoLoanProduct", 0)
        map.put("Phone1", view.getManager()?.phone1 ?: "")
        map.put("Phone2", view.getManager()?.phone2 ?: "")
        map.put("Rate", 0)
        map.put("OrdersAbility", 0)
        map.put("ExistingSingular", 0)
        map.put("BranchBankName", view.getManager()?.branchBankName ?: "")
        map.put("RecommendedName", view.getManager()?.recommendedName ?: "")
        map.put("Zone", view.getManager()?.zoneId ?: 0)
        map.put("Remark", view.getManager()?.remark ?: "")
        map.put("DelMarker", false)
        list.add(JSON.toJSONString(map))
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        view.complete()
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun rxDeAttach() {
        if (bankSubscription != null && !bankSubscription?.isUnsubscribed!!) {
            bankSubscription?.unsubscribe()
            bankSubscription = null
        }
        if (subscription != null && !subscription?.isUnsubscribed!!) {
            subscription?.unsubscribe()
            subscription = null
        }
    }
}