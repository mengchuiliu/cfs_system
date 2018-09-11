package com.xxjr.cfs_system.LuDan.view.activitys.withdraw_wallet

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.alibaba.fastjson.JSONArray
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.presenter.WagePresenter
import com.xxjr.cfs_system.LuDan.view.activitys.SearchActivity
import com.xxjr.cfs_system.LuDan.view.viewinter.WageVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.Utils
import entity.WageDetail
import kotlinx.android.synthetic.main.activity_wage_confirm.*
import refresh_recyclerview.PullToRefreshRecyclerView
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import java.util.ArrayList

class WageConfirmActivity : BaseActivity<WagePresenter, WageVInter>(), WageVInter, View.OnClickListener {
    private var wages = mutableListOf<WageDetail>()
    private var adapter: CommonAdapter<WageDetail>? = null
    private var titleAdapter: CommonAdapter<CommonItem<*>>? = null
    private var pull = false
    private var refuseId = 0
    private var page = 0
    private var state = 1
    private var date: String = ""
    private var date1: String = ""
    private var mortgage: String = ""
    private var isMortgage = false

    override fun getPresenter(): WagePresenter = WagePresenter()

    override fun getWageDetails(): MutableList<WageDetail> = wages

    override fun getPull(): Boolean = pull

    override fun getLayoutId(): Int = R.layout.activity_wage_confirm

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "提成列表"
        if (Hawk.get<String>("UserType") == "22") {
            isMortgage = true
            mortgage = Hawk.get<String>("UserID")
        }
        rl_botom_over.setOnClickListener(this)
        tv_submit.setOnClickListener(this)
        tv_name.setOnClickListener(this)
        ll_date.setOnClickListener(this)
        iv_date.setOnClickListener(this)
        setMortgage(mortgage)
        setDate(date, date1)
        initTitle()
        initRvWageDetails()
        presenter.setDefaultValue()
        presenter.getWageDetails(mortgage, date, date1, "$state", page)
    }

    override fun setMortgage(mortgage: String) {
        pull = false
        this.mortgage = mortgage
        if (isMortgage) {
            tv_name.text = Hawk.get<String>("UserRealName")
        } else {
            if (mortgage.isBlank()) {
                tv_name.text = "全部"
            } else {
                tv_name.text = CacheProvide.getMortgageName(mortgage.toInt())
            }
        }
    }

    override fun setDate(date: String, date1: String) {
        pull = false
        this.date = date
        this.date1 = date1
        if (date.isBlank() && date1.isBlank()) {
            tv_date1.text = "开始时间：无"
            tv_date2.text = "结束时间：无"
        } else {
            tv_date1.text = "开始时间：${Utils.FormatTime(date, "yyyy-MM-dd", "yyyy/MM/dd")}"
            tv_date2.text = "结束时间：${Utils.FormatTime(date1, "yyyy-MM-dd", "yyyy/MM/dd")}"
        }
    }

    private fun initTitle() {
        rv_title.layoutManager = LinearLayoutManager(this@WageConfirmActivity, LinearLayoutManager.HORIZONTAL, false)
        titleAdapter = object : CommonAdapter<CommonItem<*>>(this@WageConfirmActivity, presenter.getTitles(), R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
                holder.setText(R.id.tv_title, item.name)
                holder.setTextSize(R.id.tv_title, 15f)
                val textview = holder.getView<TextView>(R.id.tv_title)
                textview.setPadding(Utils.dip2px(this@WageConfirmActivity, 5f),
                        Utils.dip2px(this@WageConfirmActivity, 10f),
                        Utils.dip2px(this@WageConfirmActivity, 5f),
                        Utils.dip2px(this@WageConfirmActivity, 10f))
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

    override fun initRvWageDetails() {
        val jsonArrayLoan = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoansType"), ""))
        val jsonArray = JSONArray.parseArray(Hawk.get(CacheProvide.getCacheKey("LoanProductType"), ""))
        adapter = object : CommonAdapter<WageDetail>(this@WageConfirmActivity, ArrayList(), R.layout.item_wage_details) {
            override fun convert(holder: BaseViewHolder, wage: WageDetail, position: Int) {
                holder.setText(R.id.tv_loan_code, "${wage.loanCode ?: ""}-${wage.customerName
                        ?: ""}")
                holder.setText(R.id.tv_loan_detail, "【${CacheProvide.getLoanType(jsonArrayLoan, wage.loanType
                        ?: 0)}-${CacheProvide.getProduct(jsonArray, wage.product ?: 0)}】")
                holder.setText(R.id.tv_leng_money, "放款金额：${Utils.div((wage.lendAmount
                        ?: "0.0").toDouble())}万")
//                holder.setText(R.id.tv_date, Utils.getTime(wage.lendDate ?: ""))
                holder.setText(R.id.tv_commission, "提成金额：${wage.calcCommission}元")
                holder.setText(R.id.tv_wage, getReadText(5, "实际发放：${wage.commission}元"))
                holder.setText(R.id.tv_remark, "备注：${wage.remark}")
                if (wage.userFeedBack.isNullOrBlank()) {
                    holder.setVisible(R.id.tv_user_remark, false)
                } else {
                    holder.setVisible(R.id.tv_user_remark, true)
                    holder.setText(R.id.tv_user_remark, "员工异议：${wage.userFeedBack}")
                }
                if (wage.financialFeedBack.isNullOrBlank()) {
                    holder.setVisible(R.id.tv_finance_remark, false)
                } else {
                    holder.setVisible(R.id.tv_finance_remark, true)
                    holder.setText(R.id.tv_finance_remark, "财务异议：${wage.financialFeedBack}")
                }
                holder.setVisible(R.id.ll_confirm, wage.state == "1")
                holder.setVisible(R.id.tv_confirmedState, wage.state == "1" && wage.confirmedState != 1)
                holder.getView<TextView>(R.id.tv_confirm).isEnabled = wage.confirmedState == 1
                holder.getView<TextView>(R.id.tv_refuse).isEnabled = wage.confirmedState == 1
                if (wage.confirmedState != 1) {
                    holder.setTextColorRes(R.id.tv_confirm, R.color.font_c9)
                    holder.setTextColorRes(R.id.tv_refuse, R.color.font_c9)
                } else {
                    holder.setTextColorRes(R.id.tv_confirm, R.color.font_home)
                    holder.setTextColorRes(R.id.tv_refuse, R.color.font_home)
                }
                holder.setOnClickListener(R.id.tv_confirm) {
                    if (wage.confirmedState == 1) {
                        CustomDialog.showTwoButtonDialog(this@WageConfirmActivity, "确定该笔提成无异议？", "确定", "取消") { dialogInterface, i ->
                            dialogInterface.dismiss()
                            pull = false
                            presenter.confirmWage(wage.wageId ?: 0)
                        }
                    }
                }
                holder.setOnClickListener(R.id.tv_refuse) {
                    if (wage.confirmedState == 1) {
                        rl_botom_over.visibility = View.VISIBLE
                        showSoftInput(et_over_remark)
                        refuseId = wage.wageId ?: 0
                    }
                }
            }
        }
        rv_wage_details.setAdapter(adapter)

        rv_wage_details.setOnRefreshListener(object : PullToRefreshRecyclerView.OnRefreshListener {
            override fun onPullDownRefresh() {
                Handler().postDelayed({
                    pull = false
                    if (isMortgage) {
                        mortgage = Hawk.get<String>("UserID")
                    } else {
                        mortgage = ""
                    }
                    setMortgage(mortgage)
                    setDate("", "")
                    page = 0
                    presenter.getWageDetails(mortgage, date, date1, "$state", page)
                    completeRefresh()
                }, 500)
            }

            override fun onLoadMore() {
                Handler().postDelayed({
                    page++
                    pull = true
                    presenter.getWageDetails(mortgage, date, date1, "$state", page)
                    completeRefresh()
                }, 500)
            }
        })
    }

    override fun completeRefresh() {
        if (pull) {
            rv_wage_details.completeLoadMore()
        } else {
            rv_wage_details.completeRefresh()
        }
    }

    override fun refreshData() {
        if (wages.isEmpty()) {
            rl_nodata.visibility = View.VISIBLE
            rv_wage_details.visibility = View.GONE
        } else {
            rl_nodata.visibility = View.GONE
            rv_wage_details.visibility = View.VISIBLE
        }
        adapter?.setNewData(wages)
        adapter?.notifyDataSetChanged()
    }

    private fun refreshTitle(position: Int) {
        state = position + 1
        for (i in 0 until titleAdapter?.datas?.size!!) {
            (titleAdapter?.datas!![i] as CommonItem<*>).isClick = i == position
        }
        titleAdapter?.notifyDataSetChanged()
        pull = false
        presenter.getWageDetails(mortgage, date, date1, "$state", 0)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            rl_botom_over -> {
                hideSoftInput(p0!!)
                rl_botom_over.visibility = View.GONE
            }
            tv_submit -> {//提交
                hideSoftInput(p0!!)
                rl_botom_over.visibility = View.GONE
                if (getEdRemark().isBlank()) {
                    showMsg("拒绝理由不能为空!")
                } else {
                    pull = false
                    presenter.refuseWage(refuseId, getEdRemark())
                }
            }
            tv_name -> {
                if (!isMortgage) {
                    val intent1 = Intent(this@WageConfirmActivity, SearchActivity::class.java)
                    intent1.putExtra("type", Constants.MORTGAGE_CODE)
                    intent1.putExtra("hintContent", "搜索按揭员")
                    startActivity(intent1)
                }
            }
            ll_date, iv_date -> {
                presenter.showTime(toolbarTitle)
            }
        }
    }

    private fun hideSoftInput(v: View) {
        val imm = this@WageConfirmActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun showSoftInput(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        this@WageConfirmActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    override fun getEdRemark(): String = et_over_remark.text.toString().trim()

    override fun isShowBacking(): Boolean = true

    override fun showMsg(msg: String?) = ToastShow.showShort(application, msg)

    private fun getReadText(start: Int, text: String): CharSequence {
        val spannableString = SpannableString(text)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#FF4040")), start, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (rl_botom_over.visibility == View.VISIBLE) {
                hideSoftInput(toolbarTitle)
                rl_botom_over.visibility = View.GONE
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }
//    //设置数据
//    override fun setPieData(wageDetail: WageDetail?) {
//        tv_name.text = if (wageDetail == null) Hawk.get<String>("UserRealName") else wageDetail.mortgageName ?: Hawk.get<String>("UserRealName")
//        tv_wage_amount.text = if (wageDetail == null) "0" else wageDetail.totalWages ?: "0"
//        val entries = arrayListOf<PieEntry>()
//        entries.add(PieEntry(if (wageDetail == null) 0f else wageDetail.basicWages?.toFloat() ?: 0f, "基本工资  ${wageDetail?.basicWages ?: ""}"))
//        entries.add(PieEntry(if (wageDetail == null) 0f else wageDetail.businessCommission?.toFloat() ?: 0f, "业务提成  ${wageDetail?.businessCommission ?: ""}"))
//        entries.add(PieEntry(if (wageDetail == null) 0f else wageDetail.fullWages?.toFloat() ?: 0f, "全勤奖金  ${wageDetail?.fullWages ?: ""}"))
//        entries.add(PieEntry(if (wageDetail == null) 0f else wageDetail.meritWages?.toFloat() ?: 0f, "绩效工资  ${wageDetail?.meritWages ?: ""}"))
//        entries.add(PieEntry(if (wageDetail == null) 1f else wageDetail.otherRevenue?.toFloat() ?: 0f, "其他收入  ${wageDetail?.otherRevenue ?: ""}"))
//        val dataSet = PieDataSet(entries, "")
//        //数据和颜色
//        val colors = ArrayList<Int>()
//        colors.add(resources.getColor(R.color.wage_1))
//        colors.add(resources.getColor(R.color.wage_2))
//        colors.add(resources.getColor(R.color.wage_3))
//        colors.add(resources.getColor(R.color.wage_4))
//        colors.add(resources.getColor(R.color.wage_5))
//        dataSet.colors = colors
//        dataSet.setDrawValues(false)
//        val data = PieData(dataSet)
//        data.setValueFormatter(PercentFormatter())
//        data.setValueTextSize(10f)
//        data.setValueTextColor(Color.WHITE)
//        mPieChart.data = data
//        mPieChart.highlightValues(null)
//        //刷新
//        mPieChart.invalidate()
//    }

//    private fun initPie() {
//        mPieChart.setUsePercentValues(false)//设置百分比
//        mPieChart.setDrawEntryLabels(false)//设置隐藏饼图上文字
//        mPieChart.isDrawHoleEnabled = true //是否空心
//        mPieChart.rotationAngle = 0f // 初始旋转角度
//        mPieChart.holeRadius = 50f
//        mPieChart.transparentCircleRadius = 0f
//        mPieChart.isRotationEnabled = false //可手动旋转
//        mPieChart.extraLeftOffset = 80f
//        mPieChart.extraBottomOffset = 10f
//        val description = Description()
//        description.text = ""
//        mPieChart.description = description
//        setPieData(null)
//        val mLegend = mPieChart.legend  //设置比例图
//        mLegend.position = LegendPosition.LEFT_OF_CHART_INSIDE  //最右边显示
////      mLegend.setForm(LegendForm.LINE);  //设置比例图的形状，默认是方形
//        mLegend.xEntrySpace = 0f
//        mLegend.yEntrySpace = 5f
//        mLegend.formSize = 15f
//        mLegend.yOffset = 8f
//        mLegend.xOffset = 15f
//        mLegend.formToTextSpace = 5f
//        mLegend.textSize = 13f
//        mLegend.textColor = resources.getColor(R.color.font_c9)
//        mPieChart.animateXY(1000, 1000)  //设置动画
//    }
}
