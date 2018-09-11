package com.xxjr.cfs_system.LuDan.view.activitys.gold_account.he_xin_chan_rong

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.View
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.presenter.BindPresenter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.main.WelWebActivity
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.ClientInfo
import kotlinx.android.synthetic.main.activity_bind_account.*

class BindAccountActivity : BaseActivity<BindPresenter, BindAccountActivity>(), BaseViewInter, View.OnClickListener {
    var clientInfo = ClientInfo()
    override fun getPresenter(): BindPresenter = BindPresenter()

    override fun getLayoutId(): Int = R.layout.activity_bind_account

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "账户绑定"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        tv_register.setOnClickListener(this)
        tv_bind.setOnClickListener(this)
        tv_bind.isEnabled = false
        initET()
    }

    private fun initET() {
        CustomDialog.showOneButtonDialog(this@BindAccountActivity, R.string.he_xin_account_bind, R.string.bind_account)
        et_card.keyListener = DigitsKeyListener.getInstance("0123456789zZ")
        et_name.addTextChangedListener(MyTextWatcher(0))
        et_card.addTextChangedListener(MyTextWatcher(1))
        et_account.addTextChangedListener(MyTextWatcher(2))
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            tv_register -> {//去注册
                val intent = Intent(this@BindAccountActivity, WelWebActivity::class.java)
                intent.putExtra("Type", 1)
                startActivity(intent)
            }
            tv_bind -> {//绑定
                val s = Utils.IDCardValidate(clientInfo.idCode)
                if (s.isNotBlank()) showMsg(s) else presenter.bindAccount()
            }
        }
    }

    fun bindSuccess() {
        showMsg("绑定成功")
        val intent = Intent(this@BindAccountActivity, ExchangeActivity::class.java)
        intent.putExtra("Name", clientInfo.name)
        intent.putExtra("Account", clientInfo.mobile)
        intent.putExtra("UsableAmount", intent.getStringExtra("UsableAmount"))
        startActivity(intent)
        finish()
    }

    inner class MyTextWatcher(val type: Int) : TextWatcher {

        override fun afterTextChanged(editable: Editable?) {
        }

        override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            when (type) {
                0 -> clientInfo.name = (text ?: "").toString()
                1 -> clientInfo.idCode = (text ?: "").toString()
                2 -> clientInfo.mobile = (text ?: "").toString()
            }
            tv_bind.isEnabled = presenter.canBind()
        }
    }
}
