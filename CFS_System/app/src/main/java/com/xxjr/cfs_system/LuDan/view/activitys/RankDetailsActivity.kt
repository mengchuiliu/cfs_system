package com.xxjr.cfs_system.LuDan.view.activitys

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.RankDetailsAdapter
import com.xxjr.cfs_system.LuDan.presenter.RankDetailsPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.RankDetailsVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_rank_details.*
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class RankDetailsActivity : BaseActivity<RankDetailsPresenter, RankDetailsVInter>(), RankDetailsVInter {
    override fun getCommonItem(): CommonItem<Any>? = intent.getSerializableExtra("rankItem") as? CommonItem<Any>

    override fun getPresenter(): RankDetailsPresenter = RankDetailsPresenter()

    override fun getLayoutId(): Int = R.layout.activity_rank_details

    override fun initView(savedInstanceState: Bundle?) {
        val type = intent.getIntExtra("Type", 0)
        toolbarTitle.text = if (type == 3) "门店放款详情" else "签单回款详情"
        tv_rank.text = "第${intent.getIntExtra("rankNum", 0) - 1}名"
        tv_store_name.text = "${getCommonItem()?.name ?: ""}(查看当前半年数据)"
        tv_type.text = if (type == 3) "放款量" else {
            if (intent.getIntExtra("rankType", 1) == 1) "回款量" else "签单量"
        }
        presenter.setDefaultValue()
    }

    override fun isShowBacking(): Boolean = true

    override fun initRV(data: MutableList<CommonItem<Any>>, details: MutableList<CommonItem<Any>>) {
        rv_rank_detail.layoutManager = LinearLayoutManager(this@RankDetailsActivity)
        if (data.isNotEmpty()) {
            rv_rank_detail.adapter = object : CommonAdapter<CommonItem<Any>>(this@RankDetailsActivity, data, R.layout.item_recycle) {
                override fun convert(holder: BaseViewHolder?, commonItem: CommonItem<Any>, position: Int) {
                    val recycle = holder?.getView<RecyclerView>(R.id.recycle_item)
                    recycle?.layoutManager = LinearLayoutManager(this@RankDetailsActivity)
                    val rvAdapter = RankDetailsAdapter(this@RankDetailsActivity, presenter.getDatas(commonItem))
                    recycle?.adapter = rvAdapter
                }
            }
        }

        if (details.isNotEmpty()) {
            rv_company_details.layoutManager = LinearLayoutManager(this@RankDetailsActivity)
            rv_company_details.adapter = object : CommonAdapter<CommonItem<Any>>(this@RankDetailsActivity, details, R.layout.item_common_show) {
                override fun convert(holder: BaseViewHolder, commonItem: CommonItem<Any>, position: Int) {
                    holder.convertView.setPadding(0, Utils.dip2px(this@RankDetailsActivity, 8f), 0, 0)
                    holder.setTextSize(R.id.tv_content_name, 14f)
                    holder.setTextSize(R.id.tv_content, 14f)
                    holder.setTextSize(R.id.tv_content_right, 14f)
                    holder.setTextColorRes(R.id.tv_content_name, R.color.font_c6)
                    holder.setTextColorRes(R.id.tv_content, R.color.font_c6)
                    holder.setTextColorRes(R.id.tv_content_right, R.color.font_c6)
                    holder.setText(R.id.tv_content_name, "${commonItem.name}：")
                    if (position > 0) {
                        holder.setINVISIBLE(R.id.tv_content_name, !showPost(details[position].type, details[position - 1].type))
                    }
                    holder.setText(R.id.tv_content, commonItem.content)
                    holder.setVisible(R.id.tv_content_right, true)
                    holder.setText(R.id.tv_content_right, commonItem.hintContent)
                }
            }
        }
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    private fun showPost(before: Int, type: Int): Boolean = before != type
}
