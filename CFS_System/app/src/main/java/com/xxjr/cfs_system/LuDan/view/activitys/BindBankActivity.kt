package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.BindBankPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.BindBankVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.ToastShow
import entity.Bank
import kotlinx.android.synthetic.main.activity_bind_bank.*
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class BindBankActivity : BaseActivity<BindBankPresenter, BindBankVInter>(), BindBankVInter, View.OnClickListener {

    override fun getPresenter(): BindBankPresenter = BindBankPresenter()

    override fun getLayoutId(): Int = R.layout.activity_bind_bank

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        presenter.setDefaultValue()
        iv_back.setOnClickListener(this)
        iv_add.setOnClickListener(this)
        ll_add.setOnClickListener(this)
    }

    override fun initRecycler(bankCards: MutableList<Bank>?) {
        if (bankCards == null || bankCards.isEmpty()) {
            rv_bank.visibility = View.GONE
            ll_add.visibility = View.VISIBLE
        } else {
            rv_bank.visibility = View.VISIBLE
            ll_add.visibility = View.GONE
            rv_bank.layoutManager = LinearLayoutManager(this@BindBankActivity)
            val adapter = object : CommonAdapter<Bank>(this@BindBankActivity, bankCards, R.layout.item_bank_detail) {
                override fun convert(holder: BaseViewHolder?, bank: Bank?, position: Int) {
                    holder?.setText(R.id.tv_bank_name, bank?.bankName)
                    holder?.setText(R.id.tv_card_type, bank?.cardType)
                    if (!bank?.bankCode.isNullOrBlank() && bank?.bankCode?.length ?: 0 > 4) {
                        val code = StringBuilder("* * * *   * * * *   * * * *   ")
                        code.append(bank?.bankCode?.substring(bank.bankCode?.length!! - 4, bank.bankCode?.length!!))
                        holder?.setText(R.id.tv_bank_code, code)
                    }
                    holder?.convertView?.setOnClickListener({
                        presenter.showBankClick(rv_bank, bank)
                    })
                }
            }
            rv_bank.adapter = adapter
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            iv_back -> finish()
            iv_add, ll_add -> {
                val intent = Intent(this@BindBankActivity, AddBankActivity::class.java)
                startActivityForResult(intent, 66)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Constants.RESULT_CODE_ADD_BANK) {
            //添加完成刷新数据
            presenter.getBankCardsData()
        }
    }

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }
}
