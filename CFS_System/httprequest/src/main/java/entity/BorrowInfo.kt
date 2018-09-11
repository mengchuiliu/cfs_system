package entity

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.alibaba.fastjson.JSONArray
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Created by Administrator on 2017/10/27.
 * 拆借信息实体类
 */
class BorrowInfo : Serializable {
    var contractID: Int? = null//合同ID
    var borrowId: Int? = null //拆借ID
    var borrowTypeId: Int? = null //拆借类型ID
    var borrowDate: String? = null //拆借日期
    var borrowAmount: Double? = null //拆借金额
    var borrowInterest: Double? = null //拆借利息
    var predictBorrowDate: String? = null //预计结算日期
    var predictBorrowIncome: Double? = null //预计拆借创收
    var rePayTypeId: Int? = null //还款方式id
    var borrowProposer: String? = null//拆借申请人
    var serviceID: String? = null//拆借申请人Id
    var borrowState: Int? = null //拆借状态
    var borrowRemark: String? = null //拆借备注

    var customerIds: String? = null //客户id
    var customerNames: String? = null //客户姓名

    var approveName: String? = null//拆借审批人
    var approveIds: String? = null//拆借审批人
    var settlementDate: String? = null//实际结算日期
    var income: Double? = null//实际创收
    var backPayment: Double? = null //实际回款
    var followContent: JSONArray? = null//进度集合
    var contractCode: String? = null//合同编号
    var updateTime: String? = null//更新时间

    fun borrowCheck(activity: Context): Boolean {
        when {
            borrowTypeId ?: 0 == 0 -> {
                show(activity, "请选择拆借类型")
                return false
            }
            borrowDate.isNullOrBlank() -> {
                show(activity, "请选择拆借日期")
                return false
            }
            borrowAmount ?: 0.0 == 0.0 -> {
                show(activity, "请正确的填写拆借金额")
                return false
            }
            borrowInterest ?: 0.0 == 0.0 -> {
                show(activity, "请正确的填写拆借利息")
                return false
            }
            predictBorrowDate.isNullOrBlank() -> {
                show(activity, "请选择预计结算日期")
                return false
            }
            getTimeLong(predictBorrowDate!!) < getTimeLong(borrowDate!!) -> {
                show(activity, "预计结算日期不能小于拆借日期")
                return false
            }
            rePayTypeId ?: 0 == 0 -> {
                show(activity, "请选择还款方式")
                return false
            }
        }
        return true
    }


    /**
     * 将 2000-1-1类型数据转换为long时间戳
     */
    @SuppressLint("SimpleDateFormat")
    fun getTimeLong(str: String): Long {
        var dLong = 0L
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = dateFormat.parse(str)
            dLong = date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return dLong
    }

    private fun show(context: Context, str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }
}