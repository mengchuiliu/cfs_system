package com.xxjr.cfs_system.LuDan.view.activitys.staff_training

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.TrainingDetailAdapter
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.Utils
import entity.CommonItem
import entity.StaffInfo
import kotlinx.android.synthetic.main.activity_common_list.*

class StaffInfoActivity : BaseActivity<BasePresenter<*, *>, BaseViewInter>() {
    private lateinit var staffInfo: StaffInfo

    override fun getPresenter(): BasePresenter<*, *>? = null

    override fun getLayoutId(): Int = R.layout.activity_common_list

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "报名详情"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        staffInfo = (intent.getSerializableExtra("staffInfo") as? StaffInfo) ?: StaffInfo()
        initRV()
    }

    private fun initRV() {
        val permits = CFSUtils.getPostPermitValue(Hawk.get("PermitValue", ""), "823")
        if (permits != null && permits.contains("EH")) subTitle.visibility = View.VISIBLE else subTitle.visibility = View.GONE
        subTitle.text = "编辑"
        subTitle.setOnClickListener {
            val intent1 = Intent(this@StaffInfoActivity, SignUpDetailActivity::class.java)
            intent1.putExtra("staffInfo", staffInfo)
            intent1.putExtra("projects", intent.getSerializableExtra("projects"))
            intent1.putExtra("SignUpDeadline", intent.getStringExtra("SignUpDeadline"))
            startActivityForResult(intent1, 99)
        }
        rv_remind.layoutManager = LinearLayoutManager(this@StaffInfoActivity)
        rv_remind.adapter = TrainingDetailAdapter(this@StaffInfoActivity, getItemData())
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 99) {
            setResult(99)
            finish()
        }
    }

    private fun getItemData() = mutableListOf<CommonItem<Any>>().apply {
        for (i in 0..19) {
            add(CommonItem<Any>().apply {
                when (i) {
                    0 -> {
                        type = 1
                        icon = R.mipmap.personnel
                        name = "人员信息"
                    }
                    1 -> {
                        type = 2
                        name = "员工姓名："
                        content = staffInfo.UserName ?: ""
                    }
                    2 -> {
                        type = 2
                        name = "员工职位："
                        content = staffInfo.UserPositionName ?: ""
                    }
                    3 -> {
                        type = 2
                        name = "员工编号："
                        content = staffInfo.EmployeeNo ?: ""
                    }
                    4 -> {
                        type = 0
                        isLineShow = true
                    }
                    5 -> type = 0
                    6 -> {
                        type = 1
                        icon = R.mipmap.signup
                        name = "报名信息"
                    }
                    7 -> {
                        type = 2
                        name = "培训名称："
                        content = intent.getStringExtra("title") ?: ""
                    }
                    8 -> {
                        type = 2
                        name = "培训类型："
                        content = intent.getStringExtra("CategoryTitle") ?: ""
                    }
                    9 -> {
                        type = 2
                        name = "培训项目："
                        content = "${if (staffInfo.ProjectNames.isNullOrBlank()) ""
                        else staffInfo.ProjectNames?.replace(",", "\n")}"
                    }
                    10 -> {
                        type = 2
                        name = "报名时间："
                        content = Utils.FormatTime(staffInfo.SignUpTime, "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd HH:mm:ss")
                    }
                    11 -> {
                        type = 2
                        name = "是否通过："
                        content = when (staffInfo.IsPass) {
                            0 -> "未设置"
                            1 -> "是"
                            2 -> "否"
                            else -> ""
                        }
                    }
                    12 -> {
                        type = 2
                        name = "获得积分："
                        content = staffInfo.Integral ?: ""
                    }
                    13 -> {
                        type = 2
                        name = "获得荣誉："
                        content = staffInfo.Honor ?: "暂无"
                    }
                    14 -> {
                        type = 2
                        name = "个人成绩："
                        content = staffInfo.Achievement ?: "暂无"
                    }
                    15 -> {
                        type = 2
                        name = "培训费用："
                        content = if (staffInfo.Cost.isNullOrBlank()) "暂无" else "${staffInfo.Cost}元"
                    }
                    16 -> {
                        type = 2
                        name = "适宜运动："
                        content = if (staffInfo.IsGreatOperation == true) "不适宜" else "适宜"
                    }
                    17 -> {
                        type = 2
                        name = "是否入住："
                        content = if (staffInfo.IsNeedCheckInBefore == true) "是" else "否"
                    }
                    18 -> {
                        type = 2
                        name = "是否乘车："
                        content = if (staffInfo.IsNeedUnifiedRiding == true) "是" else "否"
                    }
                    19 -> {
                        type = 0
                        isLineShow = true
                    }
                }
            })
        }
    }
}
