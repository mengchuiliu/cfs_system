package com.xxjr.cfs_system.LuDan.view.activitys

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.AddBankAdapter
import com.xxjr.cfs_system.LuDan.presenter.UnBindBankPrensenter
import com.xxjr.cfs_system.LuDan.view.viewinter.UnBindBankVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.Bank
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_un_bind_bank.*

class UnBindBankActivity : BaseActivity<UnBindBankPrensenter, UnBindBankVInter>(), UnBindBankVInter, View.OnClickListener {
    private var bank: Bank? = null
    override fun getPresenter(): UnBindBankPrensenter = UnBindBankPrensenter()

    override fun getLayoutId(): Int = R.layout.activity_un_bind_bank

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun getBank(): Bank? = bank

    override fun initView(savedInstanceState: Bundle?) {
        bank = intent.getSerializableExtra("bank") as? Bank
        if (bank == null) {
            tv_unbind.visibility = View.GONE
        } else {
            tv_unbind.visibility = View.VISIBLE
        }
        initBankCardView()
        presenter.setDefaultValue()
        iv_back.setOnClickListener(this)
        tv_unbind.setOnClickListener(this)
    }

    private fun initBankCardView() {
//        iv_bank_logo
        tv_bank_name.text = bank?.bankName ?: ""
        tv_card_type.text = bank?.cardType ?: ""
        tv_bank_code.text = bank?.bankCode ?: ""
    }

    override fun initRecycler(bankDetails: MutableList<CommonItem<Any>>) {
        rv_bank_details.layoutManager = LinearLayoutManager(this@UnBindBankActivity)
        val adapter = AddBankAdapter(this@UnBindBankActivity, bankDetails)
        rv_bank_details.adapter = adapter
    }

    override fun onClick(p0: View?) {
        when (p0) {
            iv_back -> finish()
            tv_unbind -> {
            }
        }
    }
}
