package com.xxjr.cfs_system.LuDan.view.activitys.gold_account

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.GoldDetailPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.ToastShow
import entity.GoldTradeDetail
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.toolbar.*
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import java.util.*

class DetailActivity : BaseActivity<GoldDetailPresenter, DetailActivity>(), BaseViewInter, View.OnClickListener {
    private var adapter: CommonAdapter<GoldTradeDetail>? = null

    override fun getPresenter(): GoldDetailPresenter = GoldDetailPresenter()

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun getLayoutId(): Int = R.layout.activity_detail

    fun getUserNo(): String = intent.getStringExtra("UserNo")

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "${DateUtil.getChooseDate(Date())}明细"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener(this)
        ivRight.setImageResource(R.mipmap.icon_date)
        ivRight.setOnClickListener(this)
        presenter.setDefaultValue()
    }

    fun initRv(commonItems: MutableList<GoldTradeDetail>) {
        if (commonItems.isEmpty()) {
            rl_nodata.visibility = View.VISIBLE
        } else {
            rl_nodata.visibility = View.GONE
        }
        rv_detail.layoutManager = LinearLayoutManager(this@DetailActivity)
        rv_detail.addItemDecoration(DividerItemDecoration(this@DetailActivity, DividerItemDecoration.VERTICAL))
        adapter = object : CommonAdapter<GoldTradeDetail>(this@DetailActivity, commonItems, R.layout.item_gold_detail) {
            override fun convert(holder: BaseViewHolder, item: GoldTradeDetail, position: Int) {
                holder.setText(R.id.tv_type, item.tradeTp)
                holder.setText(R.id.tv_time, item.tradeTime)
                if (item.isAdd) holder.setTextColorRes(R.id.tv_amount, R.color.detail2)
                else holder.setTextColorRes(R.id.tv_amount, R.color.detail1)
                holder.setText(R.id.tv_amount, "${if (item.isAdd) "+" else "-"}${String.format("%.2f", item.changeAmt)}")
                holder.convertView.setOnClickListener {
                    val intent1 = Intent(this@DetailActivity, TradeDetailActivity::class.java)
                    intent1.putExtra("tradeDetail", item)
                    startActivity(intent1)
                }
            }
        }
        rv_detail.adapter = adapter
    }

    override fun onClick(p0: View?) {
        when (p0) {
            iv_back -> finish()
            iv_right -> presenter.showTime(toolbarTitle)
        }
    }

//    override fun onStart() {
//        super.onStart()
//        setWater(water)
//    }
}
