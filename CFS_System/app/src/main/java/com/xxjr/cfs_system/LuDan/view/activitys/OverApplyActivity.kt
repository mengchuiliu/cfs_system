package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.EditText
import com.orhanobut.hawk.Hawk

import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.OverApplyAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.presenter.OverApplyPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.OverApplyVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import entity.LoanInfo
import kotlinx.android.synthetic.main.activity_over_apply.*
import kotlinx.android.synthetic.main.bottom_bt.*
import kotlinx.android.synthetic.main.toolbar.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.Constants
import me.kareluo.ui.OptionMenu
import me.kareluo.ui.OptionMenuView
import me.kareluo.ui.PopupMenuView
import java.util.ArrayList

class OverApplyActivity : BaseActivity<OverApplyPresenter, OverApplyVInter>(), OverApplyVInter, View.OnClickListener, OptionMenuView.OnOptionMenuClickListener {
    private var loanInfo: LoanInfo? = null
    var adapter: OverApplyAdapter? = null
    private var mPopupMenuView: PopupMenuView? = null
    private var note: String = ""

    override fun getLoanInfo(): LoanInfo = loanInfo ?: LoanInfo()

    override fun getPermits() = CFSUtils.getPermitValue(Hawk.get("PermitValue", ""), loanInfo?.loanType!!)
            ?: arrayListOf()

    override fun getPresenter(): OverApplyPresenter = OverApplyPresenter()

    override fun getLayoutId(): Int = R.layout.activity_over_apply

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "结案信息"
        loanInfo = intent.getSerializableExtra("loanInfo") as? LoanInfo
        if (getPermits().contains("CQ") || getPermits().contains("CU")) {
            //CQ入账，CU出账
            iv_right.visibility = View.VISIBLE
            iv_right.setImageResource(R.mipmap.icon_add)
        } else {
            iv_right.visibility = View.GONE
        }
        initClick()
        initPop()
        presenter.setDefaultValue()
    }

    private fun initClick() {
        iv_right.setOnClickListener(this)
        tv_not.setOnClickListener(this)
        tv_ok.setOnClickListener(this)
        rl_botom_over.setOnClickListener(this)
        tv_submit.setOnClickListener(this)
    }

    private fun initPop() {
        mPopupMenuView = PopupMenuView(this@OverApplyActivity)
        mPopupMenuView?.popLayout?.radiusSize = 10
        mPopupMenuView?.orientation = LinearLayout.VERTICAL
        mPopupMenuView?.setOnMenuClickListener(this@OverApplyActivity)
        val menus = ArrayList<OptionMenu>()
        if (getPermits().contains("CQ")) {
            val menu1 = OptionMenu()
            menu1.id = 1
            menu1.title = "     入    账  "
            menu1.drawableLeft = resources.getDrawable(R.mipmap.icon_ruzhang)
            menus.add(menu1)
        }
        if (getPermits().contains("CU")) {
            val menu2 = OptionMenu()
            menu2.id = 2
            menu2.title = "     出    账  "
            menu2.drawableLeft = resources.getDrawable(R.mipmap.icon_chuzhang)
            menus.add(menu2)
        }
        mPopupMenuView?.menuItems = menus
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun isShowBacking(): Boolean = true

    override fun initRecycler(list: MutableList<CommonItem<Any>>) {
        rv_over.layoutManager = LinearLayoutManager(this)
        adapter = OverApplyAdapter(this, list)
        adapter?.setOnItemClick(RecycleItemClickListener {
            presenter.showTimeChoose()
        })
        adapter?.setOnCostItemClick(RecycleItemClickListener { position ->
            val item: CommonItem<*> = adapter?.datas?.get(position) as CommonItem<*>
            item.isClick = !item.isClick
            val item1: CommonItem<*> = adapter?.datas?.get(position + 1) as CommonItem<*>
            item1.isClick = !item1.isClick
            adapter?.notifyDataSetChanged()
        })
        adapter?.setDelItemClick(RecycleItemClickListener { position ->
            presenter.delBooksData(position)
        })
        adapter?.setTextChangeListener(TextChangeListener { position, text ->
            note = text
        })
        rv_over.adapter = adapter
    }

    override fun showBackStep(isShow: Boolean) {
        tv_not.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun isCase(isCase: Boolean) {
        tv_ok.isEnabled = isCase
    }

    override fun showButton(isShow: Boolean, notText: String, okText: String) {
        bottom.visibility = if (isShow) View.VISIBLE else View.GONE
        tv_not.text = notText
        tv_ok.text = okText
    }

    override fun showSoftInput(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        this@OverApplyActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    override fun hideSoftInput(v: View) {
        val imm = this@OverApplyActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun refreshItem(position: Int, text: String) {
        val item: CommonItem<*> = adapter?.datas?.get(position) as CommonItem<*>
        item.content = text
        adapter?.notifyItemChanged(position, item)
    }

    override fun refreshAdapter(list: MutableList<CommonItem<Any>>) {
        adapter?.setNewData(list as List<Any>?)
    }

    override fun getEdRemark(): String = et_over_remark.text.toString().trim()

    override fun getOverNote(): String = note

    override fun onClick(p0: View?) {
        when (p0) {
            iv_right -> {
                mPopupMenuView?.show(iv_right)
            }
            tv_not -> {
                when (loanInfo?.scheduleId) {
                    109, 5 -> {
                        val intent4 = Intent(this@OverApplyActivity, RemarkActivity::class.java)
                        intent4.putExtra("loanInfo", loanInfo)
                        intent4.putExtra("remarkType", 3)
                        startActivityForResult(intent4, 77)
                    }
                    1090, 50 -> {
                        rl_botom_over.visibility = View.VISIBLE
                        showSoftInput(et_over_remark)
                    }
                }
            }
            tv_ok -> {
                when (loanInfo?.scheduleId) {
                    109, 5, -3, -4, -5 -> {
                        if (loanInfo?.overTime.isNullOrBlank()) {
                            showMsg("请先选择结单日期!")
                            return
                        }
                        CustomDialog.showTwoButtonDialog(this@OverApplyActivity, "您确定要发起结案申请?", "确定", "取消") { dialogInterface, i ->
                            dialogInterface.dismiss()
                            presenter.scheduleUpdateOrBack(1)
                        }
                    }
                    1090, 50 -> {
                        CustomDialog.showTwoButtonDialog(this@OverApplyActivity, "您确定结案信息正确,\n" +
                                "同意之后信息将不能在更改?", "确定", "取消") { dialogInterface, i ->
                            dialogInterface.dismiss()
                            presenter.scheduleUpdateOrBack(1)
                        }
                    }
                }
            }
            rl_botom_over -> {
                hideSoftInput(p0!!)
                rl_botom_over.visibility = View.GONE
            }
            tv_submit -> {//提交
                hideSoftInput(p0!!)
                rl_botom_over.visibility = View.GONE
                when (loanInfo?.scheduleId) {
                    109, 5, -3, -4, -5 -> {
                        presenter.scheduleUpdateOrBack(2)
                    }
                    1090, 50 -> {
                        if (getEdRemark().isBlank()) {
                            showMsg("拒绝理由不能为空!")
                        } else {
                            presenter.scheduleUpdateOrBack(2)
                        }
                    }
                }
            }
        }
    }

    override fun onOptionMenuClick(position: Int, menu: OptionMenu?): Boolean {
        when (menu?.id) {
            1 -> hidePopAndStart(1)
            2 -> hidePopAndStart(2)
        }
        return true
    }

    private fun hidePopAndStart(bookType: Int) {
        val intent = Intent(this@OverApplyActivity, BooksActivity::class.java)
        intent.putExtra("loanInfo", loanInfo)
        intent.putExtra("type", bookType)
        startActivityForResult(intent, 123)
    }

    override fun complete() {
        RxBus.getInstance().post(Constants.POST_REFRESH_MY_TASK, true)
        when (loanInfo?.scheduleId) {
            109, 5, -3, -4, -5 -> {
                setResult(Constants.REQUEST_CODE_BACK)
            }
            1090, 50 -> {
                setResult(99)
            }
        }
        this@OverApplyActivity.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Constants.REQUEST_CODE_BOOKS_RU_CHU -> {
                presenter.getOverData()
            }
            Constants.REQUEST_CODE_BOOKS_TOTLE -> {
                setResult(99)
                this@OverApplyActivity.finish()
            }
            Constants.REQUEST_LENDING_BACK -> {
                setResult(111)
                this@OverApplyActivity.finish()
            }
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

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onBackPressed() {
        setResult(999999)
        hideSoftInput(toolbarTitle)
        super.onBackPressed()
    }
}
