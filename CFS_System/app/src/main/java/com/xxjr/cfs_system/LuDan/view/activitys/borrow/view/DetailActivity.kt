package com.xxjr.cfs_system.LuDan.view.activitys.borrow.view

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
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.borrow.presenter.DetailPresenter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.BorrowDetail
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_over_apply.*
import kotlinx.android.synthetic.main.bottom_bt.*

class DetailActivity : BaseActivity<DetailPresenter, DetailActivity>(), BaseViewInter, View.OnClickListener {
    private lateinit var adapter: RemindDetailAdapter
    lateinit var borrowDetail: BorrowDetail
    private var isPass = false

    override fun getPresenter(): DetailPresenter = DetailPresenter()

    override fun getLayoutId(): Int = R.layout.activity_over_apply

    override fun isShowBacking(): Boolean = true

    fun getRemark(): String = et_over_remark.text.toString().trim()

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        borrowDetail = (intent.getSerializableExtra("borrowInfo") as? BorrowDetail) ?: BorrowDetail()
        toolbarTitle.text = "拆借详情"
        tv_not.text = "拒绝"
        tv_ok.text = "通过"
        tv_ok.setOnClickListener(this)
        tv_not.setOnClickListener(this)
        tv_submit.setOnClickListener(this)
        rl_botom_over.setOnClickListener(this)
        if (borrowDetail.Auditable) bottom.visibility = View.VISIBLE else bottom.visibility = View.GONE
        initRV()
        presenter.setDefaultValue()
    }

    private fun initRV() {
        rv_over.layoutManager = LinearLayoutManager(this@DetailActivity)
        adapter = RemindDetailAdapter(this@DetailActivity, presenter.getItemData(false, true, true))
        adapter.setOnItemShrink(RecycleItemClickListener { position ->
            val isPersonShrink = (adapter.datas[0] as CommonItem<*>).isClick
            val isBorrowShrink = (adapter.datas[26] as CommonItem<*>).isClick
            val isFileShrink = (adapter.datas[45] as CommonItem<*>).isClick
            when (position) {
                0 -> adapter.setNewData(presenter.getItemData(!isPersonShrink, isBorrowShrink, isFileShrink) as List<*>)
                26 -> adapter.setNewData(presenter.getItemData(isPersonShrink, !isBorrowShrink, isFileShrink) as List<*>)
                45 -> adapter.setNewData(presenter.getItemData(isPersonShrink, isBorrowShrink, !isFileShrink) as List<*>)
            }
        })
        rv_over.adapter = adapter
    }

    fun refreshData(mutableList: MutableList<CommonItem<Any>>) {
        adapter.setNewData(mutableList as List<Any>?)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            tv_not -> {
                rl_botom_over.visibility = View.VISIBLE
                showSoftInput(et_over_remark)
            }
            tv_ok -> {
                CustomDialog.showTwoButtonDialog(this@DetailActivity, "您确定同意该笔拆借？", "确定", "取消") { dialogInterface, i ->
                    dialogInterface.dismiss()
                    isPass = true
                    presenter.borrowAudit(isPass, "")
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
                    presenter.borrowAudit(isPass, getRemark())
                }
            }
        }
    }

    fun complete() {
        if (isPass) showMsg("审核已通过") else showMsg("审核已拒绝")
        setResult(99)
        this@DetailActivity.finish()
    }

    private fun showSoftInput(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        this@DetailActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    private fun hideSoftInput(v: View) {
        val imm = this@DetailActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    override fun onStart() {
        super.onStart()
        setWater(water)
    }
}
