package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.orhanobut.hawk.Hawk

import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.ReportAdapter
import com.xxjr.cfs_system.LuDan.presenter.LoanFliterPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.FliterVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.ToastShow
import kotlinx.android.synthetic.main.activity_loan_over.*
import listener.OnTabSelectListener
import java.util.ArrayList

class LoanFliterActivity : BaseActivity<LoanFliterPresenter, LoanFliterActivity>(), FliterVInter, View.OnClickListener {
    var adapter: ReportAdapter? = null
    private var searchType = 0 //查询类型
    private var query2 = ""
    private var query3 = ""

    override fun getLayoutId(): Int = R.layout.activity_loan_over

    override fun getPresenter(): LoanFliterPresenter = LoanFliterPresenter()

    override fun getContractType(): Int = intent.getIntExtra("contractType", -1)

    override fun initView(savedInstanceState: Bundle?) {
        initEdit()
        initViewPager(presenter.getTitles())
        iv_back.setOnClickListener(this)
        tv_search_type.setOnClickListener(this)
        iv_date.setOnClickListener(this)
        presenter?.setDefaultValue()
    }

    private fun initEdit() {
        et_pact_search.setOnTouchListener { _, event ->
            //仅在按键弹起时执行
            if (searchType == 3 && event.action == MotionEvent.ACTION_UP) {
                val intent = Intent(this@LoanFliterActivity, SearchActivity::class.java)
                intent.putExtra("type", Constants.Company_Choose)
                intent.putExtra("hintContent", "搜索门店")
                startActivity(intent)
                return@setOnTouchListener true
            }
            false
        }
        et_pact_search.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            /*判断是否是“GO”键*/
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                /*隐藏软键盘*/
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive) {
                    imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
                }
                if (searchType != 1 && searchType != 2) {
                    searchType = 1
                }
                val builder = StringBuilder()
                when (searchType) {
                    1 -> builder.append(" and CustomerNames like '%").append(getETText()).append("%'")
                    2 -> builder.append(" and LoanCode like '%").append(getETText()).append("%'")
                }
                if (getContractType() == 5) {
                    getTitles(builder.toString(), vp_filter.currentItem)
                }
                adapter?.getItem(vp_filter.currentItem)?.changePageData(searchType, getETText(), query2, query3)
                refreshAll()
                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun isShowBacking(): Boolean = true

    private fun initViewPager(titles: MutableList<String>) {
        when (getContractType()) {
            5 -> adapter = ReportAdapter(supportFragmentManager, titles, 0)
            6 -> adapter = ReportAdapter(supportFragmentManager, titles, 3)
        }
        vp_filter.adapter = adapter
        vp_filter.offscreenPageLimit = titles.size
        tl_filter.setSnapOnTabClick(true)
        tl_filter.setViewPager(vp_filter, titles as ArrayList<String>)
        when (getContractType()) {
            5 -> {
                when (Hawk.get<Any>("UserType")) {
                    "3", "7" -> vp_filter.currentItem = 1
                    "16", "18", "20" -> vp_filter.currentItem = 2
                    else -> vp_filter.currentItem = 0
                }
            }
            6 -> vp_filter.currentItem = 0
        }
        tl_filter.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                vp_filter.currentItem = position
            }

            override fun onTabReselect(position: Int) {
            }
        })
        tl_filter.setOnPagerSlidingListener {
            tl_filter.currentTab
            if (adapter?.getItem(it)?.getIsFirst() != false) {
                adapter?.getItem(vp_filter.currentItem)?.changePageData(searchType, getETText(), query2, query3)
            }
        }
    }

    fun getTitles(query: String, type: Int) {
        presenter.getTitlesData(query, type)
    }

    fun cleanEdit() {
        tv_search_type.text = "客户名"
        et_pact_search.setText("")
        searchType = 0
        query2 = ""
        query3 = ""
    }

    private fun getETText(): String = et_pact_search.text.toString().trim()

    override fun refreshTitle(titles: MutableList<String>) {
        tl_filter.refreshTitle(titles as ArrayList<String>, vp_filter.currentItem)
    }

    fun getTitles() = tl_filter.titles

    override fun onClick(p0: View?) {
        when (p0) {
            iv_back -> finish()
            tv_search_type -> presenter.popChoose(tv_search_type)
            iv_date -> presenter.showTime(iv_date)
        }
    }

    override fun customerClick() {
        searchType = 1
        tv_search_type.text = "客户名"
        et_pact_search.setText("")
    }

    override fun loanCodeClick() {
        searchType = 2
        if (getContractType() == 6) {
            tv_search_type.text = "申请人"
        } else {
            tv_search_type.text = "贷款号"
        }
        et_pact_search.setText("")
    }

    override fun companyClick() {
        searchType = 3
        tv_search_type.text = "门店名"
        et_pact_search.setText("")
    }

    override fun setCompanyName(id: String, name: String) {
        et_pact_search.setText(name)
        val builder = StringBuilder()
        if (id.isNotBlank()) builder.append(" and CompanyID in ('").append(id).append("')")
        if (getContractType() == 5) {
            getTitles(builder.toString(), vp_filter.currentItem)
        }
        adapter?.getItem(vp_filter.currentItem)?.changePageData(searchType, id, query2, query3)
        refreshAll()
    }

    override fun timeClick(time1: String, time2: String) {
        query2 = time1
        query3 = time2
        if (getContractType() == 5) {
            val builder = " and CONVERT(varchar(100),UpdateTime, 23) >= '$time1'" +
                    " and CONVERT(varchar(100),UpdateTime, 23) <= '$time2'"
            getTitles(builder, vp_filter.currentItem)
        }
        adapter?.getItem(vp_filter.currentItem)?.changePageData(searchType, getETText(), query2, query3)
        refreshAll()
    }

    fun refreshAll() {
        for (i in 0..(adapter?.count!! - 1)) {
            if (i != vp_filter.currentItem) {
                adapter?.getItem(i)?.setIsFirst(true)
            }
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
