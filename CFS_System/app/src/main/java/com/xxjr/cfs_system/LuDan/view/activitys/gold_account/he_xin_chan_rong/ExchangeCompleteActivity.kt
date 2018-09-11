package com.xxjr.cfs_system.LuDan.view.activitys.gold_account.he_xin_chan_rong

import android.os.Bundle
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import kotlinx.android.synthetic.main.activity_exchange_complete.*

class ExchangeCompleteActivity : BaseActivity<BasePresenter<*, *>, BaseViewInter>() {
    override fun getPresenter(): BasePresenter<*, *>? = null

    override fun getLayoutId(): Int = R.layout.activity_exchange_complete

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "兑换"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        showView()
    }

    private fun showView() {
        tv_amount.text = "${intent.getStringExtra("ExchangeAmount")}元"
        tv_complete.setOnClickListener {

        }
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }
}
