package com.xxjr.cfs_system.LuDan.view.activitys.mortgage_score

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.IntegralPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.MortgageScoreVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.MortgageScore
import kotlinx.android.synthetic.main.activity_integral_detail.*
import refresh_recyclerview.DividerItemDecoration
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import java.util.ArrayList

class IntegralDetailActivity : BaseActivity<IntegralPresenter, MortgageScoreVInter>(), MortgageScoreVInter {
    private var scores = mutableListOf<MortgageScore>()
    private var adapter: CommonAdapter<MortgageScore>? = null

    fun getUserID(): String = intent.getStringExtra("UserID")

    fun getYearMonth(): String = intent.getStringExtra("YearMonth")

    override fun getScores(): MutableList<MortgageScore> = scores

    override fun getPull(): Boolean = false

    override fun setPull(pull: Boolean) {}

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun getPresenter(): IntegralPresenter = IntegralPresenter()

    override fun getLayoutId(): Int = R.layout.activity_integral_detail

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "积分详情"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        initRVIntegral()
        presenter.setDefaultValue()
    }

    override fun setMortgageId(mortgageId: String) {}

    override fun setDateView(date: String, date1: String) {}

    //数据显示
    private fun initRVIntegral() {
        rv_integral.layoutManager = LinearLayoutManager(this@IntegralDetailActivity)
        rv_integral.addItemDecoration(DividerItemDecoration(this@IntegralDetailActivity, DividerItemDecoration.VERTICAL_LIST))
        adapter = object : CommonAdapter<MortgageScore>(this@IntegralDetailActivity, ArrayList(), R.layout.item_integral) {
            override fun convert(holder: BaseViewHolder, score: MortgageScore, position: Int) {
                holder.setVisible(R.id.ll_remark, false)
                holder.setVisible(R.id.tv_time, true)
                holder.setVisible(R.id.ll_type, true)
                holder.setText(R.id.tv_name, intent.getStringExtra("MortgageName") ?: "")
                holder.setText(R.id.tv_time, score.updateTime)
                holder.setText(R.id.tv_type, score.scoreType)
                holder.setText(R.id.tv_integral_type, score.scoreDescribe)
            }
        }
        rv_integral.adapter = adapter
    }

    override fun refreshData() {
        if (scores.isEmpty()) {
            rl_nodata.visibility = View.VISIBLE
            rv_integral.visibility = View.GONE
        } else {
            rl_nodata.visibility = View.GONE
            rv_integral.visibility = View.VISIBLE
        }
        adapter?.setNewData(scores)
    }

    override fun completeRefresh() {}

    override fun onStart() {
        super.onStart()
        setWater(water)
    }
}
