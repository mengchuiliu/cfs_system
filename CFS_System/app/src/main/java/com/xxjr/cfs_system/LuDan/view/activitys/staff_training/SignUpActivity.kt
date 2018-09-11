package com.xxjr.cfs_system.LuDan.view.activitys.staff_training

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.util.SparseArray
import android.view.View
import android.widget.TextView
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.IconCenterEditText
import com.xiaoxiao.widgets.dialog.base.BaseDialogInterface
import com.xxjr.cfs_system.LuDan.presenter.SignUpPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import entity.StaffInfo
import kotlinx.android.synthetic.main.activity_sign_up.*
import refresh_recyclerview.PullToRefreshRecyclerView
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class SignUpActivity : BaseActivity<SignUpPresenter, SignUpActivity>(), BaseViewInter {
    private lateinit var adapter0: CommonAdapter<ChooseType>
    private lateinit var adapter: CommonAdapter<StaffInfo>
    private lateinit var applySuccessDialog: ApplySuccessDialog
    var projects: MutableList<ChooseType> = mutableListOf()
    var staffs: MutableList<StaffInfo> = mutableListOf()
    var sparseArray = SparseArray<MutableList<StaffInfo>>()
    var projectId = -1
    var page = 0
    var pull = false

    override fun getPresenter(): SignUpPresenter = SignUpPresenter()

    override fun getLayoutId(): Int = R.layout.activity_sign_up

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    fun getTrainingId() = intent.getStringExtra("trainingId") ?: ""

    fun getETContent() = et_search.text.toString().trim()

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "报名"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        ivRight.setImageResource(R.mipmap.filtrate)
        projects = (intent.getSerializableExtra("projects") as? MutableList<ChooseType>) ?: mutableListOf()
        for (choose in projects) {
            sparseArray.put(choose.id, mutableListOf())
        }
        projects[0].isChoose = true
        projectId = projects[0].id
        presenter.setDefaultValue()
        init()
    }

    private fun init() {
        ivRight.setOnClickListener {
            presenter.showFilter(activity_sign_up) //筛选
        }
        //软件盘搜索
        et_search.setOnSearchClickListener(object : IconCenterEditText.OnSearchClickListener {
            override fun onSearchClick(view: View) {
                pull = false
                page = 0
                presenter.getStaffData()
            }
        })
        applySuccessDialog = ApplySuccessDialog(this)
        applySuccessDialog.setOnCancelClickListener(BaseDialogInterface.OnCancelClickListener {
            //完成
            setResult(99)
            finish()
        })
        applySuccessDialog.setOnConfirmClickListener(BaseDialogInterface.OnConfirmClickListener {
            //继续
        })
        tv_sign_up.setOnClickListener {
            //报名
            presenter.completeApply()
        }
        initRV()
    }

    private fun initRV() {
        recycler_project.layoutManager = LinearLayoutManager(this@SignUpActivity, LinearLayoutManager.HORIZONTAL, false)
        adapter0 = object : CommonAdapter<ChooseType>(this@SignUpActivity, projects, R.layout.item_title) {
            override fun convert(holder: BaseViewHolder, item: ChooseType, position: Int) {
                holder.setText(R.id.tv_title, item.content)
                holder.setTextSize(R.id.tv_title, 15f)
                val textview = holder.getView<TextView>(R.id.tv_title)
                textview.setPadding(Utils.dip2px(this@SignUpActivity, 15f),
                        Utils.dip2px(this@SignUpActivity, 10f),
                        Utils.dip2px(this@SignUpActivity, 15f),
                        Utils.dip2px(this@SignUpActivity, 10f))
                if (item.isChoose) {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_home)
                    holder.setVisible(R.id.tv_line, true)
                } else {
                    holder.setTextColorRes(R.id.tv_title, R.color.font_c6)
                    holder.setVisible(R.id.tv_line, false)
                }
                holder.convertView.setOnClickListener {
                    projectId = item.id
                    for (i in 0 until adapter0.datas.size) {
                        adapter0.datas[i].isChoose = i == position
                    }
                    adapter0.notifyDataSetChanged()
                    pull = false
                    page = 0
                    presenter.postIds = ""
                    et_search.setText("")
                    presenter.getStaffData()
                }
            }
        }
        recycler_project.adapter = adapter0


        adapter = object : CommonAdapter<StaffInfo>(this@SignUpActivity, staffs, R.layout.item_sign_up) {
            override fun convert(holder: BaseViewHolder, item: StaffInfo, position: Int) {
                holder.setText(R.id.tv_name, item.UserName)
                if (item.IsTimeOverlap) holder.setTextColorRes(R.id.tv_name, R.color.font_c9)
                else holder.setTextColorRes(R.id.tv_name, R.color.font_c3)
                holder.setImageResource(R.id.iv_choose, if (item.DelMarker) R.mipmap.choose_blue else R.mipmap.choose_gray)
                holder.setOnClickListener(R.id.iv_choose) {
                    if (!item.IsTimeOverlap) {
                        if (!item.DelMarker) {
                            sparseArray.get(projectId).add(item)
                        } else {
                            var pos = -1
                            for (i in sparseArray.get(projectId).indices) {
                                if (sparseArray.get(projectId)[i].UserId == item.UserId) {
                                    pos = i
                                    break
                                }
                            }
                            if (pos != -1) sparseArray.get(projectId).removeAt(pos)
                        }
                        adapter.notifyItemChanged(position, item.apply { DelMarker = !DelMarker })
                    }
                }
            }
        }
        rv_sign_up.addItemDecoration(1)
        rv_sign_up.setAdapter(adapter)
        rv_sign_up.setOnRefreshListener(object : PullToRefreshRecyclerView.OnRefreshListener {
            override fun onPullDownRefresh() {
                Handler().postDelayed({
                    pull = false
                    page = 0
                    presenter.postIds = ""
                    et_search.setText("")
                    presenter.chooseTypes.apply {
                        for (choose in this) {
                            choose.isChoose = false
                        }
                    }
                    presenter.getStaffData()
                }, 500)
            }

            override fun onLoadMore() {
                Handler().postDelayed({
                    pull = true
                    page++
                    presenter.getStaffData()
                }, 500)
            }
        })
    }

    fun completeRefresh() {
        if (pull) {
            rv_sign_up.completeLoadMore()
        } else {
            rv_sign_up.completeRefresh()
        }
    }

    fun refreshData() {
        adapter.setNewData(staffs)
        completeRefresh()
    }

    fun complete() {
        applySuccessDialog.show()
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
//        Handler().postDelayed({ rv_sign_up.completeLoadMore() }, 200)
    }
}
