package com.xxjr.cfs_system.LuDan.view.activitys.reimbursement_remind

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.RemindListPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.LoanInfo
import kotlinx.android.synthetic.main.activity_common_list.*
import refresh_recyclerview.SimpleItemDecoration
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import java.math.BigDecimal

class RemindListActivity : BaseActivity<RemindListPresenter, RemindListActivity>(), BaseViewInter {

    override fun getPresenter(): RemindListPresenter = RemindListPresenter()

    override fun getLayoutId(): Int = R.layout.activity_common_list

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        (application as MyApplication).MsgType = 0
        toolbarTitle.text = "还款提醒列表"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        presenter.setDefaultValue()
    }

    fun initRv(loans: MutableList<LoanInfo>) {
        if (loans.isEmpty()) {
            rl_nodata.visibility = View.VISIBLE
        } else {
            rl_nodata.visibility = View.GONE
            rv_remind.layoutManager = LinearLayoutManager(this@RemindListActivity)
            rv_remind.addItemDecoration(SimpleItemDecoration(this@RemindListActivity, 10))
            rv_remind.adapter = object : CommonAdapter<LoanInfo>(this@RemindListActivity, loans, R.layout.remind_list_item) {
                override fun convert(holder: BaseViewHolder, loan: LoanInfo, position: Int) {
                    holder.setText(R.id.tv_content, loan.loanDescription ?: "")
                    holder.setText(R.id.tv_loan_code, loan.loanCode ?: "")
                    holder.setText(R.id.tv_customer, loan.customer ?: "")
                    holder.setText(R.id.tv_apply_amount, Utils.parseMoney(BigDecimal(loan.lendingAmount)) + "元")
                    holder.setText(R.id.return_day, loan.returnTime ?: "")
                    holder.convertView.setOnClickListener {
                        val intent = Intent(this@RemindListActivity, RemindDetailsActivity::class.java)
                        intent.putExtra("lendingId", loan.loanId)//放款id
                        startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }
}
