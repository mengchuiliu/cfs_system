package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.view.View
import android.widget.PopupWindow
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.WithdrawalActivity
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import entity.ChooseType
import rx.Subscription
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Administrator on 2018/3/14.
 */
class WithdrawalPresenter : BasePresenter<WithdrawalActivity, ModelImp>() {
    private var subscription: Subscription? = null
    private var popWindow: PopupWindow? = null

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        subscription = RxBus.getInstance().toObservable(11, ChooseType::class.java).subscribe { chooseType ->
            if (popWindow != null && popWindow?.isShowing!!) {
                popWindow?.dismiss()
                view.setTopText(chooseType.id)
            }
        }
    }

    fun showTopUp(parent: View) {
        popWindow = PopChoose.showChooseType(view as Context, parent, "充值方式",
                getTypeDataList(), 11, false)
    }

    fun getTopUpData(amount: Double) {
        getData(0, getTopUpParam(getListParam(amount)), true)
    }

    private fun getTopUpParam(json: String): String {
        val map = HashMap<String, Any>()
        map["IsUseZip"] = false
        map["Function"] = ""
        map["ParamString"] = ""
        map["Json"] = json
        map["Action"] = "OfflineRecharge"
        map["TranName"] = "GoldAccount"
        Logger.e("==通用数据参数==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    private fun getListParam(amount: Double): String {
        val arrayMap = hashMapOf<String, Any>()
        arrayMap["Amt"] = amount
        arrayMap["CompanyID"] = Hawk.get<String>("CompanyID")
        arrayMap["OriginCode"] = 4 //1：PC客户端，2: Web前端,3: iOS,4: Android
        arrayMap["Remark"] = "" //备注
        arrayMap["UserNo"] = view.getUserNo()
        return JSON.toJSONString(arrayMap)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            if ((data?.returnString ?: "").isNotBlank()) {
                view.showTopUpInfo(data?.returnString!!)
            } else {
                view.showMsg("充值码获取失败")
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun getTypeDataList(): MutableList<ChooseType> {
        val list = ArrayList<ChooseType>()
        var chooseType: ChooseType
        for (i in 0..1) {
            chooseType = ChooseType()
            chooseType.id = i
            when (i) {
                0 -> chooseType.content = "快捷充值"
                1 -> chooseType.content = "充值码转账"
            }
            list.add(chooseType)
        }
        return list
    }

    fun rxDeAttach() {
        if (popWindow != null) {
            popWindow?.dismiss()
            popWindow = null
        }
        if (subscription != null && !subscription?.isUnsubscribed!!) {
            subscription?.unsubscribe()
            subscription = null
        }
    }
}