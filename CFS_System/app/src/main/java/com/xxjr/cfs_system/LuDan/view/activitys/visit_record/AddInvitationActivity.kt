package com.xxjr.cfs_system.LuDan.view.activitys.visit_record

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.ItemCommonAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.visit_record.presenter.AddInvitationPresenter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_agreement.*
import kotlinx.android.synthetic.main.toolbar.*

class AddInvitationActivity : BaseActivity<AddInvitationPresenter, AddInvitationActivity>(), BaseViewInter, View.OnClickListener {
    private lateinit var adapter: ItemCommonAdapter

    override fun getPresenter(): AddInvitationPresenter = AddInvitationPresenter()

    override fun getLayoutId(): Int = R.layout.activity_agreement

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "新增邀约客户"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener(this)
        next.text = "提交"
        next.setOnClickListener(this)
        initRV()
        presenter.setDefaultValue()
    }

    private fun initRV() {
        rv_agree.layoutManager = LinearLayoutManager(this@AddInvitationActivity)
        adapter = ItemCommonAdapter(this@AddInvitationActivity, presenter.getItemData())
        adapter.setOnItemClick(RecycleItemClickListener { position ->
            hideSoftInput(rv_agree)
            presenter.timeShow(rv_agree)
        })
        adapter.setTextChangeListener(TextChangeListener { position, text -> presenter.editContent(position, text) })
        adapter.setNoteChangeListener(TextChangeListener { position, text -> presenter.editContent(position, text) })
        rv_agree.adapter = adapter
    }

    fun refreshItem(position: Int, text: String) {
        val item: CommonItem<*> = adapter.datas?.get(position) as CommonItem<*>
        item.content = text
        adapter.notifyItemChanged(position, item)
    }

    private fun hideSoftInput(v: View) {
        val imm = this@AddInvitationActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            next -> presenter.submit()
            iv_back -> finish()
        }
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    fun complete() {
        showMsg("添加成功")
        setResult(88)
        finish()
    }
}
