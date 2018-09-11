package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.PopupWindow
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.AgreementMImp
import com.xxjr.cfs_system.LuDan.view.activitys.SearchActivity
import com.xxjr.cfs_system.LuDan.view.activitys.withholding_agreement.AgreementActivity
import com.xxjr.cfs_system.LuDan.view.viewinter.AgreementVInter
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.tools.Constants
import com.yanzhenjie.permission.AndPermission
import entity.ChooseType
import entity.SignInfo
import rx.Subscription

/**
 * Created by Administrator on 2018/1/16.
 */
class AgreementPresenter : BasePresenter<AgreementVInter, AgreementMImp>() {
    private var popWindow: PopupWindow? = null
    private var companySubscription: Subscription? = null//代扣平台
    private var payAisleSubscription: Subscription? = null//代扣平台
    private var signBankSubscription: Subscription? = null//签约银行
    private var bankCardSubscription: Subscription? = null//银行卡类型
    private var accountSubscription: Subscription? = null//账户属性
    private var signerSubscription: Subscription? = null//签约者身份
    private var cardSubscription: Subscription? = null//证件类型
    private var signInfo = SignInfo()

    override fun getModel(): AgreementMImp = AgreementMImp()

    override fun setDefaultValue() {
        initRX()
//        signInfo.aisleType = "5"
        signInfo.companyID = Hawk.get<String>("CompanyID")
        view.initRV(model.getAgreementData(signInfo.companyID ?: ""))
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if ((data?.returnStrings ?: "").isNotBlank()) {
            val jsonArray = JSONArray.parseArray(data?.returnStrings)
            view.complete(jsonArray.getString(1) == "True", signInfo.telPhone ?: "",
                    jsonArray.getString(0) ?: "", signInfo.aisleType ?: "", jsonArray.getString(2)
                    ?: "")
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun toNext() {
        if (signInfo.check(view as Context)) {
            getData(0, model.getParam(getListParam(), "ApplyRealPay"), true)
        }
    }

    private fun getListParam(): MutableList<Any> {
        val list = mutableListOf<Any>()
        val map = hashMapOf<Any, Any>()
        map.put("AisleType", signInfo.aisleType ?: "")//signInfo.aisleType ?: "",1和5同一家协议，缓存是1，接口用5
        map.put("CompanyID", signInfo.companyID ?: "")
        map.put("BankId", signInfo.bankCode ?: "")
        map.put("AccountType", signInfo.accountType ?: "")
        map.put("AccountNo", signInfo.accountNo ?: "")
        map.put("AccountName", signInfo.accountName ?: "")
        map.put("AccountProp", signInfo.accountProp ?: "")
        map.put("IDType", signInfo.IDType ?: "")
        map.put("IDCardNo", signInfo.IDCardNo ?: "")
        map.put("ShareIDCard", signInfo.friendCardNo ?: "")
        map.put("Tel", signInfo.telPhone ?: "")
        map.put("OperateId", Hawk.get<String>("UserID"))
        map.put("OriginCode", "4")
        list.add(JSON.toJSONString(map))
        list.add(signInfo.signer ?: "")
        return list
    }

    fun editContent(position: Int, text: String) {
        if (isViewAttached) {
            when (position) {
                1 -> signInfo.accountName = text
                6 -> {
                    signInfo.accountNo = text
//                    view.refreshItem(1, signInfo.accountName ?: "")
                }
                9 -> {
                    signInfo.IDCardNo = text
                    view.refreshItem(1, signInfo.accountName ?: "")
                }
                10 -> {
                    signInfo.telPhone = text
                    view.refreshItem(1, signInfo.accountName ?: "")
                }
                11 -> {
                    signInfo.friendCardNo = text
                    view.refreshItem(1, signInfo.accountName ?: "")
                }
            }
        }
    }

    fun setAccountNo(text: String) {
        signInfo.accountNo = text
    }

    fun clickChoose(position: Int, parent: View) {
        if (isViewAttached) {
            when (position) {
                0 -> {
                    popWindow = PopChoose.showChooseType(view as Context, parent, "所属门店",
                            model.getTypeDataIdList("CompanyInfoType"), 9, false)
                }
                2 -> {
                    popWindow = PopChoose.showChooseType(view as Context, parent, "代扣平台",
                            model.getPayAisleTypeDataList("PayAisleType"), 10, false)
                }
                3 -> {
                    if (signInfo.aisleType.isNullOrBlank()) {
                        view.showMsg("请先选择代扣平台")
                        return
                    }
//                    popWindow = PopChoose.showChooseType(view as Context, parent, "签约银行",
//                            model.getTypeDataList("PaymentBank", signInfo.aisleType
//                                    ?: "", true), 11, false)
                    val intent = Intent(view as Context, SearchActivity::class.java)
                    intent.putExtra("type", Constants.Sign_Bank)
                    intent.putExtra("aisleType", if (signInfo.aisleType ?: "" == "5") "1" else signInfo.aisleType
                            ?: "")
                    intent.putExtra("hintContent", "搜索签约银行")
                    (view as Context).startActivity(intent)
                }
                4 -> {
                    if (signInfo.aisleType.isNullOrBlank()) {
                        view.showMsg("请先选择代扣平台")
                        return
                    }
                    popWindow = PopChoose.showChooseType(view as Context, parent, "银行卡类型",
                            model.getTypeDataList("PayBankCardType", if (signInfo.aisleType ?: "" == "5") "1" else signInfo.aisleType
                                    ?: "", false), 22, false)
                }
                5 -> {
                    popWindow = PopChoose.showChooseType(view as Context, parent, "账户属性",
                            model.getTypeDataList("PayAccountProp"), 33, false)
                }
                6 -> {
                    AndPermission.with(view as AgreementActivity)
                            .requestCode(Constants.REQUEST_CODE_PERMISSION_Camera)
                            .permission(android.Manifest.permission.CAMERA)
                            .callback(permissioner)
                            // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                            // 这样避免用户勾选不再提示，导致以后无法申请权限。
                            // 你也可以不设置。
                            .rationale { requestCode, rationale ->
                                // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                                AndPermission.rationaleDialog(view as Context, rationale).show()
                            }
                            .start()
                }
                7 -> {
                    popWindow = PopChoose.showChooseType(view as Context, parent, "签约者身份",
                            model.getTypeDataList("SignerType"), 44, false)
                }
                8 -> {
                    if (signInfo.aisleType.isNullOrBlank()) {
                        view.showMsg("请先选择代扣平台")
                        return
                    }
                    popWindow = PopChoose.showChooseType(view as Context, parent, "证件类型",
                            model.getTypeDataList("PayCardType", if (signInfo.aisleType ?: "" == "5") "1" else signInfo.aisleType
                                    ?: "", false), 55, false)
                }
            }
        }
    }

    private fun initRX() {
        companySubscription = RxBus.getInstance().toObservable(9, ChooseType::class.java).subscribe { s ->
            hidePop()
            signInfo.companyID = s.ids
            view.refreshItem(0, s.content)
        }
        payAisleSubscription = RxBus.getInstance().toObservable(10, ChooseType::class.java).subscribe { s ->
            hidePop()
            signInfo.aisleType = s.ids
            view.refreshItem(2, s.content)
        }
        signBankSubscription = RxBus.getInstance().toObservable(Constants.Sign_Bank, ChooseType::class.java).subscribe { s ->
            hidePop()
            signInfo.bankCode = s.ids
            view.refreshItem(3, s.content)
        }
        bankCardSubscription = RxBus.getInstance().toObservable(22, ChooseType::class.java).subscribe { s ->
            hidePop()
            signInfo.accountType = s.ids
            view.refreshItem(4, s.content)
        }
        accountSubscription = RxBus.getInstance().toObservable(33, ChooseType::class.java).subscribe { s ->
            hidePop()
            signInfo.accountProp = s.ids
            view.refreshItem(5, s.content)
        }
        signerSubscription = RxBus.getInstance().toObservable(44, ChooseType::class.java).subscribe { s ->
            hidePop()
            signInfo.signer = s.ids
            view.refreshItem(7, s.content)
        }
        cardSubscription = RxBus.getInstance().toObservable(55, ChooseType::class.java).subscribe { s ->
            hidePop()
            signInfo.IDType = s.ids
            view.refreshItem(8, s.content)
        }
    }

    private fun hidePop() {
        if (popWindow != null && popWindow?.isShowing!!) {
            popWindow?.dismiss()
        }
    }

    override fun permissionSuccess(code: Int) {
        when (code) {
            Constants.REQUEST_CODE_PERMISSION_Camera -> {
                view.scanCard()
            }
        }
    }

    fun rxDeAttach() {
        if (popWindow != null) {
            popWindow?.dismiss()
            popWindow = null
        }
        if (companySubscription != null && !companySubscription!!.isUnsubscribed) {
            companySubscription!!.unsubscribe()
        }
        if (payAisleSubscription != null && !payAisleSubscription!!.isUnsubscribed) {
            payAisleSubscription!!.unsubscribe()
        }
        if (bankCardSubscription != null && !bankCardSubscription!!.isUnsubscribed) {
            bankCardSubscription!!.unsubscribe()
        }
        if (signBankSubscription != null && !signBankSubscription!!.isUnsubscribed) {
            signBankSubscription!!.unsubscribe()
        }
        if (accountSubscription != null && !accountSubscription!!.isUnsubscribed) {
            accountSubscription!!.unsubscribe()
        }
        if (signerSubscription != null && !signerSubscription!!.isUnsubscribed) {
            signerSubscription!!.unsubscribe()
        }
        if (cardSubscription != null && !cardSubscription!!.isUnsubscribed) {
            cardSubscription!!.unsubscribe()
        }
    }
}