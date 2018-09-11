package com.xxjr.cfs_system.LuDan.view.activitys.gold_account

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.CustomerAccountP
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.GoldRegisteredInfo
import kotlinx.android.synthetic.main.activity_customer_account.*
import kotlinx.android.synthetic.main.toolbar.*
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class CustomerAccountActivity : BaseActivity<CustomerAccountP, CustomerAccountActivity>(), BaseViewInter, View.OnClickListener {
    override fun getPresenter(): CustomerAccountP = CustomerAccountP()

    override fun getLayoutId(): Int = R.layout.activity_customer_account

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "客户账户"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener(this)
        tv_add_customer.setOnClickListener(this)
        presenter.setDefaultValue()
    }

    fun initRV(commonItems: MutableList<GoldRegisteredInfo>) {
        if (commonItems.isEmpty()) rl_nodata.visibility = View.VISIBLE else rl_nodata.visibility = View.GONE
        rv_customer.layoutManager = LinearLayoutManager(this@CustomerAccountActivity)
        rv_customer.adapter = object : CommonAdapter<GoldRegisteredInfo>(this@CustomerAccountActivity, commonItems, R.layout.item_customer_account) {
            override fun convert(holder: BaseViewHolder, item: GoldRegisteredInfo, position: Int) {
                holder.setText(R.id.tv_customer_name, item.customerName?.trim())
                holder.setText(R.id.tv_customer_phone, item.telPhone)
                holder.setText(R.id.tv_time, item.insertTime)
                holder.convertView.setOnClickListener {
                    val intent1 = Intent(this@CustomerAccountActivity, GoldInfoActivity::class.java)
                    intent1.putExtra("GoldUserInfo", item)
                    intent1.putExtra("GoldInfoType", 1)
                    startActivity(intent1)
                }
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
//        setWater(water)
//    }

    override fun onClick(p0: View?) {
        when (p0) {
            tv_add_customer -> {//新增客户账户
                val intent = Intent(this@CustomerAccountActivity, RegisteredActivity::class.java)
                intent.putExtra("Type", 1)
                intent.putExtra("AccountType", 4)
                startActivityForResult(intent, 8)
            }
            iv_back -> finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 8 && resultCode == 88) {
            presenter.setDefaultValue()
        }
    }
}
