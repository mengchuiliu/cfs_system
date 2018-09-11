package com.xxjr.cfs_system.LuDan.view.activitys.spending_audit

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.RemindDetailAdapter
import com.xxjr.cfs_system.LuDan.presenter.SpendingDetailP
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import entity.SpendingInfo
import kotlinx.android.synthetic.main.activity_over_apply.*
import kotlinx.android.synthetic.main.bottom_bt.*

class SpendingDetailActivity : BaseActivity<SpendingDetailP, SpendingDetailActivity>(), BaseViewInter, View.OnClickListener {
    private var spendingInfo: SpendingInfo? = null
    private lateinit var adapter: RemindDetailAdapter
    private var isPass = false

    fun getSpendingInfo(): SpendingInfo = spendingInfo ?: SpendingInfo()

    override fun getPresenter(): SpendingDetailP = SpendingDetailP()

    override fun getLayoutId(): Int = R.layout.activity_over_apply

    override fun isShowBacking(): Boolean = true

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    fun getRemark(): String = et_over_remark.text.toString().trim()

    override fun initView(savedInstanceState: Bundle?) {
        spendingInfo = intent.getSerializableExtra("spendingDetail") as? SpendingInfo
        toolbarTitle.text = "支出审核详情"
        tv_not.text = "拒绝"
        tv_ok.text = "通过"
        tv_ok.setOnClickListener(this)
        tv_not.setOnClickListener(this)
        tv_submit.setOnClickListener(this)
        rl_botom_over.setOnClickListener(this)
        if (spendingInfo?.AC_Auditable ?: 0 > 0 && spendingInfo?.AuditState ?: 0 > 0) bottom.visibility = View.VISIBLE
        initRV()
        presenter.setDefaultValue()
    }

    private fun initRV() {
        rv_over.layoutManager = LinearLayoutManager(this@SpendingDetailActivity)
        adapter = RemindDetailAdapter(this@SpendingDetailActivity, presenter.getItemData(true, true))
        adapter.setOnItemShrink(RecycleItemClickListener { position ->
            val isShrink = (adapter.datas[13] as CommonItem<*>).isClick
            val isFileShrink = (adapter.datas[23] as CommonItem<*>).isClick
            when (position) {
                13 -> adapter.setNewData(presenter.getItemData(!isShrink, isFileShrink) as List<*>)
                23 -> adapter.setNewData(presenter.getItemData(isShrink, !isFileShrink) as List<*>)
            }
        })
        rv_over.adapter = adapter
    }

    override fun onClick(p0: View?) {
        when (p0) {
            tv_not -> {
                rl_botom_over.visibility = View.VISIBLE
                showSoftInput(et_over_remark)
            }
            tv_ok -> {
                CustomDialog.showTwoButtonDialog(this@SpendingDetailActivity, "确定信息无误，通过后将不能更改！", "确定", "取消") { dialogInterface, i ->
                    dialogInterface.dismiss()
                    isPass = true
                    presenter.auditSpending("True")
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
                } else {//拒绝
                    isPass = false
                    presenter.auditSpending("False")
                }
            }
        }
    }

    private fun showSoftInput(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        this@SpendingDetailActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    private fun hideSoftInput(v: View) {
        val imm = this@SpendingDetailActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
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

    fun complete() {
        if (isPass) showMsg("审核已通过") else showMsg("审核已拒绝")
        setResult(99)
        this.finish()
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }
}
