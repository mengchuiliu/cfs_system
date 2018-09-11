package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.PopupWindow
import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.RegisteredMImp
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.RegisteredActivity
import com.xxjr.cfs_system.LuDan.view.viewinter.RegisteredVInter
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.Utils
import com.yanzhenjie.permission.AndPermission
import entity.ChooseType
import entity.GoldRegisteredInfo
import rx.Subscription

/**
 * Created by Administrator on 2018/3/9.
 */
class RegisteredPresenter : BasePresenter<RegisteredVInter, RegisteredMImp>() {
    private var register = GoldRegisteredInfo()
    private var popWindow: PopupWindow? = null
    private var bankSubscription: Subscription? = null//银行
    private var cardSubscription: Subscription? = null//证件类型
    private var citySubscription: Subscription? = null//开户行地区类型
    private var isRefresh = true

    override fun getModel(): RegisteredMImp = RegisteredMImp()

    override fun setDefaultValue() {
        initRX()
        register.IDType = 1
        view.initRV(model.getRegisteredData(view.getRegisteredType()))
    }

    private fun initRX() {
        cardSubscription = RxBus.getInstance().toObservable(11, ChooseType::class.java).subscribe { s ->
            hidePop()
            register.IDType = s.id
            view.refreshItem(view.getRegisteredType(), s.content)
        }
        bankSubscription = RxBus.getInstance().toObservable(22, ChooseType::class.java).subscribe { s ->
            hidePop()
            register.bankId = s.id
            register.bankName = s.content
            view.refreshItem(if (view.getRegisteredType() == 1) 5 else 6, s.content)
        }
        citySubscription = RxBus.getInstance().toObservable(33, ChooseType::class.java).subscribe { s ->
            hidePop()
            register.area = s.ids
            view.refreshItem(if (view.getRegisteredType() == 1) 6 else 7, s.content)

        }
    }

    fun editContent(position: Int, text: String) {
        if (isViewAttached) {
            when (view.getRegisteredType()) {
                1 -> {
                    if (position > 1) setEditText(position + 1, text) else register.customerName = text
                }
                2 -> {
                    when (position) {
                        0 -> register.enterpriseName = text
                        1 -> register.personName = text
                        else -> setEditText(position, text)
                    }
                }
            }
        }
    }

    private fun setEditText(position: Int, text: String) {
        when (position) {
            3 -> register.IDCardNo = text
            4 -> register.telPhone = text
            5 -> register.email = text
            8 -> {
                register.branch = text
                freshHead()
            }
            9 -> {
                register.bankNo = text
                if (isRefresh) freshHead()
                isRefresh = true
            }
        }
    }

    //刷新头部两个item
    fun freshHead() {
        when (view.getRegisteredType()) {
            1 -> view.refreshItem(0, register.customerName ?: "")
            2 -> {
                view.refreshItem(0, register.enterpriseName ?: "")
                view.refreshItem(1, register.personName ?: "")
            }
        }
    }

    fun clickChoose(position: Int, parent: View) {
        if (isViewAttached) {
            when (position) {
                view.getRegisteredType() -> {
                    popWindow = PopChoose.showChooseType(view as Context, parent, "证件类型",
                            model.getTypeDataList("PayCardType", false), 11, false)
                }
                5 -> {//银行名称
                    popWindow = PopChoose.showChooseType(view as Context, parent, "选择银行",
                            model.getTypeDataList("PaymentBank", true), 22, false)
                }
                6 -> {//银行名称或者开户行地区
                    when (view.getRegisteredType()) {
                        1 -> {//开户行地区
                            view.chooseCity()
                        }
                        2 -> {//银行名称
                            popWindow = PopChoose.showChooseType(view as Context, parent, "选择银行",
                                    model.getTypeDataList("PaymentBank", true), 22, false)
                        }
                    }
                }
                7 -> {//开户行地区
                    view.chooseCity()
                }
                8, 9 -> {//银行卡
                    AndPermission.with(view as RegisteredActivity)
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
            }
        }
    }

    fun setBankNo(bankNo: String) {
        register.bankNo = bankNo
    }

    fun setRefresh(isRefresh: Boolean) {
        this.isRefresh = isRefresh
    }

    override fun permissionSuccess(code: Int) {
        when (code) {
            Constants.REQUEST_CODE_PERMISSION_Camera -> {
                view.scanCard()
            }
        }
    }

    private fun hidePop() {
        if (popWindow != null && popWindow?.isShowing!!) {
            popWindow?.dismiss()
        }
    }

    fun registered() {
        if (register.check(view as Context, view.getRegisteredType())) {
            val s = Utils.IDCardValidate(register.IDCardNo ?: "")
            if (s.isNotBlank()) {
                view.showMsg(s)
                return
            }
            getData(0, model.getRegisteredParam(getListParam()), true)
        }
    }

    private fun getListParam(): String {
        val arrayMap = hashMapOf<String, Any>()
        arrayMap["UserID"] = Hawk.get<String>("UserID") //用户ID
        arrayMap["CustomerID"] = "" //客户ID
        arrayMap["AisleBranch"] = 1 //金账户商户分支；现在固定为 1
        arrayMap["UserType"] = view.getRegisteredType() //账户类型（1：个人，2：门店/企业）
        arrayMap["CstmNm"] = if (view.getRegisteredType() == 1) register.customerName
                ?: "" else register.enterpriseName ?: ""//客户姓名（门店/企业账户填写企业名称）
        arrayMap["LegalPersonNm"] = if (view.getRegisteredType() == 1) "" else register.personName
                ?: "" //法人姓名（门店/企业账户必填，个人账户忽略）
        arrayMap["IDCardType"] = register.IDType //证件类型
        arrayMap["IDCardNo"] = register.IDCardNo ?: "" //证件号码
        arrayMap["PhoneNo"] = register.telPhone ?: "" //手机号码
        arrayMap["Email"] = register.email ?: ""
        arrayMap["CityCode"] = register.area ?: "" //开户行地区（支付中心的）
        arrayMap["BankId"] = register.bankId //银行id（支付中心的）
        arrayMap["BankNm"] = register.branch ?: "" //支行名称
        arrayMap["BankCardNo"] = register.bankNo ?: "" //银行卡卡号
        arrayMap["Rem"] = "" //备注
        arrayMap["CompanyID"] = Hawk.get<String>("CompanyID")
        arrayMap["OriginCode"] = 4 //1：PC客户端，2: Web前端,3: iOS,4: Android
        arrayMap["AccountType"] = view.getAccountType() //1.个人私户  2.门店私户  3.门店公户 4.顾客账户
        return JSON.toJSONString(arrayMap)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    register.userNo = data?.returnString ?: ""
                    view.complete(register)
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun rxDeAttach() {
        if (popWindow != null) {
            popWindow?.dismiss()
            popWindow = null
        }
        if (bankSubscription != null && !bankSubscription!!.isUnsubscribed) {
            bankSubscription!!.unsubscribe()
        }
        if (cardSubscription != null && !cardSubscription!!.isUnsubscribed) {
            cardSubscription!!.unsubscribe()
        }
        if (citySubscription != null && !citySubscription!!.isUnsubscribed) {
            citySubscription!!.unsubscribe()
        }
    }
}