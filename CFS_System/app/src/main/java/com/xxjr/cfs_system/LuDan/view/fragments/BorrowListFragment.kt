package com.xxjr.cfs_system.LuDan.view.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.LoanFliterActivity
import com.xxjr.cfs_system.LuDan.view.activitys.borrow.presenter.ListPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.borrow.view.DetailActivity
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.BorrowDetail
import kotlinx.android.synthetic.main.fragment_filter.*
import refresh_recyclerview.PullToRefreshRecyclerView
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import java.math.BigDecimal
import java.util.ArrayList

/**
 * Created by Administrator on 2017/10/27.
 * 拆借列表
 */
class BorrowListFragment : BaseFragment(), BaseViewInter {
    private var presenter = ListPresenter()
    private lateinit var adapter: CommonAdapter<BorrowDetail>

    val borrowLists = ArrayList<BorrowDetail>()//拆借列表
    var isPull = false //false->下拉
    var state = 0 //状态
    var page = 0//分页
    var searchType = 0 //搜索类型
    private var query1 = ""
    private var query2 = ""
    private var query3 = ""

    var titles = arrayListOf<String>()

    override fun setIsFirst(isFirst: Boolean) {
        firstUp = isFirst
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(activity.applicationContext, msg)

    override fun initView(inflater: LayoutInflater, savedInstanceState: Bundle?, arguments: Bundle): View {
        presenter.attach(this)
        state = arguments.getInt("position")
        return inflater.inflate(R.layout.fragment_filter, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        titles.addAll((activity as LoanFliterActivity).getTitles())
        initRecycler()
        if (state == 0) {
            presenter.getBorrowItemData(searchType, query1, query2, query3)
        }
    }

    fun initRecycler() {
        adapter = object : CommonAdapter<BorrowDetail>(context, arrayListOf(), R.layout.item_borrow_list) {
            override fun convert(holder: BaseViewHolder, borrowInfo: BorrowDetail, position: Int) {
                holder.setVisible(R.id.tv_code_title, !borrowInfo.Code.isNullOrBlank())
                holder.setVisible(R.id.tv_code, !borrowInfo.Code.isNullOrBlank())
                holder.setVisible(R.id.v_blank, !borrowInfo.Code.isNullOrBlank())
                holder.setText(R.id.tv_code, borrowInfo.Code ?: "")
                holder.setText(R.id.tv_customer, borrowInfo.CustomerName)
                holder.setText(R.id.tv_apply_date, Utils.FormatTime(borrowInfo.StartDate, "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd"))
                holder.setText(R.id.tv_loan_used, when (borrowInfo.FunctionType) {
                    "11" -> "进货周转"
                    "12" -> "店铺装修"
                    "13" -> "扩大经营"
                    "14" -> "日常消费"
                    "15" -> "其他"
                    else -> ""
                })
                holder.setText(R.id.tv_amount, "${Utils.parseMoney(BigDecimal(borrowInfo.ApplyAmount))} 元")
                holder.setText(R.id.tv_deadline, "${borrowInfo.ApplyBackTime}月")
                holder.setText(R.id.tv_apply_people, borrowInfo.ApplicantName ?: "")
                holder.setText(R.id.tv_borrow_state, when (borrowInfo.State) {
                    -99 -> "未提交"
                    -1 -> "资料不全"//-1
                    1 -> "已提交"//1
                    2 -> "已进件待授信"//2
                    3 -> "已授信待审批"//3
                    4 -> "审批中"//4
                    5 -> "已拆借待放款"//5
                    6 -> "已放款"//6
                    -2 -> "授信审核未通过"//-2
                    -3 -> "审批未通过"//-3
                    else -> "未指定"
                })
                holder.convertView.setOnClickListener {
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("borrowInfo", borrowInfo)
                    startActivityForResult(intent, 99)
                }
            }
        }
        recycler_filter.addItemDecoration(8)
        recycler_filter.setAdapter(adapter)
        recycler_filter.completeLoadMore()
        recycler_filter.setOnRefreshListener(object : PullToRefreshRecyclerView.OnRefreshListener {
            override fun onPullDownRefresh() {
                isPull = false
                page = 0
                searchType = 0
                query1 = ""
                query2 = ""
                query3 = ""
                presenter.getBorrowItemData(searchType, query1, query2, query3)
                (activity as LoanFliterActivity).cleanEdit()
                (activity as LoanFliterActivity).refreshAll()
            }

            override fun onLoadMore() {
                isPull = true
                page++
                presenter.getBorrowItemData(searchType, query1, query2, query3)
            }
        })
    }

    fun refreshChange() {
        adapter.setNewData(borrowLists)
    }

    fun completeRefresh() {
        if (isPull) {
            recycler_filter.completeLoadMore()
        } else {
            recycler_filter.completeRefresh()
        }
    }

    fun refreshBorrowTitle() {
        (activity as LoanFliterActivity).refreshTitle(titles)
    }

    override fun changePageData(searchType: Int, query1: String, query2: String, query3: String) {
        isPull = false
        page = 0
        this.searchType = searchType
        this.query1 = query1
        this.query2 = query2
        this.query3 = query3
        presenter.getBorrowItemData(searchType, query1, query2, query3)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.getBorrowItemData(searchType, query1, query2, query3)
        (activity as LoanFliterActivity).refreshAll()
    }
}
