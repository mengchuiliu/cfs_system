package com.xxjr.cfs_system.LuDan.view.activitys.transfer_receivable

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.ClipboardManager
import android.view.View
import android.widget.LinearLayout
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.RechargeCodeDetailAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemLongClickListener
import com.xxjr.cfs_system.LuDan.presenter.RechargeInfoPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_common_list.*
import me.kareluo.ui.OptionMenu
import me.kareluo.ui.OptionMenuView
import me.kareluo.ui.PopupMenuView
import me.kareluo.ui.PopupView

class RechargeInfoActivity : BaseActivity<RechargeInfoPresenter, RechargeInfoActivity>(), BaseViewInter, OptionMenuView.OnOptionMenuClickListener {
    private var mPopupMenuView: PopupMenuView? = null
    private var copyContent = ""

    fun getRechargeInfo(): String = intent.getStringExtra("rechargeInfo")

    override fun getPresenter(): RechargeInfoPresenter = RechargeInfoPresenter()

    override fun getLayoutId(): Int = R.layout.activity_common_list

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "充值码信息"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
//        initPop()
        presenter.setDefaultValue()
    }

    private fun initPop() {
        mPopupMenuView = PopupMenuView(this@RechargeInfoActivity)
        mPopupMenuView?.orientation = LinearLayout.HORIZONTAL
        mPopupMenuView?.setSites(PopupView.SITE_TOP, PopupView.SITE_RIGHT, PopupView.SITE_BOTTOM, PopupView.SITE_LEFT)
        mPopupMenuView?.setOnMenuClickListener(this@RechargeInfoActivity)
        mPopupMenuView?.menuItems = mutableListOf(OptionMenu("  复 制  "))
    }

    fun initRVCode(commonItems: MutableList<CommonItem<Any>>) {
        rv_remind.layoutManager = LinearLayoutManager(this@RechargeInfoActivity)
        val adapter = RechargeCodeDetailAdapter(this@RechargeInfoActivity, commonItems)
        adapter.setOnItemClick(RecycleItemLongClickListener { parent, position, text ->
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm.text = text
            showMsg("复制成功")
//            copyContent = text
//            mPopupMenuView?.show(parent)
        })
        rv_remind.adapter = adapter
    }

    override fun onOptionMenuClick(position: Int, menu: OptionMenu?): Boolean {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 将文本内容放到系统剪贴板里。
        cm.text = copyContent
        showMsg("复制成功")
        return true
    }
}
