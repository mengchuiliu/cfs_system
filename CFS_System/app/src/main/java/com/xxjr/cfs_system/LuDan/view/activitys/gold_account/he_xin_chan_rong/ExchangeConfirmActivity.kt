package com.xxjr.cfs_system.LuDan.view.activitys.gold_account.he_xin_chan_rong

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.presenter.ExchangeConfirmP
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import kotlinx.android.synthetic.main.activity_exchang_confirm.*

class ExchangeConfirmActivity : BaseActivity<ExchangeConfirmP, ExchangeConfirmActivity>(), BaseViewInter, View.OnClickListener {
    private lateinit var amount: String
    private lateinit var name: String
    private lateinit var account: String
    override fun getPresenter(): ExchangeConfirmP = ExchangeConfirmP()

    override fun getLayoutId(): Int = R.layout.activity_exchang_confirm

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "确认兑换"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        amount = intent.getStringExtra("ExchangeAmount")
        name = intent.getStringExtra("Name") ?: ""
        account = intent.getStringExtra("Account") ?: ""
        tv_content.text = "向 $name $account 账户兑换"
        tv_amount.text = "${amount}元"
        tv_phone.text = "+86 ${account.replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2")}"
        tv_sms.setOnClickListener(this)
        tv_confirm.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            tv_sms -> {//获取验证码

            }
            tv_confirm -> {//确认
                val intent = Intent(this@ExchangeConfirmActivity, ExchangeCompleteActivity::class.java)
                intent.putExtra("ExchangeAmount", amount)
                startActivity(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }
}
