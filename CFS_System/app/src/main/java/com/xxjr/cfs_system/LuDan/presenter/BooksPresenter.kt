package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.view.View
import android.widget.PopupWindow
import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.BooksMImp
import com.xxjr.cfs_system.LuDan.view.viewinter.BooksVInter
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.tools.DateUtil
import entity.BookRecord
import entity.ChooseType
import rx.Subscription
import timeselector.TimeSelector

class BooksPresenter : BasePresenter<BooksVInter, BooksMImp>() {
    private var popWindow: PopupWindow? = null
    private var timeSelector: TimeSelector? = null
    private var paymentSubscription: Subscription? = null
    private var costSubscription: Subscription? = null
    private var accountSubscription: Subscription? = null
    private var accountSubscription1: Subscription? = null
    private var bookRecord: BookRecord = BookRecord()

    private var chooseTypeList: MutableList<ChooseType>? = null//虚拟账户集合
    private var protocolList: MutableList<ChooseType>? = null//代扣协议账户集合
    private var goldList: MutableList<ChooseType>? = null//金账户集合

    private var payAisleId = ""//代扣平台id

    override fun getModel(): BooksMImp = BooksMImp()

    override fun setDefaultValue() {
        chooseTypeList = ArrayList()
        protocolList = ArrayList()
        goldList = ArrayList()

        view.initRecycler(model.getItemData(view.getLoanInfo(), view.getBooksType()))
        initTimeSelector()
        initRX()
        getAccount()
    }

    private fun getAccount() {
        if (isViewAttached) {
            getData(0, model.getAccountParam(getAccountParamList(), "GET", "GetAccountInfo"), true)
        }
    }

    private fun initTimeSelector() {
        if (isViewAttached) {
            timeSelector = TimeSelector(view as Context, TimeSelector.ResultHandler { time ->
                bookRecord.payTime = time
                view.refreshItem(6, time)
            }, "1900-01-01", DateUtil.getCurDate())
            timeSelector?.setScrollUnit(TimeSelector.SCROLLTYPE.YEAR, TimeSelector.SCROLLTYPE.MONTH, TimeSelector.SCROLLTYPE.DAY)
        }
    }

    private fun initRX() {
        paymentSubscription = RxBus.getInstance().toObservable(11, ChooseType::class.java).subscribe { s ->
            hidePop()
            bookRecord.bookType = s.id
            view.refreshItem(2, s.content)
        }
        costSubscription = RxBus.getInstance().toObservable(22, ChooseType::class.java).subscribe { s ->
            hidePop()
            bookRecord.payType = s.id
            view.refreshItem(3, s.content)
            if (s.id == 3) {
                if (protocolList != null && protocolList?.isEmpty()!!) {
                    getData(2, model.getAccountParam(getProtocolParamList(), "GET", "GetLoanProtocol"), false)//获取门店协议
                }
                view.refreshItemName(13, "代扣平台")
                view.refreshItemName(14, "代扣协议号")
                view.refreshItemName(15, "选择付款方")
                view.refreshItemName(16, "付款方户名")
                view.refreshItemName(17, "付款方账号")
                view.refreshItem(16, false)
                view.refreshItem(17, false)
                cleanItem()
            } else if (s.id == 4) {
                if (goldList != null && goldList?.isEmpty()!!) {
                    getData(3, model.getAccountParam(mutableListOf<Any>().apply {
                        add(view.getLoanInfo().companyID)
                    }, "GetGoldAccountNoByCompanyId", "GoldAccount"), false)//获取金账户信息
                }
                view.refreshItemName(13, "金账户")
                view.refreshItemName(14, "签约银行")
                view.refreshItemName(15, "账户")
                view.refreshItemName(16, "银行卡账号")
                view.refreshItemName(17, "备注")
                view.refreshItem(16, false)
                view.refreshItem(17, false)
                cleanItem()
            } else {
                if (view.getBooksType() == 1) {
                    view.refreshItemName(13, "收款方户名")
                    view.refreshItemName(14, "收款方账号")
                    view.refreshItemName(15, "选择付款方")
                    view.refreshItemName(16, "付款方户名")
                    view.refreshItemName(17, "付款方账号")
                } else {
                    view.refreshItemName(13, "付款方户名")
                    view.refreshItemName(14, "付款方账号")
                    view.refreshItemName(15, "选择收款方")
                    view.refreshItemName(16, "收款方户名")
                    view.refreshItemName(17, "收款方账号")
                }
                view.refreshItem(16, true)
                view.refreshItem(17, true)
                cleanItem()
            }
        }
        accountSubscription = RxBus.getInstance().toObservable(33, ChooseType::class.java).subscribe { s ->
            hidePop()
            if (bookRecord.payType == 3) {//代扣协议
                payAisleId = s.ids
                bookRecord.ourName = s.content
                view.refreshItem(13, s.content)
                view.refreshItem(14, "")
                view.refreshItem(15, "")
                view.refreshItem(16, "")
                view.refreshItem(17, "")
                bookRecord.ourAccount = ""
                bookRecord.otherName = ""
                bookRecord.otherAccount = ""
                bookRecord.protocolId = -1
            } else if (bookRecord.payType == 4) {//金账户
                bookRecord.protocolId = s.id//金账户id
                if (view.getBooksType() == 1) {
                    bookRecord.ourName = s.account
                    bookRecord.ourAccount = s.ids
                } else {
                    bookRecord.otherName = s.account
                    bookRecord.otherAccount = s.ids
                }
                view.refreshItem(13, s.content)
                view.refreshItem(14, s.type)
                view.refreshItem(15, s.account)
                view.refreshItem(16, s.ids)
                view.refreshItem(17, "")
            } else {
                if (view.getBooksType() == 1) {
                    bookRecord.ourName = s.account
                    bookRecord.ourAccount = s.ids
                } else {
                    bookRecord.otherName = s.account
                    bookRecord.otherAccount = s.ids
                }
                view.refreshItem(13, s.account)
                view.refreshItem(14, s.ids)
            }
        }
        accountSubscription1 = RxBus.getInstance().toObservable(44, ChooseType::class.java).subscribe { s ->
            hidePop()
            if (bookRecord.payType == 3) {
                bookRecord.otherName = s.account
                bookRecord.otherAccount = s.ids
                bookRecord.protocolId = s.id
                view.refreshItem(14, s.protocolNo)
            } else {
                if (view.getBooksType() == 1) {
                    bookRecord.otherName = s.account
                    bookRecord.otherAccount = s.ids
                } else {
                    bookRecord.ourName = s.account
                    bookRecord.ourAccount = s.ids
                }
            }
            view.refreshItem(15, s.content)
            view.refreshItem(16, s.account)
            view.refreshItem(17, s.ids)
//            view.refreshAdapter()
        }
    }

    private fun cleanItem() {
        view.refreshItem(13, "")
        view.refreshItem(14, "")
        view.refreshItem(15, "")
        view.refreshItem(16, "")
        view.refreshItem(17, "")
        bookRecord.ourName = ""
        bookRecord.ourAccount = ""
        bookRecord.otherName = ""
        bookRecord.otherAccount = ""
        bookRecord.protocolId = -1
        payAisleId = ""
    }

    fun getBookRecord(): BookRecord = bookRecord

    fun clickChoose(position: Int, parent: View) {
        if (isViewAttached) {
            when (position) {
                2 -> {
                    val list = model.getTypeDataList("BookTypes", view.getBooksType())
                    popWindow = PopChoose.showChooseType(view as Context, parent, "收支类型", list, 11, false)
                }
                3 -> {
                    popWindow = PopChoose.showChooseType(view as Context, parent, "支付方式",
                            model.getCostDataList("EnterAccountType", view.getBooksType()), 22, false)
                }
                6 -> timeSelector?.show()
                13 -> {
                    if (bookRecord.payType == 3) {//代扣协议
                        popWindow = PopChoose.showChooseType(view as Context, parent, "账户信息", model.getPayAisles(), 33, false)
                    } else if (bookRecord.payType == 4) {//金账户
                        popWindow = PopChoose.showChooseType(view as Context, parent, "账户信息", goldList, 33, false)
                    } else {
                        popWindow = PopChoose.showChooseType(view as Context, parent, "账户信息", chooseTypeList, 33, false)
                    }
                }
                15 -> {
                    if (bookRecord.payType == 3) {//代扣协议
                        if (payAisleId.isNotBlank()) {
                            popWindow = PopChoose.showChooseType(view as Context, parent, "账户信息", model.getProtocols(protocolList!!, payAisleId), 44, false)
                        } else {
                            view.showMsg("请先选择代扣平台")
                        }
                    } else if (bookRecord.payType == 4) {//金账户
                    } else {
                        popWindow = PopChoose.showChooseType(view as Context, parent, "账户信息", chooseTypeList, 44, false)
                    }
                }
            }
        }
    }

    //门店账号数据参数
    private fun getAccountParamList(): MutableList<Any> {
        val list = ArrayList<Any>()
        list.add("*")
        if (view.getLoanInfo().companyID == "000") {
            list.add("")
        } else {
            list.add("CompanyID IN ('000','" + view.getLoanInfo().companyID + "')")
        }
        return list
    }

    //获取代扣协议数据参数
    private fun getProtocolParamList(): MutableList<Any> {
        val list = ArrayList<Any>()
//        list.add(Hawk.get<Any>("CompanyID"))
        list.add(view.getLoanInfo().companyID)
        list.add("0")
        list.add(view.getLoanInfo().customerId)
        return list
    }

    fun postData() {
        if (bookRecord.check(view as Context, bookRecord.payType))
            getData(1, model.getParam(getPostParamList(), "AddInOnAccount"), true)
    }

    private fun getPostParamList(): MutableList<Any> {
        val list = ArrayList<Any>()
        val map1 = HashMap<String, Any>()
        map1.put("LoanID", view.getLoanInfo().loanId)
        map1.put("ContractID", view.getLoanInfo().contractId)
        map1.put("BookType", bookRecord.bookType)
        map1.put("Amount", bookRecord.amount)
        map1.put("PayType", bookRecord.payType)
        map1.put("PayTime", bookRecord.payTime)
        map1.put("Abstract", bookRecord.digest)
        map1.put("OurName", bookRecord.ourName)
        map1.put("OurAccount", bookRecord.ourAccount)
        map1.put("OtherName", bookRecord.otherName)
        map1.put("OtherAccount", bookRecord.otherAccount)
        map1.put("ServicePeople", Hawk.get("UserID"))
        map1.put("UserType", view.getBooksType())
        map1.put("HavePic", 0)
        map1.put("ProtocolId", bookRecord.protocolId)//协议id
        map1.put("Remark", bookRecord.remark)
        val s = JSON.toJSONString(map1)
        list.add(s)
        val scheduleId = view.getLoanInfo().scheduleId
        //0->"发起申请"  1->"待审核" 2->"转总部入账"
        if (scheduleId == 109 || scheduleId == 5 || scheduleId == -3 || scheduleId == -4 || scheduleId == -5) {
            list.add("0")
        } else if (scheduleId == 1090 || scheduleId == 50) {
            list.add("1")
        } else {
            list.add("0")
        }
        return list
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        when (resultCode) {
            0 -> getAccountData(data?.data ?: "")
            1 -> view.complete()//提交数据
            2 -> getProtocolData(data?.data ?: "")//代扣协议数据
            3 -> getGoldData(data?.data ?: "")//金账户数据
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) {
        view.showMsg(msg)
    }

    private fun hidePop() {
        if (popWindow != null && popWindow?.isShowing!!) {
            popWindow?.dismiss()
        }
    }

    //账户信息
    private fun getAccountData(data: String) {
        val jsonArray = JSON.parseArray(data)
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                chooseType = ChooseType()
                val `object` = jsonArray.getJSONObject(i)
                chooseType.content = `object`.getString("AccountName") + "--" + `object`.getString("Account")
                chooseType.ids = `object`.getString("Account")
                chooseType.account = `object`.getString("AccountName")
                chooseTypeList?.add(chooseType)
            }
        }
    }

    //获取所有代扣协议数据
    private fun getProtocolData(data: String) {
        val jsonArray = JSON.parseArray(data)
        if (jsonArray != null && jsonArray.size != 0) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                chooseType = ChooseType()
                val jsonObject = jsonArray.getJSONObject(i)
                chooseType.content = jsonObject.getString("CardName") + "--" + jsonObject.getString("BankCardNo")
                chooseType.id = jsonObject.getIntValue("ID")
                chooseType.account = jsonObject.getString("CardName")
                chooseType.ids = jsonObject.getString("BankCardNo")
                chooseType.protocolNo = jsonObject.getString("ProtocolNo")
                chooseType.type = jsonObject.getString("AisleType")
                protocolList?.add(chooseType)
            }
        }
    }

    private fun getGoldData(data: String) {
        val jsonArray = JSON.parseArray(data)
        if (jsonArray != null && jsonArray.isNotEmpty()) {
            var chooseType: ChooseType
            for (i in jsonArray.indices) {
                chooseType = ChooseType()
                val jsonObject = jsonArray.getJSONObject(i)
                chooseType.content = jsonObject.getString("CstmNm") + "--" + jsonObject.getString("BankCardNo")
                chooseType.type = jsonObject.getString("BankName")
                chooseType.account = jsonObject.getString("CstmNm")
                chooseType.ids = jsonObject.getString("BankCardNo")
                chooseType.id = jsonObject.getIntValue("ID")
                goldList?.add(chooseType)
            }
        }
    }

    fun rxDeAttach() {
        chooseTypeList = null
        protocolList = null
        goldList = null
        if (paymentSubscription != null && !paymentSubscription!!.isUnsubscribed) {
            paymentSubscription!!.unsubscribe()
        }
        if (costSubscription != null && !costSubscription!!.isUnsubscribed) {
            costSubscription!!.unsubscribe()
        }
        if (accountSubscription != null && !accountSubscription!!.isUnsubscribed) {
            accountSubscription!!.unsubscribe()
        }
        if (accountSubscription1 != null && !accountSubscription1!!.isUnsubscribed) {
            accountSubscription1!!.unsubscribe()
        }
    }
}