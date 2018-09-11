package com.xxjr.cfs_system.LuDan.view.activitys.transaction_record

import android.content.Intent
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.view.activitys.BaseListActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.TransactionRecord
import me.kareluo.ui.OptionMenu
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter
import rx.Subscription

class TransactionListActivity : BaseListActivity<TransactionPresenter, TransactionListActivity>() {
    private var companySubscription: Subscription? = null//门店
    var transactionRecords = mutableListOf<TransactionRecord>()

    override fun getListPresenter(): TransactionPresenter = TransactionPresenter()

    override fun initAdapter() {
        companySubscription = RxBus.getInstance().toObservable(Constants.Company_Choose, ChooseType::class.java)
                .subscribe { chooseType ->
                    searchCompanyId = chooseType.ids
                    etPactSearch.setText(chooseType.content)
                    page = 0
                    isPull = false
                    refreshData(page, searchType)
                }
        tvSearchType.text = "流水号"
        adapter = object : CommonAdapter<TransactionRecord>(this@TransactionListActivity, arrayListOf(), R.layout.item_transaction_list) {
            override fun convert(holder: BaseViewHolder, record: TransactionRecord, position: Int) {
                holder.setText(R.id.tv_number, record.TransSn)
                holder.setText(R.id.tv_date, Utils.FormatTime(record.HappenTime, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm"))
                holder.setText(R.id.tv_company, record.ApplyCompanyName)
                holder.setText(R.id.tv_amount, "${String.format("%,.2f", Utils.div(record.Amount, 100.0, 2))}元")
                holder.setText(R.id.tv_state, when (record.State) {
                    1 -> "交易申请中"
                    2 -> "交易申请失败"
                    3 -> "交易中"
                    4 -> "交易成功"
                    5 -> "交易失败"
                    else -> "未指定"
                })
                holder.setTextColorRes(R.id.tv_state, when (record.State) {
                    1 -> R.color.detail3
                    2 -> R.color.detail1
                    3 -> R.color.font_home
                    4 -> R.color.detail2
                    5 -> R.color.detail1
                    else -> R.color.detail3
                })
                holder.setText(R.id.tv_type, when (record.TransType) {
                    1 -> "成本报销"
                    2 -> "按揭提成"
                    3 -> "业务员提成"
                    4 -> "退定金"
                    5 -> "退回款"
                    6 -> "拆借"
                    7 -> "固定支出"
                    8 -> "回款"
                    else -> "未指定"
                })
                holder.setText(R.id.tv_operator, record.OperatorName)
                val sb = StringBuilder()
                sb.append(record.Remark ?: "")
                if (record.State == 2 || record.State == 5) {
                    if ((record.FailReason ?: "").isNotBlank()) {
                        sb.append("  失败原因：").append(record.FailReason ?: "")
                    }
                }
                holder.setVisible(R.id.tv_remark_title, sb.isNotBlank())
                holder.setVisible(R.id.tv_remark, sb.isNotBlank())
                if (sb.isNotBlank()) holder.setText(R.id.tv_remark, sb.toString())
                holder.convertView.setOnClickListener {
                    val intent = Intent(this@TransactionListActivity, TransactionDetailActivity::class.java)
                    intent.putExtra("TransactionRecord", record)
                    startActivity(intent)
                }
            }
        }
    }

    override fun getPopMenuList(): MutableList<OptionMenu> = mutableListOf<OptionMenu>().apply {
        for (i in 1..4) {
            add(OptionMenu().apply {
                id = i
                title = when (i) {
                    1 -> "流水号"
                    2 -> "操作人"
                    3 -> "门店名"
                    4 -> "账户名"
                    else -> ""
                }
            })
        }
    }

    override fun optionMenusClick(menu: OptionMenu) {
        searchType = menu.id
        etPactSearch.setText("")
        when (menu.id) {
            1 -> tvSearchType.text = "流水号"
            2 -> tvSearchType.text = "操作人"
            3 -> tvSearchType.text = "门店名"
            4 -> tvSearchType.text = "账户名"
        }
    }

    override fun refreshChange() {
        adapter.setNewData(transactionRecords)
    }

    override fun refreshData(page: Int, searchType: Int) {
        presenter.getTransactionData(page, searchType)
    }

    override fun onDestroy() {
        if (companySubscription != null && !companySubscription!!.isUnsubscribed) {
            companySubscription!!.unsubscribe()
        }
        super.onDestroy()
    }

}
