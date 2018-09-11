package com.xxjr.cfs_system.LuDan.view.activitys.gold_account.he_xin_chan_rong

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.MoneyEditText
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.Utils
import kotlinx.android.synthetic.main.activity_exchange.*

class ExchangeActivity : BaseActivity<BasePresenter<*, *>, BaseViewInter>(), View.OnClickListener {
    private lateinit var usableAmount: String
    private lateinit var name: String
    private lateinit var account: String
    override fun getPresenter(): BasePresenter<*, *>? = null

    override fun getLayoutId(): Int = R.layout.activity_exchange

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "兑换"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        usableAmount = intent.getStringExtra("UsableAmount") ?: "0.00"
        name = intent.getStringExtra("Name") ?: ""
        account = intent.getStringExtra("Account") ?: ""
        tv_next.isEnabled = false
        tv_all.setOnClickListener(this)
        tv_next.setOnClickListener(this)
        showView()
    }

    private fun showView() {
        tv_name.text = name
        tv_phone.text = account
        tv_usable_amount.text = "${usableAmount}元"
        et_amount.setMax(usableAmount.toDouble())
        et_amount.setOnTextChangeListener(object : MoneyEditText.OnTextChangeListener {
            override fun onTextChange(text: String) {
                tv_next.isEnabled = !text.isBlank()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    private var clickTime = 0L

    override fun onClick(p0: View?) {
        when (p0) {
            tv_all -> et_amount.setText(usableAmount)
            tv_next -> {
                if (Utils.isClickAgain(clickTime, 1000)) {
                    clickTime = System.currentTimeMillis()
                    val intent = Intent(this@ExchangeActivity, ExchangeConfirmActivity::class.java)
                    intent.putExtra("Name", name)
                    intent.putExtra("Account", account)
                    intent.putExtra("ExchangeAmount", et_amount.text.toString())
                    startActivity(intent)
                }
            }
        }
    }
}
