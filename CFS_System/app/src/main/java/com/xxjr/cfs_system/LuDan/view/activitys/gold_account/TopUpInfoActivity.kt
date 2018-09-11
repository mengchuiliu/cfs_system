package com.xxjr.cfs_system.LuDan.view.activitys.gold_account

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.TopUpPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_top_up_info.*
import me.kareluo.ui.OptionMenu
import me.kareluo.ui.OptionMenuView
import me.kareluo.ui.PopupMenuView
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import android.text.ClipboardManager
import me.kareluo.ui.PopupView


class TopUpInfoActivity : BaseActivity<TopUpPresenter, TopUpInfoActivity>(), BaseViewInter, OptionMenuView.OnOptionMenuClickListener {
    private var mPopupMenuView: PopupMenuView? = null
    private var copyContent = ""

    fun getReturnString(): String = intent.getStringExtra("returnString")

    override fun getPresenter(): TopUpPresenter = TopUpPresenter()

    override fun getLayoutId(): Int = R.layout.activity_top_up_info

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "充值码信息"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        tv_complete.setOnClickListener { finish() }
        tv_top_code.setOnLongClickListener { v ->
            copyContent = tv_top_code.text.toString()
            mPopupMenuView?.show(v)
            true
        }
        presenter.setDefaultValue()
        initPop()
    }

    private fun initPop() {
        mPopupMenuView = PopupMenuView(this@TopUpInfoActivity)
        mPopupMenuView?.orientation = LinearLayout.HORIZONTAL
        mPopupMenuView?.setSites(PopupView.SITE_TOP, PopupView.SITE_RIGHT, PopupView.SITE_BOTTOM, PopupView.SITE_LEFT)
        mPopupMenuView?.setOnMenuClickListener(this@TopUpInfoActivity)
        mPopupMenuView?.menuItems = mutableListOf(OptionMenu("  复 制  "))
    }

    fun initRv(commonItems: MutableList<CommonItem<Any>>, rechargeCode: String) {
        tv_top_code.text = rechargeCode
        rv_top_info.layoutManager = LinearLayoutManager(this@TopUpInfoActivity)
        rv_top_info.adapter = object : CommonAdapter<CommonItem<Any>>(this@TopUpInfoActivity, commonItems, R.layout.item_common_show) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.getView<TextView>(R.id.tv_content)?.setSingleLine(false)
                holder.setTextColorRes(R.id.tv_content_name, R.color.font_c9)
                holder.setTextColorRes(R.id.tv_content, R.color.font_c5)
                holder.setTextSize(R.id.tv_content_name, 13f)
                holder.setTextSize(R.id.tv_content, 13f)
                holder.setText(R.id.tv_content_name, item.name)
                holder.setText(R.id.tv_content, item.content)
                holder.setOnLongClickListener(R.id.tv_content, { v ->
                    copyContent = item.content
                    mPopupMenuView?.show(v)
                    true
                })
            }
        }
    }

    override fun onOptionMenuClick(position: Int, menu: OptionMenu?): Boolean {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 将文本内容放到系统剪贴板里。
        cm.text = copyContent
        showMsg("已复制")
        return true
    }
}
