package com.xxjr.cfs_system.LuDan.view.activitys.staff_training

import android.content.Intent
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.StaffListPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.BaseListActivity
import entity.StaffInfo
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class StaffListActivity : BaseListActivity<StaffListPresenter, StaffListActivity>() {
    var staffListInfo = mutableListOf<StaffInfo>()

    fun getTrainingId() = intent.getStringExtra("trainingId") ?: ""

    override fun getListPresenter(): StaffListPresenter = StaffListPresenter()

    override fun initAdapter() {
        tvSearchType.visibility = View.GONE
        etPactSearch.hint = "请输入姓名查询"

        adapter = object : CommonAdapter<StaffInfo>(this@StaffListActivity, arrayListOf(), R.layout.item_staff) {
            override fun convert(holder: BaseViewHolder, staff: StaffInfo, position: Int) {
                holder.setText(R.id.tv_name, "${staff.UserName}(${staff.UserPositionName})")
                holder.setText(R.id.tv_project_content, "${if (staff.ProjectNames.isNullOrBlank()) ""
                else staff.ProjectNames?.replace(",", "\n")}")
                holder.setText(R.id.tv_state, when (staff.IsPass) {
                    0 -> "未设置"
                    1 -> "是"
                    2 -> "否"
                    else -> ""
                })
                holder.setText(R.id.tv_integral, staff.Integral ?: "0")
                holder.setText(R.id.tv_grade, staff.Achievement ?: "0")
                holder.convertView.setOnClickListener {
                    val intent1 = Intent(this@StaffListActivity, StaffInfoActivity::class.java)
                    intent1.putExtra("title", intent.getStringExtra("title"))
                    intent1.putExtra("CategoryTitle", intent.getStringExtra("CategoryTitle"))
                    intent1.putExtra("staffInfo", staff)
                    intent1.putExtra("projects", intent.getSerializableExtra("projects"))
                    intent1.putExtra("SignUpDeadline", intent.getStringExtra("SignUpDeadline"))
                    startActivityForResult(intent1, 99)
                }
            }
        }
    }

    override fun refreshData(page: Int, searchType: Int) {
        presenter.getStaffListData(page)
    }

    override fun refreshChange() {
        adapter.setNewData(staffListInfo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 99) {
            isPull = false
            presenter.getStaffListData(0)
        }
    }
}