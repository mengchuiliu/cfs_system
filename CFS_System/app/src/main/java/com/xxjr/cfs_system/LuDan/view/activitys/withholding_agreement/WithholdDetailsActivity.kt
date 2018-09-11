package com.xxjr.cfs_system.LuDan.view.activitys.withholding_agreement

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.RemindDetailAdapter
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import entity.CommonItem
import entity.SignInfo
import kotlinx.android.synthetic.main.activity_common_list.*

class WithholdDetailsActivity : BaseActivity<BasePresenter<*, *>, BaseViewInter>() {
    private lateinit var signInfo: SignInfo

    override fun getPresenter(): BasePresenter<*, *>? = null

    override fun getLayoutId(): Int = R.layout.activity_common_list

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "代扣协议详情"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        signInfo = (intent.getSerializableExtra("signInfo") as? SignInfo) ?: SignInfo()
        initRV()
    }

    private fun initRV() {
        rv_remind.layoutManager = LinearLayoutManager(this@WithholdDetailsActivity)
        rv_remind.adapter = RemindDetailAdapter(this@WithholdDetailsActivity, getItemData())
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    private fun getItemData() = mutableListOf<CommonItem<Any>>().apply {
        for (i in 0..16) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> {
                        type = 5
                        name = "基本信息"
                    }
                    1 -> {
                        type = 2
                        name = "所属公司      "
                        content = signInfo.companyName ?: ""
                    }
                    2 -> {
                        type = 2
                        name = "持卡人姓名  "
                        content = signInfo.accountName ?: ""
                    }
                    3 -> {
                        type = 2
                        name = "签约银行      "
                        content = signInfo.bankCode ?: ""
                    }
                    4 -> {
                        type = 2
                        name = "银行卡类型  "
                        content = signInfo.accountType ?: ""
                    }
                    5 -> {
                        type = 2
                        name = "银行卡号      "
                        content = signInfo.accountNo ?: ""
                    }
                    6 -> {
                        type = 2
                        name = "证件号码      "
                        content = signInfo.IDCardNo ?: ""
                    }
                    7 -> {
                        type = 2
                        name = "签约者身份  "
                        content = signInfo.signer ?: ""
                    }
                    8 -> {
                        type = 2
                        name = "预留手机号  "
                        content = signInfo.telPhone ?: ""
                    }
                    9 -> {
                        type = 2
                        name = "银行规则      "
                        content = signInfo.bankRule ?: ""
                    }
                    10 -> type = 4
                    11 -> type = 0
                    12 -> {
                        type = 5
                        name = "协议信息"
                    }
                    13 -> {
                        type = 2
                        name = " 协 议 号 ："
                        content = signInfo.protocolNo ?: ""
                    }
                    14 -> {
                        type = 2
                        name = "协议状态："
                        content = signInfo.protocolState ?: ""
                    }
                    15 -> {
                        type = 2
                        name = "冻结原因："
                        content = signInfo.remark ?: ""
                    }
                    16 -> type = 4
                }
            })
        }
    }
}
