package com.xxjr.cfs_system.LuDan.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.SwipeMenuLayout
import com.xxjr.cfs_system.LuDan.presenter.LoanOverPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.*
import com.xxjr.cfs_system.LuDan.view.viewinter.LoanOverVIntrer
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.LoanInfo
import kotlinx.android.synthetic.main.fragment_filter.*
import refresh_recyclerview.PullToRefreshRecyclerView
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import java.util.ArrayList

/**
 * Created by Administrator on 2017/10/9.
 * 贷款结案
 */
class LoanOverFragment : BaseFragment(), LoanOverVIntrer {
    private var presenter = LoanOverPresenter(this)
    private var adapter: CommonAdapter<LoanInfo>? = null

    private val loanInfos = ArrayList<LoanInfo>()//贷款列表
    private var type = 0
    private var page = 0
    private var searchType = 0
    private var isPull = false
    private var query1 = ""
    private var query2 = ""
    private var query3 = ""

    override fun getFrgContext(): Context = context

    override fun getType(): Int = type

    override fun getPage(): Int = page

    override fun setIsFirst(isFirst: Boolean) {
        firstUp = isFirst
    }

    override fun getPull(): Boolean = isPull

    override fun getLoanInfos(): MutableList<LoanInfo> = loanInfos

    override fun initView(inflater: LayoutInflater, savedInstanceState: Bundle?, arguments: Bundle): View {
        type = arguments.getInt("position")
        return inflater.inflate(R.layout.fragment_filter, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecycler()
        when (Hawk.get<Any>("UserType")) {
            "3", "7" -> {
                if (type == 1) {
                    presenter.getData(searchType, query1, query2, query3)
                }
            }
            "16", "18", "20" -> {
                if (type == 2) {
                    presenter.getData(searchType, query1, query2, query3)
                }
            }
            else -> {
                if (type == 0) {
                    presenter.getData(searchType, query1, query2, query3)
                }
            }
        }
    }

    override fun initRecycler() {
        adapter = object : CommonAdapter<LoanInfo>(context, ArrayList(), R.layout.item_loan) {
            override fun convert(holder: BaseViewHolder, loanInfo: LoanInfo, position: Int) {
                val swipeMenuLayout = (holder.convertView as SwipeMenuLayout).setIos(false).setLeftSwipe(true)
                swipeMenuLayout.isSwipeEnable = false
                holder.setVisible(R.id.ll_cost, false)
                when (type) {
                    0 -> {
                        when (loanInfo.scheduleId) {
                            5, 109, -3, -4, -5 -> holder.setVisible(R.id.ll_is_case, true)
                            else -> holder.setVisible(R.id.ll_is_case, false)
                        }
                    }
                    1 -> holder.setVisible(R.id.ll_is_case, true)
                }
                holder.setText(R.id.tv_loan_numb, loanInfo.loanCode)
                holder.setText(R.id.tv_date, if (loanInfo.updateTime.contains("T"))
                    loanInfo.updateTime.substring(0, loanInfo.updateTime.indexOf("T"))
                else
                    loanInfo.updateTime)
                holder.setText(R.id.tv_customer, loanInfo.customer)
                val description = "【" + loanInfo.bankName + "·" + loanInfo.productName +
                        "(" + (if (TextUtils.isEmpty(loanInfo.loanTypeName)) "" else loanInfo.loanTypeName.substring(0, 1)) + ")" + "】" +
                        "申请: " + Utils.div(loanInfo.amount) + "万"
                holder.setText(R.id.tv_loan_content, description)
                holder.setText(R.id.tv_schedule, loanInfo.schedule)
                holder.setText(R.id.tv_case, if (loanInfo.isCase == 1) "是" else "否")

                holder.setOnClickListener(R.id.ll_home) {
                    val intent = Intent()
                    intent.setClass(activity, OverApplyActivity::class.java)
                    intent.putExtra("loanInfo", loanInfo)
                    intent.putExtra("position", type)
                    startActivityForResult(intent, type)
                }
            }
        }

        recycler_filter.addItemDecoration(1)
        recycler_filter.setAdapter(adapter)
        recycler_filter.setOnRefreshListener(object : PullToRefreshRecyclerView.OnRefreshListener {
            override fun onPullDownRefresh() {
                isPull = false
                page = 0
                searchType = 0
                query1 = ""
                query2 = ""
                query3 = ""
                (activity as LoanFliterActivity).getTitles("", type)
                presenter.getData(searchType, query1, query2, query3)
                (activity as LoanFliterActivity).cleanEdit()
                (activity as LoanFliterActivity).refreshAll()
            }

            override fun onLoadMore() {
                isPull = true
                page++
                presenter.getData(searchType, query1, query2, query3)
            }
        })
    }

    override fun onStart() {
        super.onStart()
//        Handler().postDelayed({recycler_filter.completeLoadMore()}, 1000)
    }

    override fun refreshChange() {
        adapter?.setNewData(loanInfos)
    }

    override fun completeRefresh() {
        if (isPull) {
            recycler_filter.completeLoadMore()
        } else {
            recycler_filter.completeRefresh()
        }
    }

    override fun changePageData(searchType: Int, query1: String, query2: String, query3: String) {
        isPull = false
        page = 0
        this.searchType = searchType
        this.query1 = query1
        this.query2 = query2
        this.query3 = query3
        presenter.getData(searchType, query1, query2, query3)
    }

//    override fun getETName(): String = query1

    override fun showMsg(msg: String?) = ToastShow.showShort(activity.applicationContext, msg)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        (activity as LoanFliterActivity).getTitles("", type)
        presenter.getData(searchType, query1, query2, query3)
        (activity as LoanFliterActivity).refreshAll()
    }
}