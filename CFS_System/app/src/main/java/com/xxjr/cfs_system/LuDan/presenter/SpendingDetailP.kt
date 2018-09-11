package com.xxjr.cfs_system.LuDan.presenter

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.spending_audit.SpendingDetailActivity
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import java.util.HashMap

class SpendingDetailP : BasePresenter<SpendingDetailActivity, ModelImp>() {
    private var blank = "            "
    private var fileData = mutableListOf<CommonItem<Any>>()
    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        getData(1, getSpendParam(mutableListOf<Any>().apply {
            add(view.getSpendingInfo().RecordType ?: "")
            add(view.getSpendingInfo().RecordId)
            add(view.getSpendingInfo().ContractId)
        }, "GetRecordFile", "ConstantPayout"), true)
    }

    fun auditSpending(isPass: String) {
        getData(0, getSpendParam(mutableListOf<Any>().apply {
            add(when (view.getSpendingInfo().RecordType ?: "") {
                "1" -> "t_room_rent_record"
                "2" -> "t_managementfee_record"
                else -> ""
            })
            add(view.getSpendingInfo().RecordId)
            add(view.getSpendingInfo().CompanyID ?: "")
            add(view.getSpendingInfo().AuditState)
            add(isPass)
            add(view.getRemark())
        }, "AUDIT", "Audit"), true)
    }

    //通用参数组装
    fun getSpendParam(list: List<Any>, Action: String, TranName: String): String {
        val map = HashMap<String, Any>()
        map["Action"] = Action
        map["ParamString"] = list
        map["UserId"] = Hawk.get<String>("UserID")
        map["TranName"] = TranName
        Logger.e("==审核==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> view.complete()
                1 -> {
                    if ((data?.data ?: "").isNotBlank()) {
                        val jsonArray = JSONArray.parseArray(data?.data ?: "")
                        if (jsonArray.isNotEmpty()) {
                            for (i in jsonArray.indices) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                fileData.add(CommonItem<Any>().apply {
                                    type = 6
                                    isClick = true
                                    name = jsonObject.getString("F2")
                                    content = jsonObject.getString("F5")
                                    remark = jsonObject.getString("F6")
                                    hintContent = jsonObject.getString("FileGuid")//文件id
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun getItemData(isShrink: Boolean, isFileShrink: Boolean) = mutableListOf<CommonItem<Any>>().apply {
        for (i in 0..23) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> {
                        type = 5
                        name = "费用信息"
                    }
                    1 -> {
                        type = 2
                        name = "费用项目$blank"
                        content = when (view.getSpendingInfo().ExpenseItem) {
                            0 -> "办公场地"
                            1 -> "设备机房"
                            2 -> "房屋综合税"
                            3 -> "印花税"
                            4 -> "物业费"
                            5 -> "水电费"
                            6 -> "物业费+水电费"
                            else -> ""
                        }
                        icon = R.color.font_home
                    }
                    2 -> {
                        type = 2
                        name = "所属门店$blank"
                        content = view.getSpendingInfo().Company ?: ""
                    }
                    3 -> {
                        type = 2
                        name = "缴费周期$blank"
                        content = "${Utils.FormatTime(view.getSpendingInfo().StartDate
                                ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")}—${Utils.FormatTime(view.getSpendingInfo().EndDate
                                ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")}"
                    }
                    4 -> {
                        type = 2
                        name = "支付金额$blank"
                        content = "${view.getSpendingInfo().Amount}"
                        icon = R.color.detail1
                    }
                    5 -> {
                        type = 2
                        name = "审核进度$blank"
                        content = when (view.getSpendingInfo().AuditState) {
                            1 -> "待区域主管审核"
                            2 -> "待总部财务审核"
                            -1 -> "待出纳付款"
                            -3 -> "已付款"
                            -2 -> "已拒绝"
                            else -> ""
                        }
                    }
                    6 -> {
                        type = 2
                        name = "收款人$blank    "
                        content = view.getSpendingInfo().Receiver ?: ""
                    }
                    7 -> {
                        type = 2
                        name = "收款账号$blank"
                        content = view.getSpendingInfo().ReceiveAccount ?: ""
                    }
                    8 -> {
                        type = 2
                        name = "收款银行$blank"
                        content = view.getSpendingInfo().ReceiverBank ?: ""
                        icon = R.color.detail1
                    }
                    9 -> {
                        type = 2
                        name = "交费日期$blank"
                        content = Utils.FormatTime(view.getSpendingInfo().PayDate
                                ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")
                    }
                    10 -> {
                        type = 2
                        name = "出账属性$blank"
                        content = when (view.getSpendingInfo().PayAccountType) {
                            1 -> "私户"
                            2 -> "公户"
                            else -> ""
                        }
                    }
                    11 -> type = 4
                    12 -> type = 0
                    13 -> {
                        type = 5
                        name = "合同信息"
                        isEnable = false
                        isClick = isShrink
                    }
                    14 -> {
                        type = 2
                        name = "门店$blank         "
                        content = view.getSpendingInfo().Company ?: ""
                        isClick = isShrink
                    }
                    15 -> {
                        type = 2
                        name = "面积$blank         "
                        content = "${view.getSpendingInfo().Area}㎡"
                        isClick = isShrink
                    }
                    16 -> {
                        type = 2
                        name = "公司地址$blank "
                        content = view.getSpendingInfo().CompanyAddress ?: ""
                        isClick = isShrink
                    }
                    17 -> {
                        type = 2
                        name = "签约人$blank     "
                        content = view.getSpendingInfo().Signatory ?: ""
                        isClick = isShrink
                    }
                    18 -> {
                        type = 2
                        name = "合同起始日期     "
                        content = Utils.FormatTime(view.getSpendingInfo().ContractST
                                ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")
                        isClick = isShrink
                    }
                    19 -> {
                        type = 2
                        name = "合同结束日期     "
                        content = Utils.FormatTime(view.getSpendingInfo().ContractET
                                ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")
                        isClick = isShrink
                    }
                    20 -> {
                        type = 2
                        name = "付费周期$blank "
                        content = "${view.getSpendingInfo().Cycle}月"
                        isClick = isShrink
                    }
                    21 -> {
                        type = 4
                        isClick = isShrink
                    }
                    22 -> type = 0
                    23 -> {
                        type = 5
                        name = "支出附件"
                        isEnable = false
                        isClick = isFileShrink
                    }
                }
            })
        }
        for (item in fileData) {
            add(item.apply { isClick = isFileShrink })
        }
    }
}