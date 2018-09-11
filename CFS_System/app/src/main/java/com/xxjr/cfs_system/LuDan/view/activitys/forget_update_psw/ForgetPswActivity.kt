package com.xxjr.cfs_system.LuDan.view.activitys.forget_update_psw

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.ForgetPswPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import kotlinx.android.synthetic.main.activity_forget_psw.*

class ForgetPswActivity : BaseActivity<ForgetPswPresenter, ForgetPswActivity>(), BaseViewInter, View.OnClickListener {
    private var sms1 = ""

    private lateinit var smsCount: CountDownTimer

    override fun getPresenter() = ForgetPswPresenter()

    override fun getLayoutId(): Int = R.layout.activity_forget_psw

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "忘记密码"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        et_four_phone.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(4))

        initCount()
        tv_sms.setOnClickListener(this)
        tv_next.setOnClickListener(this)
    }

    private fun initCount() {
        smsCount = object : CountDownTimer(120 * 1000 + 100, 1000) {
            override fun onFinish() {
                tv_sms.text = "获取验证码"
                tv_sms.isEnabled = true
                tv_sms.setBackgroundResource(R.drawable.message_bg)
                tv_sms.setTextColor(resources.getColor(R.color.font_home))
            }

            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished / 1000 > 0)
                    tv_sms.text = " 等待${millisUntilFinished / 1000}s "
            }
        }
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun onClick(p0: View?) {
        val account = et_account.text.toString().trim()
        val phone = et_four_phone.text.toString().trim()
        when (p0) {
            tv_sms -> {
                when {
                    account.isBlank() -> showMsg("请正确的填写账号")
                    phone.isBlank() || phone.length != 4 -> showMsg("请正确的填写手机后四位号码")
                    else -> presenter.getSMS(account, phone)
                }
            }
            tv_next -> {
                when {
                    account.isBlank() -> showMsg("请正确的填写账号")
                    phone.isBlank() || phone.length != 4 -> showMsg("请正确的填写手机后四位号码")
                    ed_vc.inputContent.isBlank() -> showMsg("验证码错误")
                    else -> presenter.checkSMS(account, ed_vc.inputContent)
                }
            }
        }
    }

    fun setSMS() {
        smsCount.start()
        tv_sms.isEnabled = false
        tv_sms.setBackgroundResource(R.drawable.forget_psw_bg)
        tv_sms.setTextColor(resources.getColor(R.color.font_c9))
    }

    fun goResetPsw() {
        val intent = Intent(this@ForgetPswActivity, ResetPswActivity::class.java)
        intent.putExtra("sms", ed_vc.inputContent)
        intent.putExtra("account", et_account.text.toString().trim())
        startActivityForResult(intent, 99)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 99) {
            finish()
        }
    }

    override fun onDestroy() {
        smsCount.cancel()
        super.onDestroy()
    }
}
