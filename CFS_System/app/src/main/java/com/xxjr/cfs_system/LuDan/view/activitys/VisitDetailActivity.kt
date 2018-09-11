package com.xxjr.cfs_system.LuDan.view.activitys

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.VisitDetailPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.VisitDetailVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_visit_detail.*
import refresh_recyclerview.DividerItemDecoration
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class VisitDetailActivity : BaseActivity<VisitDetailPresenter, VisitDetailVInter>(), VisitDetailVInter {
    override fun getPresenter(): VisitDetailPresenter = VisitDetailPresenter()

    override fun getLayoutId(): Int = R.layout.activity_visit_detail

    override fun getCompanyId(): String = intent.getStringExtra("CompanyId") ?: ""

    override fun getCurYear(): Int = intent.getIntExtra("year", 0)

    override fun getCurMonth(): Int = intent.getIntExtra("month", 0)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = intent.getStringExtra("CompanyName") ?: ""
        presenter.setDefaultValue()
    }

    override fun initStoreRV(commonItems: MutableList<CommonItem<Any>>) {
        rv_store.layoutManager = LinearLayoutManager(this@VisitDetailActivity)
        rv_store.addItemDecoration(DividerItemDecoration(this@VisitDetailActivity, DividerItemDecoration.VERTICAL_LIST))
        rv_store.adapter = object : CommonAdapter<CommonItem<Any>>(this@VisitDetailActivity, commonItems, R.layout.item_visit_detail) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                val name = holder.getView<TextView>(R.id.tv_name)
                val person = holder.getView<TextView>(R.id.tv_persons)
                name.text = item.name
                if (item.isLineShow) {
                    person.text = item.content
                    name.setTextColor(resources.getColor(R.color.font_c5))
                    person.setTextColor(resources.getColor(R.color.font_c5))
                } else {
                    person.text = "${item.position}"
                    name.setTextColor(resources.getColor(R.color.font_c6))
                    person.setTextColor(resources.getColor(R.color.font_c6))
                }
            }
        }
    }

    override fun initClerkRV(commonItems: MutableList<CommonItem<Any>>) {
        rv_clerk.layoutManager = LinearLayoutManager(this@VisitDetailActivity)
        rv_clerk.addItemDecoration(DividerItemDecoration(this@VisitDetailActivity, DividerItemDecoration.VERTICAL_LIST))
        rv_clerk.adapter = object : CommonAdapter<CommonItem<Any>>(this@VisitDetailActivity, commonItems, R.layout.item_visit_detail) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                val name = holder.getView<TextView>(R.id.tv_name)
                val person = holder.getView<TextView>(R.id.tv_persons)
                name.text = item.name
                if (item.isLineShow) {
                    person.text = item.content
                    name.setTextColor(resources.getColor(R.color.font_c6))
                    person.setTextColor(resources.getColor(R.color.font_c6))
                } else {
                    person.text = "${item.position}"
                    name.setTextColor(resources.getColor(R.color.font_c6))
                    person.setTextColor(resources.getColor(R.color.font_c6))
                }
            }
        }
    }

    override fun isShowBacking(): Boolean = true

    override fun showMsg(msg: String?) = ToastShow.showShort(application, msg)

    override fun onStart() {
        super.onStart()
        setWater(water)
    }
}
