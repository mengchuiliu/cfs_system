package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import com.alibaba.fastjson.JSONArray
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.SignPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import kotlinx.android.synthetic.main.activity_sign.*
import android.text.style.ForegroundColorSpan


class SignActivity : BaseActivity<SignPresenter, SignActivity>(), BaseViewInter {
    private var isLogin = false
    private var telphone = ""
    private var timer: CountTime? = null

    override fun showMsg(msg: String?) = ToastShow.showShort(application, msg)

    override fun getPresenter(): SignPresenter = SignPresenter()

    override fun getLayoutId(): Int = R.layout.activity_sign

    fun getSignCode(): String = intent.getStringExtra("signCode") ?: ""

    fun getAisleType(): String = intent.getStringExtra("aisleType") ?: ""

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "短信验证"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        isLogin = intent.getBooleanExtra("isLogin", false)
        if (isLogin) {
            tv_sign.text = "确定"
            timer = CountTime(120 * 1000 + 100, 1000)
        } else {
            timer = CountTime(60 * 1000 + 100, 1000)
        }
        tv_sign.setOnClickListener {
            if (isLogin) presenter.loginVerification(ed_vc.text.toString().trim()) else presenter.sign(ed_vc.text.toString().trim())
        }
        initCountDown()
    }

    private fun initCountDown() {
        var issend = intent.getStringExtra("isSend")
        if (isLogin) {
            val returnStrings = intent.getStringExtra("returnStrings")
            val jsonArray = JSONArray.parseArray(returnStrings)
            if (jsonArray != null && jsonArray.size >= 2) {
                telphone = jsonArray.getString(0)
            }
            issend = "0"
        } else {
            telphone = if (isLogin) "" else intent.getStringExtra("telphone")
        }
        tv_phone.text = getReadText("向${hidePhone(telphone)}发送短信验证码")
        if (!issend.isNullOrBlank()) {
            when (issend) {
                "0" -> countOver()
                "1" -> countStart()
            }
        }
        tv_sms.setOnClickListener {
            if (isLogin) presenter.sendLoginSMS() else presenter.sendSignSMS()
        }
    }

    fun countStart() {
        tv_sms.isEnabled = false
        tv_sms.isClickable = false
        tv_sms.setTextColor(resources.getColor(R.color.font_c6))
        timer?.start()
    }

    private fun countOver() {
        tv_sms_num.text = ""
        tv_sms.setTextColor(resources.getColor(R.color.font_home))
        tv_sms.isEnabled = true
        tv_sms.isClickable = true
    }

    private fun getReadText(text: String): CharSequence {
        if (text.isNotBlank()) {
            val spannableString = SpannableString(text)
            val colorSpan = ForegroundColorSpan(resources.getColor(R.color.font_home))
            spannableString.setSpan(colorSpan, 1, text.length - 7, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
//        spannableString.setSpan(StyleSpan(Typeface.BOLD), 4, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)  //粗体
            spannableString.setSpan(StyleSpan(Typeface.ITALIC), 1, text.length - 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)  //斜体
            spannableString.setSpan(RelativeSizeSpan(1.2f), 1, text.length - 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //2.0f表示默认字体大小的两
//        spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.font_home)), start, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            return spannableString
        }
        return ""
    }

    private fun hidePhone(phone: String): String = phone.replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2")

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onDestroy() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
        super.onDestroy()
    }

    fun complete() {
        if (isLogin) {
            showMsg("登录成功，数据加载完成")
//            val intent1 = Intent(this@SignActivity, HomePageActivity::class.java)
            val intent1 = Intent(this@SignActivity, HomeMenuActivity::class.java)
            intent1.putExtra("permissions", intent.getStringExtra("permissions"))
            intent1.putExtra("RealName", intent.getStringExtra("RealName"))
            intent1.putExtra("UserBirthday", intent.getStringExtra("UserBirthday"))
            intent1.putExtra("CompanyName", intent.getStringExtra("CompanyName"))
            intent1.putExtra("TypeName", intent.getStringExtra("TypeName"))
            intent1.putExtra("versionName", intent.getStringExtra("versionName"))
            startActivity(intent1)
        } else {
            showMsg("签约成功")
        }
        setResult(66)
        finish()
    }

    private inner class CountTime(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            countOver()
        }

        override fun onTick(millisUntilFinished: Long) {
            if (millisUntilFinished / 1000 > 0)
                tv_sms_num.text = "（${millisUntilFinished / 1000}s）"
        }
    }
}
