package com.xxjr.cfs_system.LuDan.view.activitys.lending_list

import android.content.Intent
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xiaoxiao.widgets.SwipeMenuLayout
import com.xxjr.cfs_system.LuDan.presenter.LendingListPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.BaseListActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.CommonItem
import entity.LoanInfo
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import rx.Subscription

class LendingListActivity : BaseListActivity<LendingListPresenter, LendingListActivity>() {
    private var companySubscription: Subscription? = null//门店
    var type = 0//0->未确认放款  1->有异议 2->已确认
    var loanInfos = mutableListOf<LoanInfo>()//贷款列表

    override fun getListPresenter(): LendingListPresenter = LendingListPresenter()

    override fun initAdapter() {
        companySubscription = RxBus.getInstance().toObservable(Constants.Company_Choose, ChooseType::class.java)
                .subscribe { chooseType ->
                    searchCompanyId = chooseType.ids
                    etPactSearch.setText(chooseType.content)
                    page = 0
                    isPull = false
                    refreshData(page, searchType)
                }
        type = intent.getIntExtra("Type", 0)
        adapter = object : CommonAdapter<LoanInfo>(this@LendingListActivity, arrayListOf(), R.layout.item_loan) {
            override fun convert(holder: BaseViewHolder, loanInfo: LoanInfo, position: Int) {
                val swipeMenuLayout = (holder.convertView as SwipeMenuLayout).setIos(false).setLeftSwipe(true)
                swipeMenuLayout.isSwipeEnable = false
                holder.setText(R.id.tv_loan_numb, loanInfo.loanCode)
                holder.setText(R.id.tv_date, loanInfo.updateTime)
                holder.setText(R.id.tv_customer, loanInfo.customer)
                holder.setText(R.id.tv_loan_content, "${loanInfo.loanDescription}申请${Utils.div(loanInfo.amount)}万")
                holder.setText(R.id.tv_title_1, "所属门店：")
                holder.setText(R.id.tv_cost, loanInfo.companyID)
                holder.setText(R.id.tv_title_2, "还款方式：")
                holder.setText(R.id.tv_schedule, loanInfo.paymentName)
                holder.setTextColorRes(R.id.tv_schedule, R.color.font_c3)
                holder.setVisible(R.id.ll_is_case, true)
                holder.setTextColorRes(R.id.tv_score, R.color.font_c6)
                holder.setText(R.id.tv_score, "确认状态：")
                when (loanInfo.scheduleId) {
                    -2 -> {
                        holder.setText(R.id.tv_case, "已修改")
                        holder.setTextColorRes(R.id.tv_case, R.color.detail1)
                    }
                    -1 -> {
                        holder.setText(R.id.tv_case, "有异议")
                        holder.setTextColorRes(R.id.tv_case, R.color.detail1)
                    }
                    1 -> {
                        holder.setText(R.id.tv_case, "已确认")
                        holder.setTextColorRes(R.id.tv_case, R.color.detail2)
                    }
                    else -> {
                        holder.setText(R.id.tv_case, "未确认")
                        holder.setTextColorRes(R.id.tv_case, R.color.detail3)
                    }
                }
                holder.setVisible(R.id.ll_remark, !loanInfo.disagreeReason.isNullOrBlank())
                holder.setText(R.id.tv_remark, loanInfo.disagreeReason ?: "")
                holder.setOnClickListener(R.id.ll_home) {
                    val intent = Intent(this@LendingListActivity, LendingDetailActivity::class.java)
                    intent.putExtra("LoanInfo", loanInfo)
                    startActivityForResult(intent, 99)
                }
            }
        }

        adapter0 = object : CommonAdapter<CommonItem<Any>>(this@LendingListActivity, presenter.getLendingTitles(), R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.convertView.layoutParams.width = (Utils.getScreenWidth(this@LendingListActivity)
                        - this@LendingListActivity.resources.getDimensionPixelSize(R.dimen.size_12) * 3) / 3
                holder.setText(R.id.tv_title, item.name)
                holder.setTextSize(R.id.tv_title, 15f)
                val textview = holder.getView<TextView>(R.id.tv_title)
                textview.setPadding(Utils.dip2px(this@LendingListActivity, 15f),
                        Utils.dip2px(this@LendingListActivity, 10f),
                        Utils.dip2px(this@LendingListActivity, 15f),
                        Utils.dip2px(this@LendingListActivity, 10f))
                if (item.isClick) {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_home)
                    holder.setVisible(R.id.tv_line, true)
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_c6)
                    holder.setVisible(R.id.tv_line, false)
                }
                holder.convertView.setOnClickListener { refreshTitle0(position) }
            }
        }
    }

    private fun refreshTitle0(position: Int) {
        type = position
        isPull = false
        page = 0
        for (i in 0 until adapter0.datas.size) {
            (adapter0.datas[i] as CommonItem<*>).isClick = i == position
        }
        adapter0.notifyDataSetChanged()
        presenter.getLendingListData(page, searchType)
    }

    override fun refreshData(page: Int, searchType: Int) {
        presenter.getLendingListData(page, searchType)
    }

    override fun refreshChange() {
        adapter.setNewData(loanInfos)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 99) {
            isPull = false
            page = 0
            presenter.getLendingListData(page, searchType)
        }
    }

    override fun onDestroy() {
        if (companySubscription != null && companySubscription!!.isUnsubscribed) {
            companySubscription!!.unsubscribe()
        }
        super.onDestroy()
    }
}
