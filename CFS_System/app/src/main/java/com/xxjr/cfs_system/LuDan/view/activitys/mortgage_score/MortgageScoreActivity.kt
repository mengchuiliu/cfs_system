package com.xxjr.cfs_system.LuDan.view.activitys.mortgage_score

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.View
import android.widget.TextView
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.MortgageScoreP
import com.xxjr.cfs_system.LuDan.view.activitys.SearchActivity
import com.xxjr.cfs_system.LuDan.view.viewinter.MortgageScoreVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.MortgageScore
import kotlinx.android.synthetic.main.activity_mortgage_score.*
import kotlinx.android.synthetic.main.toolbar.*
import refresh_recyclerview.PullToRefreshRecyclerView
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import java.util.ArrayList

class MortgageScoreActivity : BaseActivity<MortgageScoreP, MortgageScoreVInter>(), MortgageScoreVInter, View.OnClickListener {
    private var scores = mutableListOf<MortgageScore>()
    private var mortgageId: String = ""
    private var date: String = ""
    private var date1: String = ""
    private var type = 0 // 0->按揭积分 1->服务评分 2->服务评分统计

    private var titleAdapter: CommonAdapter<CommonItem<*>>? = null
    private var adapter: CommonAdapter<MortgageScore>? = null
    private var pull = false
    private var page = 0

    override fun getPresenter(): MortgageScoreP = MortgageScoreP()

    override fun getLayoutId(): Int = R.layout.activity_mortgage_score

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun getPull(): Boolean = pull

    override fun setPull(pull: Boolean) {
        this.pull = pull
    }

    override fun getScores(): MutableList<MortgageScore> = scores

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "绩效积分"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        right.text = "筛选"
        if (type == 1) right.visibility = View.VISIBLE else right.visibility = View.GONE
        right.setOnClickListener(this)
        tv_name.setOnClickListener(this)
        ll_date.setOnClickListener(this)
        tv_date.setOnClickListener(this)
        iv_date.setOnClickListener(this)

        setMortgageId(mortgageId)
        setDateView(date, date1)
        presenter.setDefaultValue()
        initTitle()//初始化过滤条件
        initRVScore()
        presenter.getScoreData(mortgageId, date, date1, page, type)
    }

    override fun setMortgageId(mortgageId: String) {
        pull = false
        if (Hawk.get<String>("UserType") == "22") this.mortgageId = Hawk.get<String>("UserID") else this.mortgageId = mortgageId
        if (this.mortgageId.isBlank()) {
            tv_name.text = "全部"
        } else {
            tv_name.text = CacheProvide.getMortgageName(this.mortgageId.toInt())
        }
    }

    override fun setDateView(date: String, date1: String) {
        pull = false
        this.date = date
        this.date1 = date1
        when (type) {
            0 -> {
                ll_date.visibility = View.GONE
                tv_date.visibility = View.VISIBLE
                if (date.isBlank()) {
                    tv_date.text = "日期：无"
                } else {
                    tv_date.text = "日期：${Utils.FormatTime(date, "yyyy-MM", "yyyy/MM")}"
                }
            }
            1, 2 -> {
                ll_date.visibility = View.VISIBLE
                tv_date.visibility = View.GONE
                if (date.isBlank() && date1.isBlank()) {
                    tv_date1.text = "开始时间：无"
                    tv_date2.text = "结束时间：无"
                } else {
                    tv_date1.text = "开始时间：${Utils.FormatTime(date, "yyyy-MM-dd", "yyyy/MM/dd")}"
                    tv_date2.text = "结束时间：${Utils.FormatTime(date1, "yyyy-MM-dd", "yyyy/MM/dd")}"
                }
            }
        }
    }

    private fun initTitle() {
        rv_title.layoutManager = LinearLayoutManager(this@MortgageScoreActivity, LinearLayoutManager.HORIZONTAL, false)
        titleAdapter = object : CommonAdapter<CommonItem<*>>(this@MortgageScoreActivity, presenter.getTitles(), R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
                holder.setText(R.id.tv_title, item.name)
                holder.setTextSize(R.id.tv_title, 15f)
                val textview = holder.getView<TextView>(R.id.tv_title)
                textview.setPadding(Utils.dip2px(this@MortgageScoreActivity, 5f),
                        Utils.dip2px(this@MortgageScoreActivity, 10f),
                        Utils.dip2px(this@MortgageScoreActivity, 5f),
                        Utils.dip2px(this@MortgageScoreActivity, 10f))
                if (item.isClick) {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_home)
                    holder.setVisible(R.id.tv_line, true)
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_c6)
                    holder.setVisible(R.id.tv_line, false)
                }
                holder.convertView.setOnClickListener { refreshTitle(position) }
            }
        }
        rv_title.adapter = titleAdapter
    }

    //数据显示
    private fun initRVScore() {
        adapter = object : CommonAdapter<MortgageScore>(this@MortgageScoreActivity, ArrayList(), R.layout.item_integral) {
            override fun convert(holder: BaseViewHolder, score: MortgageScore, position: Int) {
                when (type) {
                    0 -> {
                        holder.setVisible(R.id.ll_score, true)
                        holder.setVisible(R.id.ll_remark, false)
                        holder.setVisible(R.id.ll_score_total, false)
                        holder.setText(R.id.tv_title_name, "总积分：")
                        holder.setText(R.id.tv_name, score.name)
                    }
                    1 -> {
                        holder.setVisible(R.id.ll_score, true)
                        holder.setVisible(R.id.ll_remark, true)
                        holder.setVisible(R.id.ll_score_total, false)
                        holder.setText(R.id.tv_remark, score.remark)
                        holder.setText(R.id.tv_title_name, " 评  分 ：")
                        holder.setText(R.id.tv_name, score.name)
                    }
                    2 -> {
                        holder.setVisible(R.id.ll_score, false)
                        holder.setVisible(R.id.ll_remark, false)
                        holder.setVisible(R.id.ll_score_total, true)
                        holder.setText(R.id.tv_title_name, " 均  分 ：")
                        holder.setText(R.id.num_1, "${score.oneStar}")
                        holder.setText(R.id.num_2, "${score.twoStar}")
                        holder.setText(R.id.num_3, "${score.threeStar}")
                        holder.setText(R.id.num_4, "${score.fourStar}")
                        holder.setText(R.id.num_5, "${score.fiveStar}")
                        holder.setText(R.id.tv_name, Html.fromHtml(score.name))
                    }
                }
                holder.setText(R.id.tv_time, score.updateTime ?: "")
                holder.setText(R.id.tv_integral_type, score.scoreDescribe)
                holder.convertView.setOnClickListener {
                    if (type == 0) {
                        val intent1 = Intent(this@MortgageScoreActivity, IntegralDetailActivity::class.java)
                        intent1.putExtra("MortgageName", score.name)
                        intent1.putExtra("UserID", score.userID)
                        intent1.putExtra("YearMonth", score.updateTime)
                        startActivity(intent1)
                    }
                }
            }
        }
        rv_score.addItemDecoration(1)
        rv_score.setAdapter(adapter)
        rv_score.setOnRefreshListener(object : PullToRefreshRecyclerView.OnRefreshListener {
            override fun onPullDownRefresh() {
                Handler().postDelayed({
                    pull = false
                    page = 0
                    mortgageId = ""
                    setMortgageId(mortgageId)
                    setDateView("", "")
                    presenter.getScoreData(mortgageId, date, date1, page, type)
                }, 1000)
            }

            override fun onLoadMore() {
                Handler().postDelayed({
                    page++
                    pull = true
                    presenter.getScoreData(mortgageId, date, date1, page, type)
                }, 1000)
            }
        })
    }

    private fun refreshTitle(position: Int) {
        presenter.setRemarkType(0)
        type = position
        if (type == 1) right.visibility = View.VISIBLE else right.visibility = View.GONE
        pull = false
        page = 0
        mortgageId = ""
        setMortgageId(mortgageId)
        setDateView("", "")
        for (i in 0 until titleAdapter?.datas?.size!!) {
            (titleAdapter?.datas!![i] as CommonItem<*>).isClick = i == position
        }
        titleAdapter?.notifyDataSetChanged()
        presenter.getScoreData(mortgageId, date, date1, page, type)
    }

    override fun refreshData() {
        if (scores.isEmpty()) {
            rl_nodata.visibility = View.VISIBLE
            rv_score.visibility = View.GONE
        } else {
            rl_nodata.visibility = View.GONE
            rv_score.visibility = View.VISIBLE
        }
        adapter?.setNewData(scores)
    }

    override fun completeRefresh() {
        if (pull) {
            rv_score.completeLoadMore()
        } else {
            rv_score.completeRefresh()
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            right -> presenter.showRemark(right)
            tv_name -> {
                if (Hawk.get<String>("UserType") != "22") {
                    val intent1 = Intent(this@MortgageScoreActivity, SearchActivity::class.java)
                    intent1.putExtra("type", Constants.MORTGAGE_CODE)
                    intent1.putExtra("hintContent", "搜索按揭员")
                    startActivity(intent1)
                }
            }
            ll_date -> presenter.showTime(toolbarTitle)
            tv_date -> presenter.showTime(tv_date)
            iv_date -> if (type == 0) presenter.showTime(tv_date) else presenter.showTime(toolbarTitle)
        }
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }
}
