package com.xxjr.cfs_system.LuDan.view.activitys.forget_update_psw

import android.os.Bundle
import android.text.InputType
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.ResetPswPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import kotlinx.android.synthetic.main.activity_forget_psw.*

class ResetPswActivity : BaseActivity<ResetPswPresenter, BaseViewInter>(), BaseViewInter {
    override fun getPresenter(): ResetPswPresenter = ResetPswPresenter()

    override fun getLayoutId(): Int = R.layout.activity_forget_psw

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "忘记密码"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }

        tv_sms.visibility = View.GONE
        tv_prompt.visibility = View.GONE
        ed_vc.visibility = View.GONE
        complete()
    }

    private fun complete() {
        et_account.hint = "新密码必须包含数字和字母"
        et_account.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        et_four_phone.hint = "请再次输入新密码"
        et_four_phone.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        tv_next.text = "完成"
        tv_next.setOnClickListener {
            if (checkPsw()) {
                val psw = et_account.text.toString().trim()
                presenter.resetPsw(intent.getStringExtra("account") ?: "",
                        Utils.getMD5Str(psw), intent.getStringExtra("sms") ?: "")
            }
        }
    }

    fun over() {
        setResult(99)
        finish()
    }

    private fun checkPsw(): Boolean {
        val psw = et_account.text.toString().trim()
        val pswAgain = et_four_phone.text.toString().trim()
        when {
            psw.isBlank() -> {
                showMsg("新密码不能为空!")
                return false
            }
            psw.length < 8 -> {
                showMsg("新密码不能少于8位!")
                return false
            }
            !isLetter(psw) -> {
                showMsg("密码必须包含数字字母!")
                return false
            }
            psw != pswAgain -> {
                showMsg("两次输入的新密码不一致!")
                return false
            }
        }
        return true
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    /**
     * 规则2：至少包含大小写字母及数字中的两种
     * 是否包含
     *
     * @param str
     * @return
     */
    private fun isLetter(str: String): Boolean {
        var isLetter = false//定义一个boolean值，用来表示是否包含字母
        for (i in 0 until str.length) {
            if (Character.isLetter(str[i])) {  //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true
            }
        }
        val regex = "^[a-zA-Z0-9]+$"
        return isLetter && str.matches(regex.toRegex())
    }
}
