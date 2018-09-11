package com.xxjr.cfs_system.LuDan.view.activitys.borrow.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager

import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.BorrowAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.view.activitys.borrow.presenter.BorrowPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.LoanFliterActivity
import com.xxjr.cfs_system.LuDan.view.viewinter.BorrowVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.BorrowInfo
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_add_loan.*

open class BorrowActivity : BaseActivity<BorrowPresenter, BorrowVInter>(), BorrowVInter {
    private var adapter: BorrowAdapter? = null

    override fun getPresenter(): BorrowPresenter = BorrowPresenter()

    override fun getLayoutId(): Int = R.layout.activity_add_loan

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun getBorrow(): BorrowInfo? = intent.getSerializableExtra("borrow") as? BorrowInfo?

    override fun getIsUpdate(): Boolean = intent.getBooleanExtra("isUpdate", false)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "拆借信息"
        subTitle.text = "保存"
        subTitle.setOnClickListener {
            presenter.addBorrowInfo(intent.getIntExtra("contractId", 0).toString())
        }
        presenter.setDefaultValue()
    }

    override fun isShowBacking(): Boolean = true

    override fun initRecycler(dataList: MutableList<CommonItem<Any>>) {
        recycler_add_loan.layoutManager = LinearLayoutManager(this@BorrowActivity)
        adapter = BorrowAdapter(this@BorrowActivity, dataList)

        adapter?.setOnItemClick(RecycleItemClickListener { position ->
            presenter.clickChoose(position, recycler_add_loan)
        })
        adapter?.setTextChangeListener(TextChangeListener { position, text ->
            presenter.editChange(position, text)
        })
        recycler_add_loan.adapter = adapter
    }

    override fun refreshItemData(pos: Int, content: String) {
        val item: CommonItem<*> = adapter?.datas?.get(pos) as CommonItem<*>
        item.content = content
        adapter?.notifyItemChanged(pos, item)
    }

    override fun completeAdd() {
        if (!getIsUpdate()) {
            val intent = Intent(this@BorrowActivity, LoanFliterActivity::class.java)
            intent.putExtra("contractType", 6)
            startActivity(intent)
        }
        setResult(909)
        this@BorrowActivity.finish()
    }

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }
}
