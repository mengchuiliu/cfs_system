package com.xxjr.cfs_system.LuDan.view.activitys.gold_account

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.AgreementAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.presenter.RegisteredPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.ScanCamera
import com.xxjr.cfs_system.LuDan.view.viewinter.RegisteredVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import entity.GoldRegisteredInfo
import kotlinx.android.synthetic.main.activity_agreement.*
import kotlinx.android.synthetic.main.toolbar.*


class RegisteredActivity : BaseActivity<RegisteredPresenter, RegisteredVInter>(), RegisteredVInter, View.OnClickListener {
    private var adapter: AgreementAdapter? = null
    //    private var tvTimer: TextView? = null
    private var dialog: Dialog? = null

    override fun getRegisteredType(): Int = intent.getIntExtra("Type", 1)//1->个人  2->企业

    override fun getAccountType(): Int = intent.getIntExtra("AccountType", 1)//1.个人私户  2.门店私户  3.门店公户 4.顾客账户

    override fun getPresenter(): RegisteredPresenter = RegisteredPresenter()

    override fun getLayoutId(): Int = R.layout.activity_agreement

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = run {
            when (getRegisteredType()) {
                1 -> {
                    when (getAccountType()) {
                        1 -> "个人注册"
                        4 -> "客户注册"
                        else -> ""
                    }
                }
                else -> "企业注册"
            }
        }
        ivBack.visibility = View.VISIBLE
        next.text = "注册"
        ivBack.setOnClickListener(this)
        next.setOnClickListener(this)
        presenter.setDefaultValue()
    }

    override fun initRV(commonItems: MutableList<CommonItem<Any>>) {
        rv_agree.layoutManager = LinearLayoutManager(this@RegisteredActivity)
        adapter = AgreementAdapter(this@RegisteredActivity, commonItems)
        adapter?.setOnItemClick(RecycleItemClickListener { position ->
            presenter.clickChoose(position, rv_agree)
        })

        adapter?.setTextChangeListener(TextChangeListener { position, text ->
            presenter.editContent(position, text)
        })
        rv_agree.adapter = adapter
    }

    override fun refreshItem(position: Int, text: String) {
        val item: CommonItem<*> = adapter?.datas?.get(position) as CommonItem<*>
        item.content = text
        adapter?.notifyItemChanged(position, item)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            next -> presenter.registered()//complete("", "", "", "")
            iv_back -> finish()
        }
    }

    override fun chooseCity() {
        startActivity(CitySearchActivity::class.java)
    }

    override fun scanCard() {
        val intent = Intent(this@RegisteredActivity, ScanCamera::class.java)
        startActivityForResult(intent, 55)
    }

    override fun complete(register: GoldRegisteredInfo) {
        dialog = Dialog(this@RegisteredActivity, R.style.loading_dialog)
        dialog?.show()
        val window = dialog?.window
        val lp = window!!.attributes
        val d = resources.displayMetrics // 获取屏幕宽、高用
        lp.width = (d.widthPixels * 0.75).toInt()
        window.attributes = lp
        window.setGravity(Gravity.CENTER)
        window.setContentView(R.layout.dialog_to_gold)
        val gold = window.findViewById<TextView>(R.id.tv_to_gold)
        gold.isEnabled = false
        val password = window.findViewById<CheckBox>(R.id.tv_password)
        password.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            gold.isEnabled = b
        }
        gold.setOnClickListener {
            toGoldAccount(register)
        }
        dialog?.setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
                return@OnKeyListener true
            }
            false
        })
//        tvTimer = window.findViewById(R.id.tv_timer)
        dialog?.setCanceledOnTouchOutside(false)
//        timer.start()
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onDestroy() {
        if (dialog != null && dialog?.isShowing!!) {
            dialog?.dismiss()
            dialog = null
        }
//        timer.cancel()
        presenter.rxDeAttach()
        super.onDestroy()
    }

    private fun toGoldAccount(register: GoldRegisteredInfo) {
        dialog?.dismiss()
        when (getAccountType()) {
            1 -> {
                setResult(11)
                val intent = Intent(this@RegisteredActivity, GoldAccountActivity::class.java)
                intent.putExtra("GoldUserInfo", register)
                startActivity(intent)
            }
            4 -> setResult(88)
        }
        this@RegisteredActivity.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 5555) {
            //添加完成刷新数据
            val resultCard = data?.getStringExtra("resultCard")
            if (resultCard.isNullOrBlank()) {
                //showMsg("扫描失败!")
            } else {
                val imm = this@RegisteredActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(rv_agree.windowToken, 0)
                presenter.freshHead()
                presenter.setRefresh(false)
                presenter.setBankNo(resultCard ?: "")
                if (rv_agree.scrollState == RecyclerView.SCROLL_STATE_IDLE || !rv_agree.isComputingLayout) {
                    if (getRegisteredType() == 1) refreshItem(8, resultCard
                            ?: "") else refreshItem(9, resultCard ?: "")
                }
            }
        }
    }


//    private var timer = object : CountDownTimer(5000, 1000) {
//
//        override fun onTick(millisUntilFinished: Long) {
//            tvTimer?.text = getReadText("${millisUntilFinished / 1000}秒后进入金账户")
//        }
//
//        override fun onFinish() {
//            toGoldAccount()
//        }
//    }

    private fun getReadText(text: String): CharSequence {
        if (text.isNotBlank()) {
            val spannableString = SpannableString(text)
            val colorSpan = ForegroundColorSpan(resources.getColor(R.color.viewfinder_laser))
            spannableString.setSpan(colorSpan, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            return spannableString
        }
        return ""
    }
}
