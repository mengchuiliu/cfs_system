package com.xxjr.cfs_system.LuDan.view.activitys.gold_account

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.WithdrawalPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import kotlinx.android.synthetic.main.activity_withdrawa.*
import java.text.DecimalFormat

class WithdrawalActivity : BaseActivity<WithdrawalPresenter, WithdrawalActivity>(), BaseViewInter {
    private var usableAmt = 0.0
    private var topUpType = 0 //充值类型

    fun getType(): Int = intent.getIntExtra("Type", 0) //0->提现 1->充值 2->批量转账

    fun getUserNo(): String = intent.getStringExtra("UserNo")

    fun getTransferAmount(): Double = intent.getDoubleExtra("totalAmount", 0.0)

    override fun getPresenter(): WithdrawalPresenter = WithdrawalPresenter()

    override fun getLayoutId(): Int = R.layout.activity_withdrawa

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        usableAmt = intent.getDoubleExtra("UsableAmt", 0.0)
        presenter.setDefaultValue()
        if (getType() == 0) {
            toolbarTitle.text = "提现"
            tv_confirm.text = "确认提现"
            tv_payment.text = "储蓄卡"
            ll_bank_code.isEnabled = false
            val bankCode = getCode(intent.getStringExtra("BankCardNo"))
            val bankName = intent.getStringExtra("BankName")
            tv_bank_code.text = "$bankName（$bankCode）"
        } else {
            toolbarTitle.text = "充值"
            tv_payment.text = "充值方式"
            ll_bank_code.isEnabled = true
            setTopText(topUpType)
            if (getType() == 2) {
                et_amount.isEnabled = false
                et_amount.setText(String.format("%.2f", getTransferAmount()))
            } else et_amount.isEnabled = true
        }
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        tv_type.text = if (getType() == 0) "提现金额" else "充值金额"
        tv_type_amount.text = if (getType() == 0) "可提现金额：${String.format("%.2f", usableAmt)}元" else {
            if (getType() == 2) {
                "批量转账金额已累加，不可更改"
            } else {
                "可用金额：${String.format("%.2f", usableAmt)}元"
            }
        }
        if (getType() == 0) tv_withdrawal.visibility = View.VISIBLE else {
            tv_withdrawal.visibility = View.GONE
            tv_type_amount.visibility = View.GONE
        }
        tv_withdrawal.setOnClickListener {
            et_amount.setText(DecimalFormat("###0.00").format(usableAmt))
        }
        initET()
        complete()
    }

    private fun initET() {
        et_amount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable) {
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (text != null && text.isNotBlank()) {
                    if (text.toString().trim().substring(0, 1) == ".") {
                        et_amount.setText("0" + et_amount.text.toString().trim())
                        et_amount.setSelection(2)
                        return
                    }
                    // 判断小数点后只能输入两位
                    if (text.toString().contains(".")) {
                        if (text.length - 1 - text.toString().indexOf(".") > 2) {
                            val s = text.toString().subSequence(0, text.toString().indexOf(".") + 3)
                            et_amount.setText(s)
                            et_amount.setSelection(s.length)
                            return
                        }
                    }
                    //如果第一个数字为0，第二个不为点，就不允许输入
                    if (text.toString().startsWith("0") && text.toString().trim().length > 1) {
                        if (text.toString().substring(1, 2) != ".") {
                            et_amount.setText(text.subSequence(0, 1))
                            et_amount.setSelection(1)
                            return
                        }
                    }
                    if (text.toString().toDouble() != 0.0) {
                        if (getType() == 0) {
                            if (text.toString().toDouble() <= usableAmt) {
                                tv_confirm.isEnabled = true
                                setTextContent()
                            } else {
                                tv_confirm.isEnabled = false
                                tv_type_amount.setTextColor(resources.getColor(R.color.viewfinder_laser))
                                tv_type_amount.text = "余额不足,请正确填写金额"
                            }
                        } else {
                            tv_confirm.isEnabled = true
                            setTextContent()
                        }
                    } else {
                        tv_confirm.isEnabled = false
                        setTextContent()
                    }
                } else {
                    tv_confirm.isEnabled = true
                    setTextContent()
                }
            }
        })
    }

    private fun setTextContent() {
        tv_type_amount.setTextColor(resources.getColor(R.color.font_cc))
        tv_type_amount.text = if (getType() == 0) "可提现金额：${String.format("%.2f", usableAmt)}元" else "可用金额：${String.format("%.2f", usableAmt)}元"
    }

    private fun complete() {
        ll_bank_code.setOnClickListener { presenter.showTopUp(ll_bank_code) }

        tv_confirm.setOnClickListener {
            val amount = et_amount.text.toString()
            if (amount.isNotBlank()) {
                if (getType() != 0 && topUpType == 1) {
                    presenter.getTopUpData(amount.toDouble())
                } else {
                    val intent1 = Intent(this@WithdrawalActivity, GoldWebActivity::class.java)
                    intent1.putExtra("UserNo", getUserNo())
                    intent1.putExtra("Amount", amount.toDouble())
                    intent1.putExtra("Type", if (getType() == 0) getType() else 1)
                    startActivityForResult(intent1, 22)
                }
            } else {
                showMsg("请输入正确金额")
            }
        }
    }

    //显示充值码信息
    fun showTopUpInfo(returnString: String) {
        val intent1 = Intent(this@WithdrawalActivity, TopUpInfoActivity::class.java)
        intent1.putExtra("returnString", returnString)
        startActivity(intent1)
        if (getType() == 2) {
            val intent2 = Intent()
            intent2.putExtra("returnString", returnString)
            intent2.putExtra("payType", 2)
            setResult(55, intent2)
        }
        finish()
    }

    fun setTopText(type: Int) {
        topUpType = type
        when (type) {
            0 -> {//快捷充值
                val prompt = resources.getString(R.string.fast_hints)
                tv_prompt.text = Html.fromHtml(prompt)
                tv_bank_code.text = "快捷充值"
                tv_confirm.text = "确认充值"
            }
            1 -> {//充值码充值
                val prompt = resources.getString(R.string.top_up_hints)
                tv_prompt.text = Html.fromHtml(prompt)
                tv_bank_code.text = "充值码转账"
                tv_confirm.text = "获取充值码"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 22 && resultCode == 22) {
            if (getType() == 2) {
                val intent2 = Intent()
                intent2.putExtra("TradeSn", data?.getStringExtra("TradeSn"))
                intent2.putExtra("payType", 0)
                setResult(55, intent2)
            } else {
                setResult(11)
            }
            finish()
        }
    }

    private fun getCode(code: String): String {
        if (code.isNotBlank() && code.length > 4) {
            return code.substring(code.length - 4, code.length)
        } else return code
    }

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }
}
