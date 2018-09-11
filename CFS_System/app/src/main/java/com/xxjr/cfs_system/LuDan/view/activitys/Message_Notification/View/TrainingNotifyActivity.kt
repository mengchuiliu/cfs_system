package com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.View

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.Presenter.TrainingNotifyPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.staff_training.TrainingDetailActivity
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_common_pull_list_water.*
import refresh_recyclerview.PullToRefreshRecyclerView
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class TrainingNotifyActivity : BaseActivity<TrainingNotifyPresenter, TrainingNotifyActivity>(), BaseViewInter {
    private lateinit var adapter: CommonAdapter<CommonItem<Any>>
    var messages = mutableListOf<CommonItem<Any>>()
    var page = 0
    var pull = false

    override fun getPresenter(): TrainingNotifyPresenter = TrainingNotifyPresenter()

    override fun getLayoutId(): Int = R.layout.activity_common_pull_list_water

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    fun getCategoryId() = intent.getIntExtra("CategoryId", -1)

    fun setTitleText(title: String) {
        toolbarTitle.text = title
    }

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = intent.getStringExtra("CategoryTitle") ?: ""
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        initRV()
    }

    override fun onResume() {
        super.onResume()
        page = 0
        pull = false
        presenter.setDefaultValue()
    }

    private fun initRV() {
        adapter = object : CommonAdapter<CommonItem<Any>>(this@TrainingNotifyActivity, messages, R.layout.item_message_summary) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.setText(R.id.tv_notify_time, item.date)
                holder.setText(R.id.tv_title, item.hintContent)
                holder.setText(R.id.tv_create_time, item.date)
                holder.setVisible(R.id.tv_read, item.isLineShow)
                holder.getView<TextView>(R.id.tv_read).isEnabled = !item.isEnable
                holder.setText(R.id.tv_message_title, item.name)
                holder.setText(R.id.tv_message_content, item.content)
                holder.setVisible(R.id.tv_isRead, item.isLineShow && !item.isEnable)
                holder.setVisible(R.id.iv_point, !item.isClick)
                holder.convertView.setOnClickListener {
                    if (!item.isClick) {
                        presenter.readMessage(item.type)
                    }
                    if (item.remark == "J06") toStaffDetailActivity(item.payType)
                    else toMessageDetailActivity(item.type, item.isLineShow, item.isEnable)
                }
                holder.setOnClickListener(R.id.tv_read) {
                    presenter.readFeedMessage(item.type, position)
                }
            }
        }
        rv_remind.setRecyclerBackground(resources.getColor(R.color.blank_bg))
        rv_remind.setAdapter(adapter)
        rv_remind.setOnRefreshListener(object : PullToRefreshRecyclerView.OnRefreshListener {
            override fun onPullDownRefresh() {
                Handler().postDelayed({
                    pull = false
                    page = 0
                    presenter.getNotifyData()
                }, 500)
            }

            override fun onLoadMore() {
                Handler().postDelayed({
                    pull = true
                    page++
                    presenter.getNotifyData()
                }, 500)
            }
        })
//        Handler().postDelayed({ rv_remind.completeLoadMore() }, 1000)
    }

    fun completeRefresh() {
        if (pull) rv_remind.completeLoadMore() else rv_remind.completeRefresh()
    }

    fun refreshData() {
        adapter.setNewData(messages)
    }

    fun refreshItem(postion: Int) {
        val item = adapter.datas[postion]
        item.isEnable = true
        adapter.notifyItemChanged(postion, item)
    }

    //员工详情
    private fun toStaffDetailActivity(FuncId: String) {
        val intent1 = Intent(this@TrainingNotifyActivity, TrainingDetailActivity::class.java)
        intent1.putExtra("TrainingId", FuncId)
        startActivity(intent1)
    }

    //消息详情界面
    private fun toMessageDetailActivity(notifyId: Int, IsReadFb: Boolean, IsReadConfirm: Boolean) {
        val intent1 = Intent(this@TrainingNotifyActivity, MeetingDetailActivity::class.java)
        intent1.putExtra("CategoryTitle", intent.getStringExtra("CategoryTitle") ?: "")
        intent1.putExtra("NotifyId", notifyId)
        intent1.putExtra("IsReadFb", IsReadFb)
        intent1.putExtra("IsReadConfirm", IsReadConfirm)
        startActivity(intent1)
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }
}
