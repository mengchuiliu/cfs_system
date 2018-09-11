package com.xxjr.cfs_system.LuDan.view.activitys.withholding_agreement

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.SwipeMenuLayout
import com.xxjr.cfs_system.LuDan.presenter.WithholdingPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.SignInfo
import kotlinx.android.synthetic.main.activity_gold_set.*
import refresh_recyclerview.SimpleItemDecoration
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class WithholdingList : BaseActivity<WithholdingPresenter, WithholdingList>(), BaseViewInter {
    private lateinit var adapter: CommonAdapter<SignInfo>
    override fun getPresenter(): WithholdingPresenter = WithholdingPresenter()

    override fun getLayoutId(): Int = R.layout.activity_gold_set

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "代扣协议"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        ivRight.setImageResource(R.mipmap.icon_add)
        ivRight.setOnClickListener {
            val intent = Intent(this@WithholdingList, AgreementActivity::class.java)
            startActivityForResult(intent, 11)
        }
        initRV()
        presenter.setDefaultValue()
    }

    private fun initRV() {
        rv_gold_set.layoutManager = LinearLayoutManager(this@WithholdingList)
        rv_gold_set.addItemDecoration(SimpleItemDecoration(this@WithholdingList, 8))
        adapter = object : CommonAdapter<SignInfo>(this@WithholdingList, mutableListOf(), R.layout.item_contract) {
            override fun convert(holder: BaseViewHolder, sign: SignInfo, position: Int) {
                (holder.convertView as SwipeMenuLayout).setIos(true).setLeftSwipe(true).isSwipeEnable = false
                holder.setBackgroundRes(R.id.ll_home, R.drawable.clicked_white)
                holder.setVisible(R.id.ll_code, false)
                holder.setText(R.id.tv_title_1, "所属公司：")
                holder.setText(R.id.tv_title_2, "账号类型：")
                holder.setText(R.id.tv_title_3, " 持 卡 人 ：")
                holder.setText(R.id.tv_title_4, "代扣平台：")

                holder.setText(R.id.tv_customer, "${sign.companyName ?: ""}（${sign.zoneName
                        ?: ""}）")
                holder.setText(R.id.tv_pact_type, sign.accountProp)
                holder.setText(R.id.tv_loan_nub, "${sign.accountName}${if (sign.accountType.isNullOrEmpty()) "" else "—${sign.accountType}"}")
                holder.setText(R.id.tv_contract_status, sign.aisleType)

                holder.setOnClickListener(R.id.ll_home) {
                    val intent = Intent(this@WithholdingList, WithholdDetailsActivity::class.java)
                    intent.putExtra("signInfo", sign)
                    startActivity(intent)
                }
            }
        }
        rv_gold_set.adapter = adapter
    }

    fun freshRv(datas: MutableList<SignInfo>) {
        adapter.setNewData(datas)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 11) {
            presenter.setDefaultValue()
        }
    }
}
