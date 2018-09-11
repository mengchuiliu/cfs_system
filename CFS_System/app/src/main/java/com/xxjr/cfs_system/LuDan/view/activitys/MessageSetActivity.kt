package com.xxjr.cfs_system.LuDan.view.activitys

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.orhanobut.hawk.Hawk
import com.suke.widget.SwitchButton
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.MessageSetP
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_common_list.*
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class MessageSetActivity : BaseActivity<MessageSetP, MessageSetActivity>(), BaseViewInter {
    lateinit var commonItems: MutableList<CommonItem<Any>>
    private lateinit var adapter: CommonAdapter<CommonItem<Any>>

    override fun getPresenter(): MessageSetP = MessageSetP()

    override fun getLayoutId(): Int = R.layout.activity_common_list

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "通知设置"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        commonItems = presenter.getItemData(presenter.getMenus(Hawk.get<String>("PermitValue")))
        initRV()
        presenter.setDefaultValue()
    }

    private fun initRV() {
        rv_remind.layoutManager = LinearLayoutManager(this@MessageSetActivity)
        adapter = object : CommonAdapter<CommonItem<Any>>(this@MessageSetActivity, arrayListOf(), R.layout.item_recycle_set) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.setVisible(R.id.iv_set_icon, false)
                holder.setVisible(R.id.bt_switch, true)
                holder.setVisible(R.id.line_set, true)
                holder.setText(R.id.tv_set_content, item.name)
                val bt = holder.getView<SwitchButton>(R.id.bt_switch)
                bt.isChecked = item.isEnable
                bt.setOnCheckedChangeListener { _, isChecked ->
                    presenter.pos = position
                    val jsonMessage = StringBuilder()
                    commonItems[position].isEnable = isChecked
                    for (comm in commonItems) {
                        if (comm.isEnable) {
                            if (jsonMessage.isBlank()) jsonMessage.append(comm.remark) else jsonMessage.append("-").append(comm.remark)
                        }
                    }
                    presenter.isRemindService(jsonMessage.toString(), "UPD", 2, true)
                }
            }
        }
        rv_remind.adapter = adapter
    }

    fun refreshData(datas: MutableList<CommonItem<Any>>) {
        adapter.setNewData(datas)
    }

    fun refreshItem(pos: Int, isSwitch: Boolean) {
        val item = adapter.datas[pos]
        item.isEnable = isSwitch
        adapter.notifyItemChanged(pos, item)
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }
}
