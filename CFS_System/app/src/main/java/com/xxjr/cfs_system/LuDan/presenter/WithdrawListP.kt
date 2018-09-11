package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.bigkoo.pickerview.TimePickerView
import com.orhanobut.logger.Logger
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xxjr.cfs_system.LuDan.model.ModelImp
import com.xxjr.cfs_system.LuDan.view.activitys.withdraw_wallet.WithdrawListActivity
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import java.util.*

class WithdrawListP : BasePresenter<WithdrawListActivity, ModelImp>() {
    private lateinit var pvTime: TimePickerView
    lateinit var time: String
    private var type = 0 // 0->金账户 1->银行卡

    override fun getModel(): ModelImp = ModelImp()

    override fun setDefaultValue() {
        time = DateUtil.getFormatDate(Date())
        if (isViewAttached) {
            val selectedDate = Calendar.getInstance()
            val startDate = Calendar.getInstance()
            val endDate = Calendar.getInstance()
            //正确设置方式 原因：注意事项有说明
            startDate.set(2013, 0, 1)
            endDate.set(2060, 11, 31)

            pvTime = TimePickerView.Builder(view as Context, TimePickerView.OnTimeSelectListener { date, tvTime ->
                //选中事件回调
                time = DateUtil.getFormatDate(date)
                (tvTime as TextView).text = Utils.FormatTime(time, "yyyy-MM-dd", "yyyy-MM月")
                getWithdrawListData(time)
            })
                    .setType(booleanArrayOf(true, true, false, false, false, false))// 默认全部显示
                    .setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setContentSize(18)//滚轮文字大小
                    .setTitleText("选择时间")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .setTitleColor(Color.BLACK)//标题文字颜色
                    .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
                    .setSubmitColor((view as Context).resources.getColor(R.color.font_home))//确定按钮文字颜色
                    .setCancelColor((view as Context).resources.getColor(R.color.font_home))//取消按钮文字颜色
                    .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                    .setRangDate(startDate, endDate)//起始终止年月日设定
                    .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .isDialog(false)//是否显示为对话框样式
                    .build()
        }
    }

    //时间选择
    fun timeShow(textView: TextView) {
        if (isViewAttached) {
            pvTime.show(textView)
        }
    }

    //获取钱包列表
    fun getWithdrawListData(time: String) {
        getData(0, getWithdrawParam(mutableListOf(time), "BonusLeger", "AppGetBonusLegerList"), true)
    }

    //提现
    fun withdrawMoney(burseId: Int, JzhNo: String, amount: String, type: Int) {
        this.type = type
        getData(1, getWithdrawParam(mutableListOf<Any>().apply {
            add(burseId) //钱包id
            add(JzhNo) //金账户账号
            add(amount) //提现金额
            add("4") //操作客户端类型 4->Android
        }, "BonusLeger", "WithdrawBonusLeger"), true)
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
        if (isViewAttached) {
            when (resultCode) {
                0 -> {
                    if ((data?.returnString ?: "").isNotBlank()) {
                        val jsonObject = JSONObject.parseObject(data?.returnString ?: "")
                        view.setAmountShow(jsonObject.getString("PreLedger")
                                ?: "0", jsonObject.getString("PreWithdraw") ?: "0")
                    }
                    if ((data?.data ?: "").isNotBlank()) {
                        val jsonArray = JSONArray.parseArray(data?.data ?: "")
                        if (jsonArray.isNotEmpty()) {
                            view.refreshData(getWithdrawListItemData(jsonArray))
                        } else {
                            view.refreshData(mutableListOf())
                        }
                    } else {
                        view.refreshData(mutableListOf())
                    }
                }
                1 -> {//提现
                    when (type) {
                        0 -> {
                            view.showMsg("提现成功")
                            getWithdrawListData(time)
                        }
                        1 -> view.toGoldWeb()
                    }
                }
            }
        }
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    //通用参数组装
    private fun getWithdrawParam(list: MutableList<Any>, tranName: String, action: String): String {
        val map = HashMap<String, Any>()
        map["Action"] = action
        map["ParamString"] = list
        map["TranName"] = tranName
        Logger.e("==钱包==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    //获取钱包列表数据
    private fun getWithdrawListItemData(jsonArray: JSONArray) = mutableListOf<CommonItem<Any>>().apply {
        for (i in jsonArray.indices) {
            val jsonObject = jsonArray.getJSONObject(i)
            if (jsonObject.isNotEmpty()) {
                add(CommonItem<Any>().apply {
                    position = jsonObject.getIntValue("ID")//钱包id
                    name = jsonObject.getString("LoanType")
                    content = jsonObject.getString("Amount")
                    hintContent = jsonObject.getString("BonusAmount") ?: "0.0"
                    payType = when (jsonObject.getIntValue("BonusType")) {
                    //提成类型 1 预发、2 月金、3 季金、4 年金
                        1 -> "预发"
                        2 -> "月金"
                        3 -> "季金"
                        4 -> "年金"
                        else -> ""
                    }
                    date = if (jsonObject.getString("LedgerTime") == null) "" else Utils.getTime(jsonObject.getString("LedgerTime"))
                    type = jsonObject.getIntValue("State")
                    remark = jsonObject.getString("JzhNo") ?: ""//金账户账号
                })
            }
        }
    }
}