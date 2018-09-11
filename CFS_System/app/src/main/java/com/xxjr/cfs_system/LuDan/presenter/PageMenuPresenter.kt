package com.xxjr.cfs_system.LuDan.presenter

import android.app.Dialog
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.model.HttpResult
import com.xxjr.cfs_system.LuDan.model.modelimp.SetMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.PageMenuVInter
import com.xxjr.cfs_system.tools.AppUpdateHelp
import com.xxjr.cfs_system.tools.DateUtil
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.ArrayList

/**
 * Created by Administrator on 2017/11/8.
 */
class PageMenuPresenter(val view: PageMenuVInter) : HttpResult {
    private var resultCode = 0
    var dialog: Dialog? = null

    private val model = SetMImp()

    fun initView(permissions: String) {
        val isRemind = model.getIsRemind(permissions)
        view.initRecycler(model.getItemList(isRemind))
        if (Hawk.get<String>("UserType") == "22") {
            view.setMortgageScore("0")
        }
    }

    //检测更新
    fun checkAppUpdate() {
        AppUpdateHelp(view.getPageContext()).checkUpdate(true, true)
    }

    fun loginOut() {
        CustomDialog.showTwoButtonDialog(view.getPageContext(), "确定退出此次登录?", "确定", "取消") { dialogInterface, i ->
            dialogInterface.dismiss()
            view.loginOut()
        }
    }

    //获取服务总积分
    fun getMortgageScore() {
        if (Hawk.get<String>("UserType") == "22") {
            resultCode = 2
            model.getData(view.getPageContext(), Hawk.get("SessionID"), 2, getScoreParam(), this, false)
        }
    }

    //更新缓存
    fun updateCacheData() {
        resultCode = 0
        model.getData(view.getPageContext(), Hawk.get("SessionID"), 0, getIncreaseCacheParam(), this, true)
    }

    //按揭员服务总积分
    private fun getScoreParam(): String = model.getParam(mutableListOf<Any>().apply { add(DateUtil.getCurDate()) }, "GetMortgageSumScoreApp")

    //更新缓存
    private fun getIncreaseCacheParam(): String = model.getIncreaseCacheParam(getListParam(), "GET_INCREASE_CACHE")

    override fun reusltSuccess(data: ResponseData?) {
        when (resultCode) {
            0 -> {
                if (data != null) {
                    if (dialog == null) {
                        dialog = CustomDialog.createLoadingDialog(view.getPageContext(), "缓存数据同步中...")
                    }
                    dialog?.setCancelable(false)
                    dialog?.show()
                    Observable.create(Observable.OnSubscribe<Any> { subscriber ->
                        model.saveCacheData(data.returnMsg ?: "")
                        subscriber.onNext("")
                    }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Subscriber<Any>() {

                                override fun onCompleted() {
                                }

                                override fun onError(e: Throwable) {
                                    if (dialog != null && dialog?.isShowing!!)
                                        dialog?.dismiss()
                                    view.showMsg("数据同步失败!${e.message}")
                                }

                                override fun onNext(o: Any) {
                                    if (dialog != null && dialog?.isShowing!!)
                                        dialog?.dismiss()
                                    view.showMsg("数据同步成功!")
                                }
                            })

                }
            }
            2 -> {
                if ((data?.returnString ?: "").isNotBlank()) {
                    view.setMortgageScore(data?.returnString ?: "0")
                }
            }
        }
    }

    override fun reusltFailed(msg: String?) = view.showMsg(msg)

    private fun getListParam(): List<Any> {
        val list = ArrayList<Any>()
        list.add("SexType")
        list.add("IDType")
        list.add("LoansType")
        list.add("ContractStatus")//合同状态，进度
        list.add("LoanStateType")//贷款状态，进度
        list.add("CreditLoanOperateType")//前进回退
        list.add("CompanyInfoType")//签约门店
        list.add("MortgageUserType")//按揭员
        list.add("LoanBankType")//银行类型
        list.add("LoanProductType")//银行产品
        list.add("LoanCostType")
        list.add("BookTypes")
        list.add("EnterAccountType")
        list.add("T_Config")//审核配置
//        list.add("WithholdPlatf")//代扣账号
        list.add("LendingType")//拆借类型
        list.add("LendStateType")//拆借状态
        list.add("RePayType")//还款方式
        list.add("ImageDataType")//资料类型
        list.add("ZoneType")//地区
        list.add("ImprovementType")//评价按揭员标签
        list.add("PayAisleType")//代扣平台
        list.add("PaymentBank")//签约银行
        list.add("PayBankCardType")//银行卡类型
        list.add("PayAccountProp")//账户属性
        list.add("PayCardType")//证件类型
        list.add("SignerType")//签约者身份

        list.add("EchelonType")//梯队
        list.add("MortgageScoreType")//积分类型
        list.add("RepaymentType")//还款类型
        return list
    }
}