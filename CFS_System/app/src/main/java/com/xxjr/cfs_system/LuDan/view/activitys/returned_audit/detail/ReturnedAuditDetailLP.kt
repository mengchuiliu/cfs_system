package com.xxjr.cfs_system.LuDan.view.activitys.returned_audit.detail

import android.app.Activity
import android.content.Intent
import android.util.Base64
import android.view.View
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.HttpAction
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.tools.*
import entity.*
import rx.Subscriber
import java.util.*
import kotlin.collections.HashMap

/**
 *
 * 回款审核详情   逻辑处理类
 * @author huangdongqiang
 * @date 27/06/2018
 */
class ReturnedAuditDetailLP : BasePresenter<ReturnedAuditDetailActivity, ReturnedAuditDetailMImp>() {
    private var entity: ReturnedAuditEntity? = null
    var withholdEntity: ReturnedAuditWithholdEntity? = null    //代扣信息
    var goldEntity: ReturnedAuditGoldEntity? = null            //金账户信息
    private var fileData = mutableListOf<CommonItem<Any>>()    //附件数据集合
    var jsonArray = JSONArray() //贷款数据

    var schedulePos = 0 // 一级菜单栏点击的位置
    var auditPos = 0    // 二级菜单栏点击的位置
    val requestCodeWithhold = 0 //代扣信息
    val requestCodeWithLoan = 1 //贷款信息
    val requestCodeGold = 100   //金账户信息
    val requestActionCheckPass = 10   //待审核 - 通过
    val requestActionCheckPassSpecific = 40    //待审核 - 代扣 - 只有代扣平台AisleType为（通联支付1，上海富友2，通联协议5）
    val requestActionCheckReject = 20 //待审核 - 拒绝
    val requestActionWithhold = 30    //发起代扣
    val requestFileDelete = 50        //删除附件
    val requestFileCheck = 60         //文件查看的数据
    val blank = "       "
    val blank5 = "   "
    val blankFor3 = "    "
    private var uploadSubscription: UploadSubscriber? = null
    //private var uploadNum = 0
    var uploadFileList: MutableList<ImageInfo> = ArrayList()    //上传文件集合
    private var progress = 0
    private var isCancelPost = false
    var isLoadAttachmentFinish = false  //是否加载完附件数据，加载完才可以点击上传，控制上传文件的最大值

    /**
     * 图片错误数
     */
    private var errorCount = 0
    /**
     * 删除的附件的位置
     */
    private var deletePosition = 0
    private var attachmentShrink = true    //附件是否收缩

    override fun getModel(): ReturnedAuditDetailMImp = ReturnedAuditDetailMImp()

    override fun setDefaultValue() {
        if (!isViewAttached) {
            return
        }

        if (null == entity) {
            return
        }

        uploadFileList.clear()

        view.initRecyclerView()
        view.initAttachmentView()

        if (model.isWithhold(schedulePos)) {
            //获取代扣信息
            getWithholdFromService()
        } else if (model.isGoldInfo(schedulePos)) {
            //获取金账户信息
            getGoldInfoFromService()
        }

        //获取贷款信息
        getData(requestCodeWithLoan, model.getParam(mutableListOf<Any>().apply {
            add("${entity?.ID ?: 0}")
            add("1")//是否移动端请求 1：是  0：否 可选，不传默认非移动端
        }, "GetBookLoans"), true)

        view.setWaitForCheckLayoutVisibility(if (model.isShowWaitForCheckLayout(schedulePos, auditPos) and (null != entity)) View.VISIBLE else View.GONE)
        view.setWithholdLayoutVisibility(if (model.isShowWithholdLayout(schedulePos, auditPos) and (null != entity)) View.VISIBLE else View.GONE)

        attachmentShrink = true
        getAttachmentFromService()
    }

    /**
     * 从后台获取附件数据
     */
    fun getAttachmentFromService() {
        //获取附件数据
        getData(requestFileCheck, model.getFileCheckParams(mutableListOf<Any>().apply {
            add("t_books_pic")
            add("BookID= ${entity?.ID} and DelMarker=0 ")
            add(" Id,ContractID,BookID,InsertTime, LP1 ")
        }), true)
    }

    /**
     * 获取代扣信息
     */
    fun getWithholdFromService() {
        getData(requestCodeWithhold, model.getWithholdParams(mutableListOf<Any>().apply {
            add("T_Loan_Protocol")
            add("ID = ${entity?.ProtocolID ?: ""}")
        }, "GetPermitData"), true)


//        getData(requestCodeWithhold, model.getWithholdParams(mutableListOf<Any>().apply {
//            //            add("T_Loan_Protocol")
////            add("ID=" + entity?.ProtocolID +" and DelMarker=0" )
//            add(entity?.CompanyId ?: "")
//            add(entity?.AisleType ?: "")
//            add(entity?.CustomerIDs ?: "")
//        }), true)
    }

    /**
     * 获取金账户信息
     */
    fun getGoldInfoFromService() {
        getData(requestCodeGold, model.getGoldInfoParams(mutableListOf<Any>().apply {
            add("t_jzh_account")
            add("ID=" + entity?.ProtocolID + " and DelMarker=0")
            add("ID,CstmNm,(select top 1  BankName from PayCenter.dbo.PaymentBank where ID=BankId)+BankNm as BankNm ,BankCardNo,Rem")
        }), true)
    }

    fun getDefaultIntent(intent: Intent) {
        entity = intent.getSerializableExtra(Constants.BUNDLE_RETURED_AUDIT_ENTITY) as ReturnedAuditEntity?
        schedulePos = intent.getIntExtra(Constants.BUNDLE_RETURED_AUDIT_SCHEDULE_POS, -1)
        auditPos = intent.getIntExtra(Constants.BUNDLE_RETURED_AUDIT_AUDIT_POS, -1)

    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (!isViewAttached) {
            return
        }
        try {
            when (resultCode) {
                requestCodeWithLoan -> {
                    //贷款信息
                    if (!data?.data.isNullOrBlank()) {
                        jsonArray = JSONArray.parseArray(data?.data ?: "")
                        if (jsonArray.isNotEmpty()) {
                            view.setLoanNewData(getItemData1(true, jsonArray))
                        }
                    }
                }
                requestCodeWithhold -> {
                    if (!data?.data.isNullOrBlank()) {
                        //代扣信息不是所有人都是看到数据的，只有相关人员能看到
                        val parseArray = JSON.parseArray(data?.data, ReturnedAuditWithholdEntity::class.java)
                        if (parseArray.size > 0) {
                            withholdEntity = parseArray[0]
                        }
                    }

                    if (null == withholdEntity) {
                        //不能看到数据的，也显示这个模块，只是value 为""
                        withholdEntity = ReturnedAuditWithholdEntity()
                    }

                    view.setNewData(getItemData2(true, true))
                }
                requestCodeGold -> {
                    if (!data?.data.isNullOrBlank()) {
                        val parseArray = JSON.parseArray(data?.data, ReturnedAuditGoldEntity::class.java)
                        if (parseArray.size > 0) {
                            goldEntity = parseArray[0]
                            view.setNewData(getItemData2(true, true))
                        }
                    }

                }
                requestActionCheckPass, requestActionCheckReject, requestActionWithhold, requestActionCheckPassSpecific -> {
                    finishWithAction()
                }
                requestFileCheck -> {
                    if (!data?.data.isNullOrBlank()) {
                        val parseArray = JSON.parseArray(data?.data, ReturnedAttachmentEntity::class.java)
                        fileData.clear()
                        for (i in parseArray.indices) {
                            val itemEntity = parseArray.get(i)
                            fileData.add(CommonItem<Any>().apply {
                                type = 16
                                isClick = true
                                //cotnent 保存 ContractID
                                content = itemEntity?.ContractID ?: ""
                                date = Utils.FormatTime(itemEntity?.InsertTime
                                        ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd HH:mm:ss")
                                //过滤后台的脏数据
                                if ("1900/01/01" == date) {
                                    date = ""
                                }
                                //用 remark 字段保存文件对于 id
                                remark = itemEntity?.Id.toString()
                                //payType 保存 文件类型 LP1
                                payType = itemEntity?.LP1.toString()
                                //用 name 保存 回款列表Id
                                name = itemEntity?.BookID
                            })
                        }

                        isLoadAttachmentFinish = true
                        getAttachmentItemData(attachmentShrink)
                        view.setNewAttachmentData(getAttachmentItemData(attachmentShrink))
                    }
                }
                requestFileDelete -> {
                    view.showShort(R.string.returned_audit_delete_success_toast)
                    fileData.removeAt(deletePosition)
                    view.setNewAttachmentData(getAttachmentItemData(false))
                }
            }
        } catch (ex: Exception) {
            if (isViewAttached) {
                view.showMsg(ex.toString())
            }
        }

    }

    override fun onFailed(resultCode: Int, msg: String?) {
        if (isViewAttached) {
            view.showMsg(msg)
        }
    }

    /**
     * 获取附件数据
     */
    fun getAttachmentItemData(isShrink: Boolean) = mutableListOf<CommonItem<Any>>().apply {
        if (isViewAttached) {
            add(CommonItem<Any>().apply {
                type = 5
                name = view.getString(R.string.returned_audit_upload_title)
                isEnable = false
                isClick = isShrink
            })
            for (item in fileData) {
                add(item.apply {
                    isClick = isShrink
                })
            }
            add(CommonItem<Any>().apply {
                type = 17
                isClick = isShrink
            })
        }
    }

    //审核信息
    fun getItemData(isShrink: Boolean) = mutableListOf<CommonItem<Any>>().apply {
        if (isViewAttached) {
            //金账户模块，审核信息，没有 审核人，审核日期，审核备注
            //现金／转账，代扣模块 中的 「待审核」 审核信息，没有 入账时间，收款时间，审核人，审核日期，审核备注

            for (i in 0..12) {
                add(CommonItem<Any>().apply {
                    isClick = isShrink
                    when (i) {
                        0 -> {
                            type = 5
                            name = view.getString(R.string.base_returned_info)
                            isEnable = false
                        }
                        1 -> {
                            type = 2
                            name = view.getString(R.string.base_num) + blank + "        "
                            content = entity?.ID.toString()
                        }
                        2 -> {
                            type = 2
                            name = view.getString(R.string.base_affiliating_area) + blank
                            content = entity?.CompanyZoneName ?: ""

                        }
                        3 -> {
                            type = 2
                            name = view.getString(R.string.base_affiliating_shop) + blank
                            content = entity?.CompanyId + entity?.CompanyName
                        }
                        4 -> {
                            type = 2
                            name = view.getString(R.string.base_salesman) + blank + blankFor3
                            content = entity?.ServiceIDName ?: ""

                        }
                        5 -> {
                            type = 2
                            name = view.getString(R.string.base_affiliating_custom) + blank
                            content = entity?.CustomerNames ?: ""
                        }
                        6 -> {
                            type = 2
                            name = view.getString(R.string.base_in_or_out_type) + blank
                            content = entity?.BookTypeName ?: ""
                        }
                        7 -> {
                            type = 2
                            name = view.getString(R.string.base_pay_way) + blank
                            content = BusinessUtils.getPayType(entity?.PayType ?: -1)
                        }
                        8 -> {
                            type = 2
                            name = view.getString(R.string.base_deal_status) + blank
                            content = BusinessUtils.getBusinessState(entity?.PayType
                                    ?: 0, entity?.AisleType ?: "", entity?.BusinessState ?: 0)
                        }
                        9 -> {
                            type = 2
                            name = view.getString(R.string.base_returned_amount) + blank
                            //content = view.getString(R.string.base_unit_yuan, entity?.Amount)
                            content = view.getString(R.string.base_unit_yuan, BusinessUtils.getBigDecimalToDobule(entity?.Amount
                                    ?: 0.0))
                        }
                        10 -> {
                            type = 2
                            name = view.getString(R.string.base_pay_time) + blank
                            content = Utils.FormatTime(entity?.PayTime
                                    ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")
                            //过滤后台的脏数据
                            if ("1900/01/01" == content) {
                                content = ""
                            }
                        }
                        11 -> type = 4
                        12 -> type = 0
                    }
                })
            }
        }
    }

    //贷款信息
    fun getItemData1(isShrink: Boolean, jsonArray: JSONArray) = mutableListOf<CommonItem<Any>>().apply {
        if (isViewAttached) {
            add(CommonItem<Any>().apply {
                isClick = isShrink
                type = 5
                name = "${view.getString(R.string.base_loan_info)}（${jsonArray.size}）"
                isEnable = false
            })
            if (jsonArray.isNotEmpty()) {
                for (j in jsonArray.indices) {
                    add(CommonItem<Any>().apply {
                        type = 8
                        isClick = isShrink
                        val jsonObject = jsonArray.getJSONObject(j)
                        name = jsonObject.getString("LoanCode")
                        content = entity?.ServiceIDName ?: ""
                        hintContent = jsonObject.getString("MortgageName")
                        date = jsonObject.getString("CustomerNames")
                        remark = jsonObject.getString("ProductName")
                        payType = view.getString(R.string.base_unit_yuan, BusinessUtils.getBigDecimalToDobule(jsonObject.getDoubleValue("LoanAmount")))
                        if (j == jsonArray.size - 1) isLineShow = true
                    })
                }
            }
            add(CommonItem<Any>().apply {
                isClick = isShrink
                type = 0
            })
        }
    }

    //审核信息和代扣金账户信息
    fun getItemData2(isShrink: Boolean, isShrinkSecond: Boolean) = mutableListOf<CommonItem<Any>>().apply {
        if (isViewAttached) {
            for (i in 0..9) {
                add(CommonItem<Any>().apply {
                    isClick = isShrink
                    when (i) {
                        0 -> {
                            type = 5
                            name = view.getString(R.string.base_auditor_info)
                            isEnable = false
                        }
                        1 -> {
                            type = 2
                            name = view.getString(R.string.base_operator) + blank + blankFor3
                            content = entity?.ServicePeopleName
                        }
                        2 -> {
                            type = 2
                            name = view.getString(R.string.base_audit_status) + blank
                            content = BusinessUtils.getState(entity?.PayType, entity?.State)
                            icon = R.color.detail3
                        }
                        3 -> {
                            type = 2
                            name = view.getString(R.string.base_recorded_time) + blank
                            content = Utils.FormatTime(entity?.RecordTime
                                    ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd HH:mm:ss")
                            //过滤后台的脏数据
                            if ("1900/01/01" == content) {
                                content = ""
                            }
                            //如果是待审核环节，则隐藏
                            isClick = if (model.isShowWaitForCheckLayout(schedulePos, auditPos)) true else isShrink
                        }
                        4 -> {
                            type = 2
                            name = view.getString(R.string.base_collection_date) + blank
                            content = Utils.FormatTime(entity?.HeadReceiveTime
                                    ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")
                            //过滤后台的脏数据
                            if ("1900/01/01" == content) {
                                content = ""
                            }
                            //如果是待审核环节，则隐藏
                            isClick = if (model.isShowWaitForCheckLayout(schedulePos, auditPos)) true else isShrink
                        }
                        5 -> {
                            type = 2
                            name = view.getString(R.string.base_auditor) + blank + blankFor3
                            content = entity?.AuditorName ?: ""

                            //如果是待审核环节 + 金账户，则隐藏
                            if (model.isShowWaitForCheckLayout(schedulePos, auditPos) || entity?.PayType == 4) {
                                isClick = true
                            } else {
                                isClick = isShrink
                            }
                        }
                        6 -> {
                            type = 2
                            name = view.getString(R.string.base_auditor_date) + blank
                            content = Utils.FormatTime(entity?.AuditTime
                                    ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")
                            //过滤后台的脏数据
                            if ("1900/01/01" == content) {
                                content = ""
                            }
                            //如果是待审核环节 + 金账户，则隐藏
                            if (model.isShowWaitForCheckLayout(schedulePos, auditPos) || entity?.PayType == 4) {
                                isClick = true
                            } else {
                                isClick = isShrink
                            }
                        }
                        7 -> {
                            type = 2
                            name = view.getString(R.string.base_auditor_remarks) + blank
                            content = entity?.AuditRemark ?: ""
                            //如果是待审核环节 + 金账户，则隐藏
                            if (model.isShowWaitForCheckLayout(schedulePos, auditPos) || entity?.PayType == 4) {
                                isClick = true
                            } else {
                                isClick = isShrink
                            }
                        }
                        8 -> type = 4
                        9 -> type = 0
                    }
                })
            }
            if (model.isGoldInfo(schedulePos) and (null != goldEntity)) {
                //金账户信息
                for (i in 0..8) {
                    add(CommonItem<Any>().apply {
                        isClick = isShrinkSecond
                        when (i) {
                            0 -> {
                                type = 5
                                name = view.getString(R.string.base_gold_info) + blank
                                isEnable = false
                            }
                            1 -> {
                                type = 2
                                name = view.getString(R.string.base_amount) + blank + "        "
                                content = view.getString(R.string.base_unit_yuan, BusinessUtils.getBigDecimalToDobule(entity?.Amount
                                        ?: 0.0))
                            }
                            2 -> {
                                type = 2
                                name = view.getString(R.string.base_gold_signatory_bank) + blank
                                content = goldEntity?.BankNm ?: ""
                                icon = R.color.detail3
                            }
                            3 -> {
                                type = 2
                                name = view.getString(R.string.base_gold_account_name) + blank + "        "
                                content = goldEntity?.CstmNm ?: ""
                            }
                            4 -> {
                                type = 2
                                name = view.getString(R.string.base_gold_bank_account) + blank5
                                content = goldEntity?.BankCardNo ?: ""
                            }
                            5 -> {
                                type = 2
                                name = view.getString(R.string.base_gold_remark) + blank5
                                content = goldEntity?.Rem ?: ""
                            }
                            6 -> {
                                type = 2
                                name = view.getString(R.string.base_receipt_date) + blank
                                content = Utils.FormatTime(entity?.HeadReceiveTime
                                        ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")
                                //过滤后台的脏数据
                                if ("1900/01/01" == content) {
                                    content = ""
                                }
                            }
                            7 -> type = 4
                            8 -> type = 0
                        }
                    })

                }

            } else if (model.isWithhold(schedulePos) and (null != withholdEntity)) {
                //代扣信息
                for (i in 0..9) {
                    add(CommonItem<Any>().apply {
                        isClick = isShrinkSecond
                        when (i) {
                            0 -> {
                                type = 5
                                name = view.getString(R.string.base_withhold_info)
                                isEnable = false
                            }
                            1 -> {
                                type = 2
                                name = view.getString(R.string.base_withhold_amount) + blank
                                //content = view.getString(R.string.base_unit_yuan, entity?.Amount)
                                content = view.getString(R.string.base_unit_yuan, BusinessUtils.getBigDecimalToDobule(entity?.Amount
                                        ?: 0.0))
                            }
                            2 -> {
                                type = 2
                                name = view.getString(R.string.base_withhold_platform) + blank
                                content = BusinessUtils.getAisleType(withholdEntity?.AisleType
                                        ?: "")
                            }
                            3 -> {
                                type = 2
                                name = view.getString(R.string.base_account_of_payment_name) + blank
                                content = withholdEntity?.CardName ?: ""
                            }
                            4 -> {
                                type = 2
                                name = view.getString(R.string.base_account_of_payment) + blank
                                content = withholdEntity?.BankCardNo ?: ""
                            }
                            5 -> {
                                type = 2
                                name = view.getString(R.string.base_reserved_phone) + blank
                                content = withholdEntity?.ReservePhone ?: ""
                            }
                            6 -> {
                                type = 2
                                name = view.getString(R.string.base_withhold_protocol) + blank
                                content = withholdEntity?.ProtocolNo ?: ""
                            }
                            7 -> {
                                type = 2
                                name = view.getString(R.string.base_receipt_date) + blank
                                content = Utils.FormatTime(entity?.HeadReceiveTime
                                        ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")
                                //过滤后台的脏数据
                                if ("1900/01/01" == content) {
                                    content = ""
                                }
                            }
                            8 -> type = 4
                            9 -> type = 0
                        }
                    })
                }
            }
        }
    }

    /**
     * 点击通过 ／ 拒绝按钮
     */
    fun clickPassOrRejectBtn(auditPass: Boolean) {
        if (!isViewAttached) {
            return
        }

        if (null == entity) {
            return
        }

        if (entity?.PayType == 3 && null == withholdEntity) {
            return
        }

        if (auditPass) {
            //如果有收款时间，就默认选择收款日期
            val selectedData = Calendar.getInstance()
            if (null != entity && !(entity?.HeadReceiveTime.isNullOrBlank())) {
                val timeLong = DateUtil.getTimeLong(entity?.HeadReceiveTime)
                if (timeLong > DateUtil.getTimeLong("1900-01-01")) {
                    selectedData.time = Date(timeLong)
                }
            }
            view.showCalendarDialog(selectedData)
        } else {
            getCheckAction(false)
        }
    }


    /**
     * 待审核 - 通过 / 拒绝
     */
    fun getCheckAction(auditPass: Boolean, date: Date = Date()) {
        if (auditPass) {
            val payType = entity?.PayType
            when (payType) {
                1, 2 -> {
                    getChenckActionNormal(auditPass, date)
                }
                3 -> {
                    if (withholdEntity?.AisleType == "1" || withholdEntity?.AisleType == "2"
                            || withholdEntity?.AisleType == "5" || withholdEntity?.AisleType == "6") {
                        //通联支付1，上海富友2，通联协议5,宝付代扣6
                        getChenckActionSpecific(date)
                    } else if (withholdEntity?.AisleType == "3" || withholdEntity?.AisleType == "4") {
                        //宝付3，通菀4
                        getChenckActionNormal(auditPass, date)
                    } else {
                        view.showShort(R.string.returned_audit_aisleType_unknown)
                    }
                }
            }

        } else {
            getChenckActionNormal(auditPass, date)
        }

    }


    /**
     * 待审核 通过／ 拒绝 （通用）
     */
    fun getChenckActionNormal(auditPass: Boolean, date: Date) {
        getData(requestActionCheckPass, model.getCheckActionParams(mutableListOf<Any>().apply {
            val map = HashMap<String, Any>()
            map[Constants.HTTP_PARAM_BOOK_ID] = entity?.ID ?: 0
            map[Constants.HTTP_PARAM_AUDIT_PASS] = auditPass
            map[Constants.HTTP_PARAM_PAY_TIME] = entity?.PayTime ?: ""
            if (auditPass) {
                map[Constants.HTTP_PARAM_COLLECT_TIME] = DateUtil.getCurDateDouble(date.time)
            }
            map[Constants.HTTP_PARAM_PAY_MONEY] = entity?.Amount ?: ""
            map[Constants.HTTP_PARAM_REMARK] = ""
            add(JSONObject.toJSONString(map))
        }), true)
    }

    /**
     * 待审核 通过 （特殊的）
     */
    fun getChenckActionSpecific(date: Date) {
        getData(requestActionCheckPassSpecific, model.getCheckActionSepcailParams(mutableListOf<Any>().apply {
            add(entity?.ID ?: 0)
            add(entity?.Amount ?: "")
            add(entity?.ProtocolID ?: "")
            //add(entity?.Auditor?: "") //传的是当前用户 id
            add(Hawk.get("UserID"))
            add(entity?.HeadReceiveTime ?: "")
            add(DateUtil.getCurDateDouble(date.time))
            add(entity?.AuditRemark ?: "")
        }), true)
    }


    /**
     * 发起代扣
     */
    fun getWithholdAction() {
        if (null == withholdEntity) {
            return
        }

        getData(requestActionWithhold, model.getWithholdActionParams(mutableListOf<Any>().apply {
            val map = HashMap<String, Any>()
            map[Constants.HTTP_PARAM_PROTOCOL_ID] = entity?.ProtocolID ?: 0
            map[Constants.HTTP_PARAM_COMPANY_ID_] = entity?.CompanyId ?: 0
            map[Constants.HTTP_PARAM_BUSNES_CODE] = "19900" //固定19900
            map[Constants.HTTP_PARAM_CURRENCY] = 0
            map[Constants.HTTP_PARAM_AMOUNT] = entity?.Amount ?: 0.0
            map[Constants.HTTP_PARAM_PROTOCOL_NO] = withholdEntity?.ProtocolNo ?: ""
            map[Constants.HTTP_PARAM_BSNSIDS] = entity?.ID.toString()
            map[Constants.HTTP_PARAM_OPERATE_ID] = Hawk.get<String>("UserID")
            map[Constants.HTTP_PARAM_ORIGIN_CODE] = 4
            add(JSONObject.toJSONString(map))
        }), true)
    }

    /**
     * 按钮动作提交后，关闭界面，并通知上一个界面刷新
     */
    private fun finishWithAction() {
        if (!isViewAttached) {
            return
        }

        view.showShort(R.string.base_operate_successfully)
        view.setResult(Activity.RESULT_OK)
        view.finish()
    }

    override fun permissionSuccess(code: Int) {
        when (code) {
            Constants.REQUEST_CODE_PERMISSION_MORE -> view.showIconPop()
        }
    }

    fun uploadFile(dataList: MutableList<ImageInfo>) {
        if (!isViewAttached) {
            return
        }

        view.showProgressDialog(dataList.size)
        uploadFileList.clear()
        uploadFileList.addAll(dataList)
        errorCount = 0
        isCancelPost = false

        if (uploadFileList.isNotEmpty()) {
            uploadFile(uploadFileList[0])
        }
    }


    /**
     * 上传一个文件
     */
    fun uploadFile(item: ImageInfo) {
        if (uploadSubscription == null) {
            uploadSubscription = UploadSubscriber()
        }

        val fileByte = BitmapManage.compressImage(if (item.sourcePath.isNullOrBlank()) item.thumbnailPath else item.sourcePath)
        val SessionID = Hawk.get<String>("SessionID")
        val list = ArrayList<Any>().apply {
            add("DB_CFS_Loan")
            add("1")
            //add(entity?.ServicePeople ?: 0) //传的是当前用户 id
            add(Hawk.get("UserID"))
            add(entity?.ContractNo ?: "")
            add(entity?.ID ?: 0)
        }
        HttpAction.getInstance().upLoadPortrait(uploadSubscription, SessionID, getBase64(JSON.toJSONString(list)), getBase64("BooksPicUpFile"), getBase64(""), fileByte)

    }


    private fun getBase64(str: String): String {
        return Base64.encodeToString(str.toByteArray(), Base64.NO_WRAP)
    }

    private inner class UploadSubscriber : Subscriber<ResponseData>() {
        override fun onCompleted() {
        }

        override fun onError(e: Throwable) {
            deUploadSubscription()
            if (uploadFileList.size > 0) {
                uploadFileList.removeAt(0)
            }

            uploadOver()
        }

        override fun onNext(data: ResponseData) {
            deUploadSubscription()
            if (uploadFileList.size > 0) {
                uploadFileList.removeAt(0)
            }


            if (data.executeResult!!) {
                //上传成功

            } else {
                //上传失败
                errorCount++
            }


            uploadOver()
        }
    }

    private fun deUploadSubscription() {
        if (uploadSubscription != null && !uploadSubscription?.isUnsubscribed!!) {
            uploadSubscription?.unsubscribe()
            uploadSubscription = null
        }
    }

    private fun uploadOver() {
        if (isCancelPost) {

            //获取数据，刷新附件
            getAttachmentDataList()
            isCancelPost = false
            return
        }

        progress++
        view.setPostProgress(progress)

        if (uploadFileList.size > 0) {
            //还有没上传完的
            uploadFile(uploadFileList[0])
        } else {
            view.hideBar()
            if (errorCount > 0) {
                view.showMsg("${errorCount}个附件上传失败")
                errorCount = 0
            } else {
                //全部上传成功，提示
                view.showMsg("上传成功")
            }
            attachmentShrink = false
            isLoadAttachmentFinish = false
            //获取数据，刷新附件
            getAttachmentFromService()
        }
    }

    fun getAttachmentDataList(): List<ImageInfo> {
        return uploadFileList
    }

    /**
     * 点击对话框，删除确定按钮回调
     */
    fun clickDeleteFile(position: Int) {
        deletePosition = position

        if (fileData.size > position) {
            val entity = fileData[position]
            getData(requestFileDelete, model.getFileDeleteParams(mutableListOf<Any>().apply {
                //用 remark 字段保存 id
                add(entity.remark)
            }), true)
        }


    }

    /**
     * 点击附件上传按钮
     */
    fun clickUploadFile() {
        if (!isViewAttached) {
            return
        }

        //加载完附件数据，才能上传
        if (isLoadAttachmentFinish) {
            if (fileData.size >= Constants.MAX_IMAGE_SIZE) {
                view.showShort(R.string.returned_audit_upload_max)
            } else {
                view.getPermissions()
            }
        }
    }

    fun cancelPost() {
        isCancelPost = true
    }


    //获取可上传图片张数
    fun getAvailableSize(): Int {
        val availSize = Constants.MAX_IMAGE_SIZE - fileData.size
        return if (availSize >= 0) {
            availSize
        } else 0
    }
}