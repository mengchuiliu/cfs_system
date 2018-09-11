package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.adapters.UpdateScheduleAdapter
import com.xxjr.cfs_system.LuDan.presenter.UpdateSchedulePresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.UpdateScheduleVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.services.CacheProvide
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.LoanInfo
import kotlinx.android.synthetic.main.activity_update_schedule.*

class UpdateScheduleActivity : BaseActivity<UpdateSchedulePresenter, UpdateScheduleVInter>(), UpdateScheduleVInter {
    private var loanInfo: LoanInfo? = null
    private var isBack: Boolean = false
    private var adapter: UpdateScheduleAdapter? = null
    private var remark = ""

    override fun getLoanInfo(): LoanInfo = loanInfo ?: LoanInfo()

    override fun getPresenter(): UpdateSchedulePresenter = UpdateSchedulePresenter()

    override fun getLayoutId(): Int = R.layout.activity_update_schedule

    override fun isShowBacking(): Boolean = true

    override fun getNewLend(): Boolean = false

    override fun initView(savedInstanceState: Bundle?) {
        loanInfo = intent.getSerializableExtra("loanInfo") as? LoanInfo
        toolbarTitle.text = CacheProvide.getLoanStatus(loanInfo?.scheduleId ?: 0)
        subTitle.text = "回退"
        initBtClick()
        presenter.setDefaultValue()
    }

    private fun initBtClick() {
        if (getLoanInfo().scheduleId == 1) {
            subTitle.visibility = View.GONE
        }
        subTitle.setOnClickListener {
            isBack = !isBack
            if (isBack) {
                subTitle.text = "更新"
                tv_update.text = "回退进度"
            } else {
                subTitle.text = "回退"
                tv_update.text = "更新进度"
            }
            presenter.refreshAdapterData(isBack)
        }

        tv_update.setOnClickListener {
            presenter.checkAndUpdate(isBack)
        }
    }

    override fun showMsg(msg: String?) {
        ToastShow.showShort(applicationContext, msg)
    }

    override fun initRecycler(list: MutableList<CommonItem<Any>>) {
        rv_schedule.layoutManager = LinearLayoutManager(this)
        adapter = UpdateScheduleAdapter(this, list)
        adapter?.setOnItemClick(RecycleItemClickListener { position ->
            when (getLoanInfo().scheduleId) {
                1 -> {
                    val intent = Intent(this@UpdateScheduleActivity, SearchActivity::class.java)
                    intent.putExtra("type", Constants.BANK_MANAGER_CODE)
                    intent.putExtra("hintContent", "搜索银行经理")
                    intent.putExtra("bankId", getLoanInfo().getBankId())
                    startActivity(intent)
                }
                else -> {
                    presenter.showTimeChoose(position)
                }
            }
        })
        adapter?.setTextChangeListener(TextChangeListener { position, text ->
            remark = text ?: ""
        })

        adapter?.setTextEditChangeListener(TextChangeListener { position, text ->
            when (getLoanInfo().scheduleId) {
                3, 103 -> {
                    when (position) {
                        0 -> {
                            if (!text.isNullOrBlank()) {
                                getLoanInfo().replyAmount = Utils.getBigLong(text)
                            }
                        }
                        1 -> {
                            getLoanInfo().accountName = text
                        }
                        2 -> {
                            getLoanInfo().account = text
                        }
                        3 -> {
                            getLoanInfo().offer = text
                        }
                    }
                }
                4, 108 -> {
                    when (position) {
                        0 -> {
                            if (!text.isNullOrBlank()) {
                                getLoanInfo().lendingAmount = Utils.getBigLong(text)
                            }
                        }
                        2 -> {
                            if (!text.isNullOrBlank()) {
                                getLoanInfo().monthAmount = Utils.getBigLong(text)
                            }
                        }
                        3 -> {
                            getLoanInfo().monthDate = text
                        }
                    }
                }
            }
        })
        rv_schedule.adapter = adapter
    }

    override fun refreshItem(position: Int, text: String) {
        val item: CommonItem<*> = adapter?.datas?.get(position) as CommonItem<*>
        item.content = text
        adapter?.notifyItemChanged(position, item)
    }

    override fun refreshItem(position: Int, show: Boolean) {
    }

    override fun refreshAdapter(list: MutableList<CommonItem<Any>>) {
        adapter?.setNewData(list as List<Any>?)
        adapter?.notifyDataSetChanged()
    }

    override fun getRemark(): String = remark

    override fun complete() {
        RxBus.getInstance().post(Constants.POST_REFRESH_MY_TASK, true)
        showMsg("进度更新成功!")
        setResult(Constants.REQUEST_CODE_UPDATE_SCHEDULE)
        this@UpdateScheduleActivity.finish()
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
