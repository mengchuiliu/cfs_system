package com.xxjr.cfs_system.LuDan.view.activitys.gold_account

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.main.MyApplication
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_gold_regist.*
import kotlinx.android.synthetic.main.toolbar.*
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class GoldRegistActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold_regist)
        (applicationContext as MyApplication).addActivity(this@GoldRegistActivity)
        gold_title.findViewById<TextView>(R.id.title).text = "注册金账户"
        iv_back.visibility = View.VISIBLE
        iv_back.setOnClickListener { finish() }
        initRv()
    }

    private fun initRv() {
        rv_registered.layoutManager = LinearLayoutManager(this@GoldRegistActivity)
        rv_registered.adapter = object : CommonAdapter<CommonItem<Any>>(this@GoldRegistActivity, getRVData(), R.layout.item_regist_gold) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.setImageResource(R.id.iv_icon, item.icon)
                holder.setText(R.id.tv_type, item.name)
                if (item.isLineShow == true) holder.convertView.visibility = View.INVISIBLE
                holder.convertView.setOnClickListener {
                    val intent1 = Intent(this@GoldRegistActivity, RegisteredActivity::class.java)
                    intent1.putExtra("Type", position + 1)
                    intent1.putExtra("AccountType", 1)
                    startActivityForResult(intent1, 1)
                }
            }
        }
    }

    private fun getRVData(): MutableList<CommonItem<Any>> {
        val list = mutableListOf<CommonItem<Any>>()
        var commonItem: CommonItem<Any>
        for (i in 0..1) {
            commonItem = CommonItem()
            when (i) {
                0 -> {
                    commonItem.name = "个人注册"
                    commonItem.icon = R.mipmap.personal
                }
                1 -> {
                    commonItem.name = "企业注册"
                    commonItem.icon = R.mipmap.enterprise
                    commonItem.isLineShow = true
                }
            }
            list.add(commonItem)
        }
        return list
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 11) {
            this@GoldRegistActivity.finish()
        }
    }
}
