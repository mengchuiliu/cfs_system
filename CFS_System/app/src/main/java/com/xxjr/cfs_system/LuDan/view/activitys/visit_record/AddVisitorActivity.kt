package com.xxjr.cfs_system.LuDan.view.activitys.visit_record

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.AgreementAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.presenter.AddVisitPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.AddVisitVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_agreement.*
import kotlinx.android.synthetic.main.toolbar.*

class AddVisitorActivity : BaseActivity<AddVisitPresenter, AddVisitVInter>(), AddVisitVInter, View.OnClickListener {
    private var adapter: AgreementAdapter? = null

    override fun getPresenter(): AddVisitPresenter = AddVisitPresenter()

    override fun getLayoutId(): Int = R.layout.activity_agreement

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "新增来访人员"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener(this)
        next.text = "提交"
        next.setOnClickListener(this)
        presenter.setDefaultValue()
    }

    override fun initRV(commonItems: MutableList<CommonItem<Any>>) {
        rv_agree.layoutManager = LinearLayoutManager(this@AddVisitorActivity)
        adapter = AgreementAdapter(this@AddVisitorActivity, commonItems)
        adapter?.setOnItemClick(RecycleItemClickListener { position ->
            hideSoftInput(rv_agree)
            presenter.clickChoose(position, rv_agree)
        })

        adapter?.setTextChangeListener(TextChangeListener { position, text ->
            presenter.editContent(position, text)
        })
        rv_agree.adapter = adapter
    }

    override fun refreshItem(position: Int, text: String) {
        val item: CommonItem<*> = adapter?.datas?.get(position) as CommonItem<*>
        item.content = text
        adapter?.notifyItemChanged(position, item)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            next -> presenter.getData()
            iv_back -> finish()
        }
    }

    private fun hideSoftInput(v: View) {
        val imm = this@AddVisitorActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun complete() {
        showMsg("添加成功")
        setResult(66)
        finish()
    }

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }
}
