package com.xxjr.cfs_system.LuDan.view.activitys.loan_calculator

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.InterestPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_gold_set.*
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import com.xxjr.cfs_system.tools.Utils


class InterestListActivity : BaseActivity<InterestPresenter, InterestListActivity>(), BaseViewInter {
    override fun getPresenter(): InterestPresenter = InterestPresenter()

    override fun getLayoutId(): Int = R.layout.activity_gold_set

    override fun isShowBacking(): Boolean = true

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "贷款利率参考表"
        toolbarTitle.setTextColor(resources.getColor(R.color.font_c3))
        ivBack.visibility = View.VISIBLE
        ivBack.setImageResource(R.mipmap.icon_back_hei)
        ivBack.setOnClickListener { finish() }
        (toolbarTitle.parent as Toolbar).setBackgroundResource(R.color.white)
        initRV()
    }

    private fun initRV() {
        val p = rv_gold_set.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(0, Utils.dip2px(this@InterestListActivity, 12f), 0, 0)
        rv_gold_set.requestLayout()
        rv_gold_set.layoutManager = LinearLayoutManager(this@InterestListActivity)
        rv_gold_set.adapter = object : CommonAdapter<CommonItem<Any>>(this@InterestListActivity, presenter.getDatas(), R.layout.item_details) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.setTextColorRes(R.id.tv_content_name, R.color.font_c3)
                holder.setText(R.id.tv_content_name, item.name)
                holder.setText(R.id.tv_content, item.content)
            }
        }
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)
}
