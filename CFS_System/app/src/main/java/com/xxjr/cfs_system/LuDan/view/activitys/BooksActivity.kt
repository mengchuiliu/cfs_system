package com.xxjr.cfs_system.LuDan.view.activitys

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.BooksAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.presenter.BooksPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.BooksVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import entity.LoanInfo
import kotlinx.android.synthetic.main.activity_books.*

class BooksActivity : BaseActivity<BooksPresenter, BooksVInter>(), BooksVInter, View.OnClickListener {
    var adapter: BooksAdapter? = null
    var refresh1 = false
    var refresh2 = false
    var refresh3 = false
    var refresh4 = false

    override fun getBooksType(): Int = intent.getIntExtra("type", 1)

    override fun getLoanInfo(): LoanInfo {
        return if (intent.getSerializableExtra("loanInfo") == null) LoanInfo()
        else intent.getSerializableExtra("loanInfo") as LoanInfo
    }

    override fun getPresenter(): BooksPresenter = BooksPresenter()

    override fun getLayoutId(): Int = R.layout.activity_books

    override fun initView(savedInstanceState: Bundle?) {
        when (getBooksType()) {
            1 -> tv_title.text = "客户回款入账"
            2 -> tv_title.text = "客户退款出账"
            else -> {
                tv_title.text = "转总部入账"
                tvb_back.visibility = View.VISIBLE
            }
        }
        iv_back.setOnClickListener(this)
        tv_submit.setOnClickListener(this)
        presenter.setDefaultValue()
    }

    override fun initRecycler(list: MutableList<CommonItem<Any>>) {
        rv_books.layoutManager = LinearLayoutManager(this@BooksActivity)
        adapter = BooksAdapter(this, list)
        adapter?.setOnItemClick(RecycleItemClickListener { position ->
            presenter.clickChoose(position, rv_books)
        })

        adapter?.setTextChangeListener(TextChangeListener { position, text ->
            when (position) {
                4 -> {//金额
                    if (!text.isNullOrBlank()) {
                        refresh1 = true
                        presenter.getBookRecord().amount = text.toDouble()
                    }
                }
                5 -> {
                    if (!text.isNullOrBlank()) {
                        refresh1 = true
                        presenter.getBookRecord().serviceCharge = text.toDouble()
                    }
                }
                10 -> { //摘要
                    refresh2 = isTextNull(text)
                    presenter.getBookRecord().digest = text
                }
                16 -> {
                    refresh3 = isTextNull(text)
                    if (getBooksType() == 1) {
                        presenter.getBookRecord().otherName = text
                    } else {
                        presenter.getBookRecord().ourName = text
                    }
                }
                17 -> {
                    refresh3 = isTextNull(text)
                    if (getBooksType() == 1) {
                        presenter.getBookRecord().otherAccount = text
                    } else {
                        presenter.getBookRecord().ourAccount = text
                    }
                }
                25 -> { //合同备注
                    refresh4 = isTextNull(text)
                    presenter.getBookRecord().remark = text
                }
            }
        })
        rv_books.adapter = adapter

        rv_books.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                val layoutManager = recyclerView?.getLayoutManager() as? LinearLayoutManager
                val lastVisibleItemPosition = layoutManager?.findLastVisibleItemPosition() ?: 0
                val firstVisibleItemPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0
                when {
                    refresh1 && firstVisibleItemPosition > 5 -> {
                        refresh1 = false
                        refreshItem(4, presenter.getBookRecord().amount.toString())
                        refreshItem(5, presenter.getBookRecord().serviceCharge.toString())
                    }
                    refresh2 && firstVisibleItemPosition > 10 -> {
                        refresh2 = false
                        refreshItem(10, presenter.getBookRecord().digest)
                    }
                    refresh3 && lastVisibleItemPosition < 16 -> {
                        refresh3 = false
                        if (getBooksType() == 1) {
                            refreshItem(16, presenter.getBookRecord().otherName)
                            refreshItem(17, presenter.getBookRecord().otherAccount)
                        } else {
                            refreshItem(16, presenter.getBookRecord().ourName)
                            refreshItem(17, presenter.getBookRecord().ourAccount)
                        }
                    }
                    refresh4 && lastVisibleItemPosition < 25 -> {
                        refresh4 = false
                        refreshItem(25, presenter.getBookRecord().remark)
                    }
                }
            }
        })
    }

    private fun isTextNull(string: String?): Boolean = !string.isNullOrBlank()

    override fun refreshItem(position: Int, text: String) {
        val item: CommonItem<*> = adapter?.datas?.get(position) as CommonItem<*>
        item.content = text
        adapter?.notifyItemChanged(position, item)
    }

    override fun refreshItem(position: Int,isEnable: Boolean) {
        val item: CommonItem<*> = adapter?.datas?.get(position) as CommonItem<*>
        item.isEnable = isEnable
        adapter?.notifyItemChanged(position, item)
    }

    override fun refreshItemName(position: Int, text: String) {
        val item: CommonItem<*> = adapter?.datas?.get(position) as CommonItem<*>
        item.name = text
        adapter?.notifyItemChanged(position, item)
    }

    override fun refreshAdapter() {
        adapter?.notifyDataSetChanged()
    }


    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun complete() {
        showMsg("提交成功")
        when (getBooksType()) {
            3 -> {
                setResult(Constants.REQUEST_CODE_BOOKS_TOTLE)
            }
            else -> {
                setResult(Constants.REQUEST_CODE_BOOKS_RU_CHU)
            }
        }
        this@BooksActivity.finish()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            iv_back -> this@BooksActivity.finish()
            tv_submit -> {
                presenter.postData()
            }
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
