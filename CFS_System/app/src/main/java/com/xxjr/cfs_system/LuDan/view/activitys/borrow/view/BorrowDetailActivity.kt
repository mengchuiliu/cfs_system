package com.xxjr.cfs_system.LuDan.view.activitys.borrow.view

import android.content.Context
import android.content.Intent
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
import com.xxjr.cfs_system.LuDan.adapters.BorrowDetailAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.view.activitys.borrow.presenter.BorrowDetailPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.BorrowDetailVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.ToastShow
import entity.BorrowInfo
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_over_apply.*
import kotlinx.android.synthetic.main.bottom_bt.*

class BorrowDetailActivity : BaseActivity<BorrowDetailPresenter, BorrowDetailVInter>(), BorrowDetailVInter, View.OnClickListener {
    var adapter: BorrowDetailAdapter? = null

    override fun getBorrowId(): Int = intent.getIntExtra("borrowId", -1)

    override fun getRemark(): String = et_over_remark.text.toString().trim()

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun getPresenter(): BorrowDetailPresenter = BorrowDetailPresenter()

    override fun getLayoutId(): Int = R.layout.activity_over_apply

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "拆借信息"
        tv_not.text = "不同意"
        tv_ok.text = "同意"
        tv_ok.setOnClickListener(this)
        tv_not.setOnClickListener(this)
        tv_submit.setOnClickListener(this)
        rl_botom_over.setOnClickListener(this)
        presenter.setDefaultValue()
    }

    override fun isShowBacking(): Boolean = true

    override fun initRecycler(mutableList: MutableList<CommonItem<Any>>, borrowInfo: BorrowInfo?) {
        bottom.visibility = View.GONE
        val userId = Hawk.get<String>("UserID")
        if (CFSUtils.isAudit(userId, "LendAuditId")
                || Hawk.get<String>("UserType") == "99" || Hawk.get<String>("UserType") == "90") {
            if ((borrowInfo?.approveIds ?: "").isBlank()) {
                bottom.visibility = View.VISIBLE
            } else {
                val ids = borrowInfo?.approveIds?.split(",".toRegex())
                if (ids?.size ?: 0 <= 1) {
                    if (userId != ids!![0]) {
                        bottom.visibility = View.VISIBLE
                    }
                }
            }
        }
        rv_over.layoutManager = LinearLayoutManager(this@BorrowDetailActivity)
        adapter = BorrowDetailAdapter(this@BorrowDetailActivity, mutableList)
        adapter?.setOnItemClick(RecycleItemClickListener { position ->
            when (position) {
                0 -> {
                    val intent1 = Intent(this@BorrowDetailActivity, BorrowActivity::class.java)
                    intent1.putExtra("borrow", borrowInfo)
                    intent1.putExtra("isUpdate", true)
                    intent1.putExtra("contractId", borrowInfo?.contractID)
                    startActivityForResult(intent1, 9)
                }
                14 -> refreshItem(position)
            }
        })
        rv_over.adapter = adapter
    }

    override fun refreshItem(pos: Int) {
        val item = adapter?.datas?.get(pos) as? CommonItem<*>
        val item1 = adapter?.datas?.get(pos + 1) as? CommonItem<*>
        if (item?.isClick == true) {
            item.isClick = false
            item1?.isClick = false
        } else {
            item?.isClick = true
            item1?.isClick = true
        }
        adapter?.notifyItemChanged(pos, item)
        adapter?.notifyItemChanged(pos + 1, item1)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            tv_not -> {
                rl_botom_over.visibility = View.VISIBLE
                showSoftInput(et_over_remark)
            }
            tv_ok -> {
                CustomDialog.showTwoButtonDialog(this@BorrowDetailActivity, "您确定同意该笔拆借？", "确定", "取消") { dialogInterface, i ->
                    dialogInterface.dismiss()
                    presenter.auditPass()
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
                    presenter.auditRefuse()
                }
            }
        }
    }

    fun showSoftInput(editText: EditText) {
        editText.setFocusable(true)
        editText.setFocusableInTouchMode(true)
        editText.requestFocus()
        this@BorrowDetailActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    fun hideSoftInput(v: View) {
        val imm = this@BorrowDetailActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun complete() {
        setResult(66)
        this@BorrowDetailActivity.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 909) {
            complete()
        }
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
