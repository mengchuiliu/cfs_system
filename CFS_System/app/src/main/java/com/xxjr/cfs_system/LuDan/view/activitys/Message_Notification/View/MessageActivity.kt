package com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.View

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.MessageAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.Presenter.MessagePresenter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_common_list_water.*
import refresh_recyclerview.SimpleItemDecoration

class MessageActivity : BaseActivity<MessagePresenter, MessageActivity>(), BaseViewInter {
    private lateinit var adapter: MessageAdapter

    override fun getPresenter(): MessagePresenter = MessagePresenter()

    override fun getLayoutId(): Int = R.layout.activity_common_list_water

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "消息"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        initRV()
    }

    override fun onResume() {
        super.onResume()
        presenter.setDefaultValue()
    }

    private fun initRV() {
        rv_remind.layoutManager = LinearLayoutManager(this@MessageActivity)
        val simpleItemDecoration = SimpleItemDecoration(this@MessageActivity, 1)
        simpleItemDecoration.setHead(true, 10)
        rv_remind.addItemDecoration(simpleItemDecoration)
        adapter = MessageAdapter(this@MessageActivity, arrayListOf())
        adapter.setRecyclerItemClickListener(RecycleItemClickListener {
            toGoActivity(adapter.getData()[it].type, adapter.getData()[it].name ?: "")
        })
        rv_remind.adapter = adapter
    }

    fun refreshData(commonItems: MutableList<CommonItem<Any>>) {
        adapter.setNewData(commonItems)
    }

    //跳转详情页面
    private fun toGoActivity(type: Int, categoryTitle: String) {
        val intent1 = Intent(this@MessageActivity, TrainingNotifyActivity::class.java)
        intent1.putExtra("CategoryId", type)
        intent1.putExtra("CategoryTitle", categoryTitle)
        startActivity(intent1)
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }
}
