package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.adapters.AuditCostAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.presenter.AuditCostPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.AuditCostVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import entity.Cost
import kotlinx.android.synthetic.main.activity_over_apply.*
import kotlinx.android.synthetic.main.bottom_bt.*

class AuditCostActivity : BaseActivity<AuditCostPresenter, AuditCostVInter>(), AuditCostVInter, View.OnClickListener {
    private var cost: Cost? = null
    var adapter: AuditCostAdapter? = null

    override fun getLoanInfo(): Cost = cost ?: Cost()

    override fun getPresenter(): AuditCostPresenter = AuditCostPresenter()

    override fun getLayoutId(): Int = R.layout.activity_over_apply

    override fun isShowBacking(): Boolean = true

    override fun showMsg(msg: String?) = ToastShow.showShort(application, msg)

    override fun initView(savedInstanceState: Bundle?) {
        cost = intent.getSerializableExtra("loanInfo") as? Cost
        toolbarTitle.text = "成本详情"
        tv_not.text = "拒绝"
        tv_ok.text = "通过"
        tv_ok.setOnClickListener(this)
        tv_not.setOnClickListener(this)
        tv_submit.setOnClickListener(this)
        rl_botom_over.setOnClickListener(this)
        presenter.setDefaultValue()
    }

    override fun initRecycler(commonItems: MutableList<CommonItem<*>>) {
        rv_over.layoutManager = LinearLayoutManager(this@AuditCostActivity)
        adapter = AuditCostAdapter(this@AuditCostActivity, commonItems)
        adapter?.setOnDelItemCheck(RecycleItemClickListener {
            CustomDialog.showTwoButtonDialog(this@AuditCostActivity, "确定删除该未审核成本?", "确定", "取消"
            ) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                presenter.delCost(cost?.loanCostId ?: 0)
            }
        })
        rv_over.adapter = adapter
        bottom.visibility = View.GONE
        val permits = CFSUtils.getPermitValue(Hawk.get("PermitValue", ""), cost?.loanType ?: 0)
        when (cost?.auditStatus) {
            0 -> {
                if (permits != null && permits.contains("CX")) {
                    bottom.visibility = View.VISIBLE
                }
            }
            1 -> {
                if ((permits != null && permits.contains("CZ")) || CFSUtils.isMortgageAudit(Hawk.get("UserID"))) {
                    bottom.visibility = View.VISIBLE
                }
            }
            6 -> {
                if ((cost?.money ?: 0.0) >= (CFSUtils.getAuditCost().toDouble())) {
                    if (CFSUtils.isAudit(Hawk.get("UserID"), "Cost_Audit_Id")
                            || Hawk.get<String>("UserType") == "99" || Hawk.get<String>("UserType") == "90") {
                        bottom.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun showSoftInput(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        this@AuditCostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    fun hideSoftInput(v: View) {
        val imm = this@AuditCostActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun getRemark(): String = et_over_remark.text.toString().trim()

    override fun onClick(p0: View?) {
        when (p0) {
            tv_not -> {
                rl_botom_over.visibility = View.VISIBLE
                showSoftInput(et_over_remark)
            }
            tv_ok -> {
                CustomDialog.showTwoButtonDialog(this@AuditCostActivity, "您确定同意该笔成本？", "确定", "取消") { dialogInterface, i ->
                    dialogInterface.dismiss()
                    when (cost?.auditStatus) {
                        0 -> presenter.auditPass(cost?.loanCostId ?: 0, 1, "")
                        1 -> presenter.auditPass(cost?.loanCostId ?: 0, 6, "")
                        6 -> presenter.auditPass(cost?.loanCostId ?: 0, 8, "")
                    }
                }
            }
            rl_botom_over -> {
                hideSoftInput(p0!!)
                rl_botom_over.visibility = View.GONE
            }
            tv_submit -> {
                hideSoftInput(p0!!)
                rl_botom_over.visibility = View.GONE
                if (getRemark().isBlank()) {
                    showMsg("拒绝理由不能为空!")
                } else {
                    when (cost?.auditStatus) {
                        0 -> presenter.auditPass(cost?.loanCostId ?: 0, 2, getRemark())
                        1 -> presenter.auditPass(cost?.loanCostId ?: 0, 4, getRemark())
                        6 -> presenter.auditPass(cost?.loanCostId ?: 0, 9, getRemark())
                    }
                }
            }
        }
    }

    override fun complete() {
        setResult(99)
        this.finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (rl_botom_over.visibility == View.VISIBLE) {
                hideSoftInput(toolbarTitle)
                rl_botom_over.visibility = View.GONE
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
