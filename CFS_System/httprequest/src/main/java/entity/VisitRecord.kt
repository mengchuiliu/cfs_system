package entity

import android.content.Context
import android.widget.Toast

/**
 * Created by Administrator on 2017/12/7.
 */
class VisitRecord {
    var ID: Int? = null//记录编号
    var VisitTime: String? = null//来访时间　
    var CompanyId: String? = null//到访门店编号
    var CompanyName: String? = null//到访门店名称
    var CustomerName: String? = null//来访客户名称
    var MobilePhone: String? = null//来访客户手机号码
    var DemandPrice: Double? = null//来访客户需求金额/贷款金额
    var LoanType: Int? = null//贷款类型
    var SalesName: String? = null//业务员名称
    var SalesPhoneNumber: String? = null//业务员电话
    var ServicePeople: String? = null//业务员编号
    var InsertTime: String? = null//记录创建时间
    var UpdateTime: String? = null//记录创建时间
    var remark: String? = null//备注

    fun check(context: Context): Boolean {
        var b = true
        if (VisitTime.isNullOrBlank()) {
            show(context, "请选择来访时间")
            b = false
        }
        if (CustomerName.isNullOrBlank()) {
            show(context, "客户名称不能为空")
            b = false
        }
        if (MobilePhone.isNullOrBlank() && MobilePhone?.length != 11) {
            show(context, "请正确的填写客户手机号码")
            b = false
        }
        if (DemandPrice == null || DemandPrice == 0.0) {
            show(context, "请正确的填写需求贷款金额")
            b = false
        }
        if (LoanType == null || LoanType == -1) {
            show(context, "请选择贷款类型")
            b = false
        }
        if (SalesPhoneNumber.isNullOrBlank() || SalesPhoneNumber?.length != 11) {
            show(context, "请正确的填写业务员手机号码")
            b = false
        }
        return b
    }

    private fun show(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun toString(): String =
            "VisitRecord(ID=$ID, VisitTime=$VisitTime, CompanyId=$CompanyId, CompanyName=$CompanyName, " +
                    "CustomerName=$CustomerName, MobilePhone=$MobilePhone, DemandPrice=$DemandPrice, LoanType=$LoanType," +
                    " SalesName=$SalesName, SalesPhoneNumber=$SalesPhoneNumber, ServicePeople=$ServicePeople)"

}