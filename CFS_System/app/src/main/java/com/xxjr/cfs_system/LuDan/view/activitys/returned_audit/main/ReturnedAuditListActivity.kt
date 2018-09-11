package com.xxjr.cfs_system.LuDan.view.activitys.returned_audit.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.BaseListActivity
import com.xxjr.cfs_system.LuDan.view.activitys.SearchActivity
import com.xxjr.cfs_system.LuDan.view.activitys.returned_audit.detail.ReturnedAuditDetailActivity
import com.xxjr.cfs_system.tools.BusinessUtils
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.ReturnedAuditCountEntity
import entity.ReturnedAuditEntity
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

/**
 *
 * 回款审核 界面
 * @author huangdongqiang
 * @date 25/06/2018
 */
class ReturnedAuditListActivity : BaseListActivity<ReturnedAuditListLP, ReturnedAuditListActivity>(), BaseViewInter{


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        presenter.getDefaultIntent(intent)

    }

    override fun getListPresenter(): ReturnedAuditListLP = ReturnedAuditListLP()

    @SuppressLint("ClickableViewAccessibility")
    override fun initAdapter() {
        //指定为两个title的样式
        tvSearchType.visibility = View.GONE
        tvSearchType.text = ""
        etPactSearch.hint = getString(R.string.returned_audit_search_hint)


        //第一级标签选项
        adapter0 = object : CommonAdapter<CommonItem<*>>(this@ReturnedAuditListActivity, presenter.getTab0ListResult(), R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
                holder.setText(R.id.tv_title, item.name)
                holder.setTextSize(R.id.tv_title, 15f)
                val textview = holder.getView<TextView>(R.id.tv_title)
                textview.setPadding(Utils.dip2px(this@ReturnedAuditListActivity, 10f),
                        Utils.dip2px(this@ReturnedAuditListActivity, 8f),
                        Utils.dip2px(this@ReturnedAuditListActivity, 10f),
                        Utils.dip2px(this@ReturnedAuditListActivity, 8f))
                holder.itemView.layoutParams.width = (getScreenWidth() - this@ReturnedAuditListActivity.resources.getDimensionPixelSize(R.dimen.size_6) * 6) / 3

                if (item.isClick) {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_home)
                    holder.setVisible(R.id.tv_line, true)
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_c6)
                    holder.setVisible(R.id.tv_line, false)
                }
                holder.convertView.setOnClickListener { presenter.clickTab0(position)}
            }

        }

        //二级菜单

        adapter1 = object : CommonAdapter<CommonItem<*>>(this@ReturnedAuditListActivity, presenter.getTab1ListResult(ReturnedAuditCountEntity()), R.layout.item_title) {

            override fun convert(holder: BaseViewHolder, item: CommonItem<*>, position: Int) {
                holder.setText(R.id.tv_title, item.name)
                holder.setTextSize(R.id.tv_title, 15f)
                val textview = holder.getView<TextView>(R.id.tv_title)
                textview.setPadding(Utils.dip2px(this@ReturnedAuditListActivity, 10f),
                        Utils.dip2px(this@ReturnedAuditListActivity, 8f),
                        Utils.dip2px(this@ReturnedAuditListActivity, 10f),
                        Utils.dip2px(this@ReturnedAuditListActivity, 8f))
                //只有 3 个的时候需要 3 等分
                when (datas.size) {
                    3 -> holder.itemView.layoutParams.width = (getScreenWidth() - this@ReturnedAuditListActivity.resources.getDimensionPixelSize(R.dimen.size_6) * 6) / 3
                    else -> holder.itemView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                }

                if (item.isClick) {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_home)
                    holder.setVisible(R.id.tv_line, true)
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_c6)
                    holder.setVisible(R.id.tv_line, false)
                }
                holder.convertView.setOnClickListener { presenter.clickTab1(position) }
            }

        }

        adapter = object : CommonAdapter<ReturnedAuditEntity>(this@ReturnedAuditListActivity, arrayListOf(), R.layout.item_returned_audit_list){
            override fun convert(holder: BaseViewHolder?, entity: ReturnedAuditEntity?, position: Int) {
                holder?.setText(R.id.tv_customer_names, entity?.CustomerNames?:"")
                holder?.setText(R.id.tv_id, entity?.ID?.toString())
                var content = Utils.FormatTime(entity?.RecordTime
                        ?: "", "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd")
                //过滤后台的脏数据
                if ("1900/01/01" == content) {
                    content = ""
                }
                holder?.setText(R.id.tv_record_time, content)
                //holder?.setText(R.id.tv_a16, getString(R.string.base_unit_yuan, entity?.A16))
                holder?.setText(R.id.tv_a16, getString(R.string.base_unit_yuan, BusinessUtils.getBigDecimalToDobule(entity?.LoansMoney ?: 0.0)))
                holder?.setText(R.id.tv_company, entity?.CompanyId + entity?.CompanyName)
                //holder?.setText(R.id.tv_business_state, BusinessUtils.getBusinessState(entity?.PayType ?:0, entity?.AisleType ?:"",entity?.BusinessState ?: 0))
                //交易状态修改为显示审核状态
                holder?.setText(R.id.tv_audit_state, BusinessUtils.getState(entity?.PayType, entity?.State))
                holder?.setText(R.id.tv_l3, getString(R.string.base_unit_yuan, BusinessUtils.getBigDecimalToDobule(entity?.Amount ?: 0.0)))
                holder?.setText(R.id.tv_pay_type, BusinessUtils.getPayType(entity?.PayType ?: -1))
                holder?.setText(R.id.tv_salesman, "    " + entity?.ServiceIDName ?: "")


                holder?.convertView?.setOnClickListener{
                    val intent = Intent(this@ReturnedAuditListActivity, ReturnedAuditDetailActivity::class.java)
                    intent.putExtra(Constants.BUNDLE_RETURED_AUDIT_ENTITY, entity)
                    intent.putExtra(Constants.BUNDLE_RETURED_AUDIT_SCHEDULE_POS, presenter.schedulePos)
                    intent.putExtra(Constants.BUNDLE_RETURED_AUDIT_AUDIT_POS, presenter.auditPos)
                    startActivityForResult(intent, Constants.REQUEST_CODE_RETURED_AUDIT)
                }

            }
        }

    }

    /**
     * 刷新一级标题
     */
    fun refreshTab0(position: Int) {
        for (i in 0 until adapter0.datas.size) {
            (adapter0.datas[i] as CommonItem<*>).isClick = i == position
        }
        adapter0.notifyDataSetChanged()

    }

    /**
     * 设置是否上拉
     */
    fun setIsPull(isPullEnable : Boolean) {
        isPull = isPullEnable
    }

    /**
     * 设置页码
     */
    fun setPageIndex(pageIndex: Int) {
        page = pageIndex
    }

    /**
     * 刷新二级标题
     */
    fun refreshTab1(position: Int) {
        for (i in 0 until adapter1.datas.size) {
            (adapter1.datas[i] as CommonItem<*>).isClick = i == position
        }
        adapter1.notifyDataSetChanged()
    }

    /**
     * 刷新列表数据
     */
    fun refreshData(list: List<ReturnedAuditEntity>) {
        adapter?.setNewData(list)

    }

    /**
     * 基类刷新的时候调用的方法
     */
    override public fun refreshData(page: Int, searchType: Int) {
        presenter.frameRefresh(page, searchType)
    }


    /**
     * 设置数据 一级标签
     */
    fun setNewDataTab0(commonItems: List<CommonItem<*>>) {
        adapter0?.setNewData(commonItems)
    }

    /**
     * 设置数据 二级菜单
     */
    fun setNewDataTab1(commonItems: List<CommonItem<*>>) {
        adapter1?.setNewData(commonItems)
    }


    /**
     * 获取一级菜单数组
     */
    fun getTab0List() : List<String> {
        return this.getString(R.string.returned_audit_tab_0).split(",")
    }

    /**
     * 获取二级菜单数组
     */
    fun getTab1List(schedulePos: Int) : List<String> {
        return when (schedulePos) {
            0 -> this.getString(R.string.returned_audit_tab_01).split(",")
            1 -> this.getString(R.string.returned_audit_tab_02).split(",")
            2 -> this.getString(R.string.returned_audit_tab_03).split(",")
            else -> mutableListOf()
        }
    }

    fun setETContent(content: String) {
        etPactSearch?.setText(content)
    }

    /**
     * 获取基类定义的页码
     */
    fun getPageIndex() : Int {
        return page
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth(): Int {
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.x
    }

    fun showShort(resId: Int) {
        ToastShow.showShort(this, resId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode, data)
    }

}