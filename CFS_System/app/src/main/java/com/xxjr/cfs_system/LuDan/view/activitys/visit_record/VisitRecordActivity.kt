package com.xxjr.cfs_system.LuDan.view.activitys.visit_record

import android.content.Intent
import android.view.View
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.SwipeMenuLayout
import com.xxjr.cfs_system.LuDan.presenter.VisitRecordPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.BaseListActivity
import com.xxjr.cfs_system.LuDan.view.viewinter.VisitRecordVInter
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.VisitRecord
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class VisitRecordActivity : BaseListActivity<VisitRecordPresenter, VisitRecordVInter>(), VisitRecordVInter {
    private var type = 0//0->来访记录  1->邀约记录 2->客户反馈

    private var records = mutableListOf<VisitRecord>()

    override fun getVisitRecords(): MutableList<VisitRecord> = records

    override fun getListPresenter(): VisitRecordPresenter = VisitRecordPresenter()

    override fun initAdapter() {
        ivDate.visibility = View.VISIBLE
        ivDate.setImageResource(R.mipmap.qr)
        ivAdd.setOnClickListener {
            when (type) {
                0 -> {
                    val intent = Intent(this@VisitRecordActivity, AddVisitorActivity::class.java)
                    startActivityForResult(intent, 6)
                }
                1 -> {
                    val intent = Intent(this@VisitRecordActivity, AddInvitationActivity::class.java)
                    startActivityForResult(intent, 8)
                }
            }
        }
        ivDate.setOnClickListener { startActivity(QRCodeActivity::class.java) }

        adapter = object : CommonAdapter<VisitRecord>(this@VisitRecordActivity, arrayListOf(), R.layout.item_visit_record) {
            override fun convert(holder: BaseViewHolder, record: VisitRecord, position: Int) {
                val swipeMenuLayout = (holder.convertView as SwipeMenuLayout).setIos(false).setLeftSwipe(true)
                swipeMenuLayout.isSwipeEnable = false
                holder.setText(R.id.tv_company_name, getName(record.CompanyName))
                when (type) {
                    0 -> {
                        holder.setText(R.id.tv_date, Utils.FormatTime(record.VisitTime
                                ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy.MM.dd HH:mm"))
                        holder.setText(R.id.tv_title_1, "客户：")
                        holder.setVisible(R.id.tv_customer, true)
                        holder.setVisible(R.id.tv_feedback, false)
                        holder.setVisible(R.id.ll_remark, false)
                        holder.setVisible(R.id.ll_invitation, false)
                        holder.setText(R.id.tv_customer, "${getName(record.CustomerName)}(${getName(record.MobilePhone?.replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2"))})")
                        holder.setText(R.id.tv_clerk, "业务员：${getName(record.SalesName)}" +
                                "(${getName((record.SalesPhoneNumber
                                        ?: "").replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2"))})")
                    }
                    1 -> {
                        holder.setText(R.id.tv_date, record.InsertTime)
                        holder.setText(R.id.tv_title_1, "客户：")
                        holder.setVisible(R.id.tv_customer, true)
                        holder.setVisible(R.id.tv_feedback, false)
                        holder.setVisible(R.id.ll_remark, (record.remark ?: "").isNotBlank())
                        holder.setVisible(R.id.ll_invitation, true)
                        holder.setText(R.id.tv_customer, "${getName(record.CustomerName)}(${getName(record.MobilePhone?.replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2"))})")
                        holder.setText(R.id.tv_clerk, "业务员：${getName(record.SalesName)}" +
                                "(${getName(record.SalesPhoneNumber?.replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2"))})")
                        holder.setText(R.id.tv_invitation, record.VisitTime)
                        holder.setText(R.id.tv_remark, record.remark ?: "")
                    }
                    2 -> {
                        holder.setText(R.id.tv_date, Utils.FormatTime(record.VisitTime
                                ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy.MM.dd HH:mm"))
                        holder.setVisible(R.id.tv_customer, false)
                        holder.setVisible(R.id.tv_feedback, true)
                        holder.setVisible(R.id.ll_remark, false)
                        holder.setVisible(R.id.ll_invitation, false)
                        holder.setText(R.id.tv_title_1, record.CustomerName)
                        holder.setText(R.id.tv_clerk, "反馈：")
                        holder.setText(R.id.tv_feedback, record.SalesName)
                    }
                }
            }
        }

        adapter0 = object : CommonAdapter<CommonItem<Any>>(this@VisitRecordActivity, presenter.getVisitTitles(), R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<Any>, position: Int) {
                holder.convertView.layoutParams.width = (Utils.getScreenWidth(this@VisitRecordActivity)
                        - this@VisitRecordActivity.resources.getDimensionPixelSize(R.dimen.size_12) * presenter.getVisitTitles().size) / presenter.getVisitTitles().size
                holder.setText(R.id.tv_title, item.name)
                holder.setTextSize(R.id.tv_title, 14f)
                val textview = holder.getView<TextView>(R.id.tv_title)
                textview.setPadding(Utils.dip2px(this@VisitRecordActivity, 10f),
                        Utils.dip2px(this@VisitRecordActivity, 10f),
                        Utils.dip2px(this@VisitRecordActivity, 10f),
                        Utils.dip2px(this@VisitRecordActivity, 10f))
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

    private fun getName(text: String?): String {
        if (text.isNullOrBlank()) {
            return "空"
        } else {
            return text ?: ""
        }
    }

    private fun refreshTitle0(position: Int) {
        records.clear()
        type = position
        if (type == 2) {
            tvSearchType.visibility = View.GONE
            etPactSearch.hint = "请输入客户手机"
            ivAdd.visibility = View.GONE
        } else {
            tvSearchType.text = "客户名"
            tvSearchType.visibility = View.VISIBLE
            etPactSearch.hint = "请输入搜索内容"
            ivAdd.visibility = View.VISIBLE
        }
        isPull = false
        page = 0
        searchType = 0
        chooseTime1 = ""
        chooseTime2 = ""
        visiTime.text = ""
        cleanET()
        for (i in 0 until adapter0.datas.size) {
            (adapter0.datas[i] as CommonItem<*>).isClick = i == position
        }
        adapter0.notifyDataSetChanged()
        presenter.refreshRecord(page, searchType, type)
    }

    override fun refreshChange() {
        adapter.setNewData(records)
    }

    override fun refreshData(page: Int, searchType: Int) {
        presenter.refreshRecord(page, searchType, type)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isPull = false
        cleanET()
        page = 0
        searchType = 0
        refreshData(page, searchType)
    }
}
