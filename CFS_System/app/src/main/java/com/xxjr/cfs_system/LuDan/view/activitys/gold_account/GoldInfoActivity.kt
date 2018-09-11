package com.xxjr.cfs_system.LuDan.view.activitys.gold_account

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.GoldInfoAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.presenter.GoldInfoPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import entity.GoldRegisteredInfo
import kotlinx.android.synthetic.main.activity_gold_set.*

class GoldInfoActivity : BaseActivity<GoldInfoPresenter, GoldInfoActivity>(), BaseViewInter {
    private var adapter: GoldInfoAdapter? = null

    fun getType() = intent.getIntExtra("GoldInfoType", 0) //0->查看自己详情 1->查看客户详情

    override fun getPresenter(): GoldInfoPresenter = GoldInfoPresenter()

    override fun getLayoutId(): Int = R.layout.activity_gold_set

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "注册信息"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        presenter.setDefaultValue()
    }

    fun getGoldUserInfo(): GoldRegisteredInfo? = (application as MyApplication).goldRegister

    fun initRV(commonItems: MutableList<CommonItem<Any>>) {
        rv_gold_set.layoutManager = LinearLayoutManager(this@GoldInfoActivity)
        adapter = GoldInfoAdapter(this@GoldInfoActivity, commonItems)
        adapter?.setOnItemClick(RecycleItemClickListener { position ->
            when (position) {
                1 -> {
                    val intent1 = Intent(this@GoldInfoActivity, GoldWebActivity::class.java)
                    intent1.putExtra("UserNo", getGoldUserInfo()?.userNo ?: "")
                    intent1.putExtra("Type", 2)
                    startActivity(intent1)
                }
                2 -> {
                    val intent1 = Intent(this@GoldInfoActivity, GoldWebActivity::class.java)
                    intent1.putExtra("UserNo", getGoldUserInfo()?.userNo ?: "")
                    intent1.putExtra("Type", 3)
                    startActivityForResult(intent1, 99)
                }
            }
        })
        rv_gold_set.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 99 && resultCode == 22) {
//            val phone = data?.getStringExtra("Phone")
//            if (!phone.isNullOrBlank()) {
//                getGoldUserInfo()?.telPhone = phone
//                val commonItem = adapter?.datas?.get(2) as CommonItem<Any>
//                commonItem.name = phone
//                adapter?.notifyItemChanged(2, commonItem)
//            }
        }
    }
}
