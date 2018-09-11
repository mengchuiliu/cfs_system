package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.KeyEvent
import com.orhanobut.hawk.Hawk

import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.adapters.CostRecordAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.presenter.CostDetailPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.CostDetailsVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.Cost
import entity.LoanInfo
import kotlinx.android.synthetic.main.activity_cost_details.*

class CostDetailsActivity : BaseActivity<CostDetailPresenter, CostDetailsVInter>(), CostDetailsVInter {
    private var loanInfo: LoanInfo? = null
    private var isAdd = false
    var adapter: CostRecordAdapter? = null
    var amount: Double = 0.0
    var remark: String = ""

    override fun getPresenter(): CostDetailPresenter = CostDetailPresenter()

    override fun getLayoutId(): Int = R.layout.activity_cost_details

    override fun initView(savedInstanceState: Bundle?) {
        loanInfo = intent.getSerializableExtra("loanInfo") as? LoanInfo
        toolbarTitle.text = "成本详情"
        subTitle.text = "添加"
        subTitle.setOnClickListener {
            if (isAdd) {
                presenter.saveCost(amount, remark)
            } else {
                isAdd = true
                toolbarTitle.text = "成本录入"
                subTitle.text = "保存"
                presenter.refreshRecyclerData()
            }
        }
        presenter.setDefaultValue()
    }

    override fun isShowBacking(): Boolean = true

    override fun getLoanInfo(): LoanInfo = loanInfo ?: LoanInfo()

    override fun getIsAdd(): Boolean = isAdd

    override fun showMsg(msg: String?) = ToastShow.showShort(this@CostDetailsActivity, msg)

    override fun initRecycler(commonItems: MutableList<CommonItem<*>>) {
        recycler_cost.layoutManager = LinearLayoutManager(this@CostDetailsActivity)
        adapter = CostRecordAdapter(this@CostDetailsActivity, commonItems,
                CFSUtils.getPermitValue(Hawk.get("PermitValue", ""), loanInfo?.loanType ?: 0))
        adapter?.setItemClick(RecycleItemClickListener { position: Int ->
            when (position) {
                12 -> presenter.showCostType(recycler_cost)
                13 -> presenter.showTimeChoose()
            }
        })
        adapter?.setTextChange(TextChangeListener { position, text ->
            if (position == 14) {//金额
                if (!TextUtils.isEmpty(text))
                    amount = Utils.getBigLong(text.toString())
            } else if (position == 15) {//备注
                remark = text
            }
        })

        adapter?.setDeleteClick(RecycleItemClickListener { position ->
            CustomDialog.showTwoButtonDialog(this@CostDetailsActivity, "确定删除该未审核成本?", "确定", "取消"
            ) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                val item = adapter?.datas?.get(adapter?.datas?.size!! - 1) as CommonItem<*>
                val cost: Cost = item.list.get(position) as Cost
                presenter.delCost(cost.loanCostId)
            }
        })
        recycler_cost.adapter = adapter
    }

    override fun refreshItem(content: String, pos: Int) {
        val commonItem: CommonItem<*> = adapter?.datas?.get(pos) as CommonItem<*>
        commonItem.content = content
        adapter?.notifyItemChanged(pos, commonItem)
    }

    override fun refreshItem(content: String, ishow: Boolean) {
        val commonItem: CommonItem<*> = adapter?.datas?.get(15) as CommonItem<*>
        commonItem.hintContent = content
        commonItem.isClick = ishow
        adapter?.notifyItemChanged(15, commonItem)
    }

    override fun refreshData(commonItems: MutableList<CommonItem<*>>) {
        adapter?.setNewData(commonItems as List<Any>?)
        adapter?.notifyDataSetChanged()
    }

    override fun complete() {
        amount = 0.0
        remark = ""
        isAdd = false
        toolbarTitle.text = "成本详情"
        subTitle.text = "添加"
        presenter.getCostListData()
        //this@CostDetailsActivity.finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isAdd) {
                isAdd = false
                toolbarTitle.text = "成本详情"
                subTitle.text = "添加"
                presenter.refreshRecyclerData()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (isAdd) {
            isAdd = false
            toolbarTitle.text = "成本详情"
            subTitle.text = "添加"
            presenter.refreshRecyclerData()
        } else {
            setResult(9999)
            super.onBackPressed()
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
