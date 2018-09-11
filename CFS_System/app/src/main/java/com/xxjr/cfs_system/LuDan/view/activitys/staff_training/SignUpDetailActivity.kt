package com.xxjr.cfs_system.LuDan.view.activitys.staff_training

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.RecyclerItemShrink
import com.xxjr.cfs_system.LuDan.adapters.SignUpDetailAdapter
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.presenter.SignUpDetailPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.DateUtil
import com.xxjr.cfs_system.tools.ToastShow
import entity.ChooseType
import entity.CommonItem
import entity.StaffInfo
import kotlinx.android.synthetic.main.activity_agreement.*

class SignUpDetailActivity : BaseActivity<SignUpDetailPresenter, SignUpDetailActivity>(), BaseViewInter {
    private lateinit var adapter: SignUpDetailAdapter
    var staffInfo: StaffInfo? = null
    var projects: MutableList<ChooseType> = mutableListOf()
    var projectIds = mutableListOf<String>()//项目ids
    var honors = mutableListOf<String>()//荣誉组合
    private var refresh1 = false
    private var isClose = false //报名截止

    override fun getPresenter(): SignUpDetailPresenter = SignUpDetailPresenter()

    override fun getLayoutId(): Int = R.layout.activity_agreement

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "报名详情"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        staffInfo = (intent.getSerializableExtra("staffInfo") as? StaffInfo) ?: StaffInfo()

        if ((staffInfo?.ProjectIds ?: "").isNotBlank()) projectIds.addAll((staffInfo?.ProjectIds
                ?: "").split(","))

        projects.addAll((intent.getSerializableExtra("projects") as? MutableList<ChooseType>)
                ?: mutableListOf())

        if ((staffInfo?.Honor ?: "").isNotBlank()) honors.addAll((staffInfo?.Honor
                ?: "").split(",")) else honors.add("")

        val signUpDeadline = intent.getStringExtra("SignUpDeadline") ?: ""
        isClose = DateUtil.getTimeLong(signUpDeadline, "yyyy/MM/dd  HH:mm:ss") < System.currentTimeMillis()
        if (isClose) next.visibility = View.GONE else next.visibility = View.VISIBLE
        next.text = "删除报名信息"
        next.setOnClickListener {
            CustomDialog.showTwoButtonDialog(this@SignUpDetailActivity, "确定删除该报名信息?", "确定", "取消") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                presenter.delete()
            }
        }
        initRV()
    }

    private fun initRV() {
        subTitle.text = "保存"
        subTitle.setOnClickListener {
            staffInfo?.ProjectIds = joinText(projectIds)
            if (staffInfo?.ProjectIds.isNullOrBlank()) {
                showMsg("请最少选择一个培训项目！")
                return@setOnClickListener
            }
            val names = StringBuilder()
            for (choose in projects) {
                for (id in projectIds) {
                    if (id.isNotBlank()) {
                        if (choose.id == id.toInt()) {
                            names.append(choose.content).append(",")
                            break
                        }
                    }
                }
            }
            staffInfo?.ProjectNames = if (names.length > 1) names.substring(0, names.length - 1) else ""
            staffInfo?.Honor = joinText(honors)
            presenter.save()
        }
        rv_agree.layoutManager = LinearLayoutManager(this@SignUpDetailActivity)
        adapter = SignUpDetailAdapter(this@SignUpDetailActivity, presenter.getItemData())
        adapter.setIsClose(isClose)
        adapter.setOnItemClick(RecycleItemClickListener {
            (adapter.datas[adapter.datas.size - 1] as CommonItem<*>).content = staffInfo?.Cost ?: ""
            (adapter.datas[adapter.datas.size - 2] as CommonItem<*>).content = staffInfo?.Achievement ?: ""
            for (i in 1 until honors.size) {
                (adapter.datas[it - i] as CommonItem<*>).content = honors[honors.size - 1 - i]
            }
            val item = adapter.datas[it] as CommonItem<Any>
            item.isClick = false
            item.content = honors[item.position]
            adapter.datas.add(it + 1, CommonItem<Any>().apply {
                type = 5
                name = "获得荣誉"
                isClick = true
                position = honors.size
            })
            honors.add("")
            adapter.notifyDataSetChanged()
        })
        //是否通过
        adapter.setOnItemPassClick(RecycleItemClickListener {
            if (isClose) showMsg("报名已截止，信息不可更改") else staffInfo?.IsPass = it
        })
        //项目选择
        adapter.setOnItemChooseListener(object : RecyclerItemShrink {
            override fun onItemShrink(position: Int, isShrink: Boolean) {
                if (isClose) {
                    showMsg("报名已截止，信息不可更改")
                } else {
                    if (position < 0) {
                        when (position) {
                            -1 -> staffInfo?.IsGreatOperation = isShrink
                            -2 -> staffInfo?.IsNeedCheckInBefore = isShrink
                            -3 -> staffInfo?.IsNeedUnifiedRiding = isShrink
                        }
                    } else {
                        if (isShrink) {
                            projectIds.add(position.toString())
                        } else {
                            projectIds.remove(position.toString())
                        }
                    }
                }
            }
        })
        adapter.setTextChangeListener(TextChangeListener { positon: Int, text: String ->
            when (positon) {
                adapter.datas.size - 1 -> {
                    refresh1 = true
                    staffInfo?.Cost = text
                }
                adapter.datas.size - 2 -> {
                    refresh1 = true
                    staffInfo?.Achievement = text
                }
            }
        })
        adapter.setTextHonorChangeListener(TextChangeListener { position: Int, text: String ->
            honors[position] = text
        })
        rv_agree.adapter = adapter
        rv_agree.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                val layoutManager = recyclerView?.layoutManager as? LinearLayoutManager
                val lastVisibleItemPosition = layoutManager?.findLastVisibleItemPosition() ?: 0
                when {
                    refresh1 && (lastVisibleItemPosition < adapter.datas.size - 3) -> {
                        refresh1 = false
                        refreshItem(adapter.datas.size - 1, staffInfo?.Cost ?: "")
                        refreshItem(adapter.datas.size - 2, staffInfo?.Achievement ?: "")
                    }
                }
            }
        })
    }

    fun refreshItem(position: Int, text: String) {
        val item: CommonItem<*> = adapter.datas[position] as CommonItem<*>
        item.content = text
        adapter.notifyItemChanged(position, item)
    }

    fun refreshData(detail: MutableList<CommonItem<Any>>) {
        adapter.setNewData(detail as List<Any>?)
    }

    fun complete(isDelete: Boolean) {
        if (isDelete) showMsg("删除成功") else showMsg("保存成功")
        setResult(99)
        finish()
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    private fun joinText(list: MutableList<String>): String {
        val stringBuilder = StringBuilder()
        for (i in list.indices) {
            if (list[i].isNotBlank()) {
                stringBuilder.append(list[i])
                stringBuilder.append(",")
            }
        }
        var text = stringBuilder.toString()
        if (text.isNotBlank()) {
            text = text.substring(0, text.length - 1)
        }
        return text
    }
}
