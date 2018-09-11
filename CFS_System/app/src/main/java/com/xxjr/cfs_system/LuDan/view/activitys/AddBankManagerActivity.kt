package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.AddBankManagerAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.presenter.AddBankManagerP
import com.xxjr.cfs_system.LuDan.view.viewinter.AddBankManagerVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.ToastShow
import entity.BankManager
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_add_bank_manager.*

class AddBankManagerActivity : BaseActivity<AddBankManagerP, AddBankManagerVInter>(), AddBankManagerVInter {
    private var manager: BankManager? = null
    private var adapter: AddBankManagerAdapter? = null
    private var refresh1 = false

    override fun getPresenter(): AddBankManagerP = AddBankManagerP()

    override fun getLayoutId(): Int = R.layout.activity_add_bank_manager

    override fun isShowBacking(): Boolean = true

    override fun initView(savedInstanceState: Bundle?) {
        manager = intent.getSerializableExtra("manager") as BankManager?
        if (manager == null) {
            manager = BankManager()
            manager?.id = -1
            toolbarTitle.text = "添加银行经理"
        } else {
            toolbarTitle.text = manager?.bankManagerName
        }
        tv_save.setOnClickListener { presenter.save() }
        presenter.setDefaultValue()
    }

    override fun getManager(): BankManager? = manager

    override fun initRv(commonItems: MutableList<CommonItem<Any>>) {
        rv_add_bank_manager.layoutManager = LinearLayoutManager(this@AddBankManagerActivity)
        adapter = AddBankManagerAdapter(this@AddBankManagerActivity, commonItems)
        adapter?.setOnItemClick(RecycleItemClickListener { position ->
            when (position) {
                1 -> {
                    val intent1 = Intent(this@AddBankManagerActivity, SearchActivity::class.java)
                    intent1.putExtra("type", Constants.BANK_CODE)
                    intent1.putExtra("hintContent", "搜索银行")
                    startActivity(intent1)
                }
                6 -> presenter.showZone(rv_add_bank_manager)
            }
        })

        adapter?.setTextChangeListener(TextChangeListener { position: Int, text: String ->
            when (position) {
                0 -> {
                    refresh1 = true
                    manager?.bankManagerName = text
                }
                2 -> {
                    refresh1 = true
                    manager?.phone1 = text
                }
                3 -> {
                    refresh1 = true
                    manager?.phone2 = text
                }
                4 -> manager?.branchBankName = text
                5 -> manager?.recommendedName = text
                7 -> manager?.remark = text
            }
        })
        rv_add_bank_manager.adapter = adapter

        rv_add_bank_manager.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                val layoutManager = recyclerView?.getLayoutManager() as? LinearLayoutManager
                val firstVisibleItemPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0
                when {
                    refresh1 && firstVisibleItemPosition > 3 -> {
                        refresh1 = false
                        refreshItem(0, manager?.bankManagerName ?: "")
                        refreshItem(2, manager?.phone1 ?: "")
                        refreshItem(3, manager?.phone2 ?: "")
                    }
                }
            }
        })
    }

    override fun refreshItem(pos: Int, text: String) {
        val item: CommonItem<*> = adapter?.datas?.get(pos) as CommonItem<*>
        item.content = text
        adapter?.notifyItemChanged(pos, item)
    }

    override fun complete() {
        showMsg("保存成功")
        setResult(99)
        this@AddBankManagerActivity.finish()
    }

    override fun showMsg(msg: String?) = ToastShow.showShort(application, msg)

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }
}
