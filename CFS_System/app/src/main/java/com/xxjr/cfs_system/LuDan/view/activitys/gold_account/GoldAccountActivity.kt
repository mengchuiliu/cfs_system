package com.xxjr.cfs_system.LuDan.view.activitys.gold_account

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.alibaba.fastjson.JSONArray
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.GoldAccountP
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.he_xin_chan_rong.BindAccountActivity
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.he_xin_chan_rong.ExchangeActivity
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import entity.GoldRegisteredInfo
import kotlinx.android.synthetic.main.activity_gold_account.*
import refresh_recyclerview.DividerGridItemDecoration
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class GoldAccountActivity : BaseActivity<GoldAccountP, GoldAccountActivity>(), BaseViewInter {
    private lateinit var adapter: CommonAdapter<CommonItem<Any>>

    fun getGoldUserInfo(): GoldRegisteredInfo? = (application as MyApplication).goldRegister

    override fun getPresenter(): GoldAccountP = GoldAccountP()

    override fun getLayoutId(): Int = R.layout.activity_gold_account

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "账户"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        ivRight.setImageResource(R.mipmap.gold_info)
        ivRight.setOnClickListener {
            val intent = Intent(this@GoldAccountActivity, GoldInfoActivity::class.java)
            startActivity(intent)
        }
        initRv()
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    private fun initRv() {
        rv_amount.layoutManager = GridLayoutManager(this@GoldAccountActivity, 2)
//        rv_amount.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rv_amount.addItemDecoration(DividerGridItemDecoration(this@GoldAccountActivity))
        adapter = object : CommonAdapter<CommonItem<Any>>(this@GoldAccountActivity, ArrayList(), R.layout.item_amount) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.setImageResource(R.id.iv_icon, item.icon)
                holder.setText(R.id.tv_amount_type, item.name)
                holder.setText(R.id.tv_amount, String.format("%,.2f", item.percent))
            }
        }
        rv_amount.adapter = adapter

        rv_project.layoutManager = GridLayoutManager(this@GoldAccountActivity, 3)
        rv_project.adapter = object : CommonAdapter<CommonItem<Any>>(this@GoldAccountActivity, presenter.getProjectData(), R.layout.item_project) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.setImageResource(R.id.iv_icon, item.icon)
                holder.setText(R.id.tv_project_name, item.name)
                if (position == presenter.getProjectData().size - 1) {
                    holder.setVisible(R.id.line, false)
                } else {
                    holder.setVisible(R.id.line, true)
                }
                holder.convertView.setOnClickListener { clickEvent(position) }
            }
        }
    }

    fun refreshData(commonItems: MutableList<CommonItem<Any>>, total: Double, state: Int) {
        tv_amount.text = String.format("%,.2f", total)
        tv_gold_state.apply {
            when (state) {
                0 -> {
                    setBackgroundResource(R.drawable.gold_state_bg_0)
                    text = "未通过"
                }
                1 -> {
                    setBackgroundResource(R.drawable.gold_state_bg_1)
                    text = "已通过"
                }
                2 -> {
                    setBackgroundResource(R.drawable.gold_state_bg_2)
                    text = "待验证"
                }
                -1 -> {
                    setBackgroundResource(R.drawable.gold_state_bg_3)
                    text = "未知"
                }
            }
        }
        adapter.setNewData(commonItems)
    }

    private fun clickEvent(position: Int) {
        when (position) {
            0, 1 -> toWithdrawal(position)//提现 //充值
            2 -> {//明细
                val intent = Intent(this@GoldAccountActivity, DetailActivity::class.java)
                intent.putExtra("UserNo", getGoldUserInfo()?.userNo ?: "")
                startActivity(intent)
            }
            3 -> {//顾客账户
                val intent = Intent(this@GoldAccountActivity, CustomerAccountActivity::class.java)
                startActivity(intent)
            }
            4 -> {//核新产融
                presenter.checkHeXinAccount()
            }
        }
    }

    private fun toWithdrawal(type: Int) {
        val intent = Intent(this@GoldAccountActivity, WithdrawalActivity::class.java)
        intent.putExtra("Type", type)// 0->提现 1->充值
        intent.putExtra("UserNo", getGoldUserInfo()?.userNo ?: "")
        intent.putExtra("BankCardNo", getGoldUserInfo()?.bankNo ?: "")
        intent.putExtra("BankName", getGoldUserInfo()?.bankName ?: "")
        intent.putExtra("UsableAmt", if (adapter.datas.isEmpty()) 0.0 else adapter.datas[0].percent)
        startActivityForResult(intent, 11)
    }

    fun toRegisterOrExchange(result: String) {
        if (result == "0") {//未绑定
            val intent = Intent(this@GoldAccountActivity, BindAccountActivity::class.java)
            intent.putExtra("UsableAmount", if (adapter.datas.isEmpty()) "0.00" else String.format("%.2f", adapter.datas[0].percent))
            startActivity(intent)
        } else {
            if (result.isNotBlank()) {
                val jsonArray = JSONArray.parseArray(result)
                if (jsonArray.isNotEmpty()) {
                    val jsonObject = jsonArray.getJSONObject(0)
                    val intent = Intent(this@GoldAccountActivity, ExchangeActivity::class.java)
                    intent.putExtra("UsableAmount", if (adapter.datas.isEmpty()) "0.00" else String.format("%.2f", adapter.datas[0].percent))
                    intent.putExtra("Name", jsonObject.getString("UserName") ?: "")
                    intent.putExtra("Account", jsonObject.getString("NFexAccount") ?: "")
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.getAccountBalance()
    }

    fun dataFailed(msg: String?) {
        showMsg("金账户错误$msg")
        this.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11 && resultCode == 11) {
//            presenter.getAccountBalance()
        }
    }
}
