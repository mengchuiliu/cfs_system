package com.xxjr.cfs_system.LuDan.view.activitys.staff_training

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TrainingDetailAdapter
import com.xxjr.cfs_system.LuDan.presenter.TrainingDetailPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.CFSUtils
import com.xxjr.cfs_system.tools.ToastShow
import entity.ChooseType
import entity.CommonItem
import entity.StaffInfo
import kotlinx.android.synthetic.main.activity_agreement.*
import java.io.Serializable

class TrainingDetailActivity : BaseActivity<TrainingDetailPresenter, TrainingDetailActivity>(), BaseViewInter {
    private lateinit var adapter: TrainingDetailAdapter
    private val permits = CFSUtils.getPostPermitValue(Hawk.get("PermitValue", ""), "823")

    fun getTrainingId() = intent.getStringExtra("TrainingId") ?: ""

    override fun getPresenter(): TrainingDetailPresenter = TrainingDetailPresenter()

    override fun getLayoutId(): Int = R.layout.activity_agreement

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "培训详情"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        next.text = "立即报名"
        next.visibility = View.GONE
        next.setOnClickListener {
            val intent = Intent(this@TrainingDetailActivity, SignUpActivity::class.java)
            intent.putExtra("projects", ((adapter.datas[3] as CommonItem<Any>).list
                    ?: mutableListOf<ChooseType>()) as Serializable)
            intent.putExtra("trainingId", getTrainingId())
            startActivityForResult(intent, 99)
        }
        initRV()
    }

    private fun initRV() {
        rv_agree.layoutManager = LinearLayoutManager(this@TrainingDetailActivity)
        adapter = TrainingDetailAdapter(this@TrainingDetailActivity, presenter.getItemData(JSONObject()))
        adapter.setOnItemClick(RecycleItemClickListener {
            val intent = Intent(this@TrainingDetailActivity, StaffListActivity::class.java)
            intent.putExtra("title", (adapter.datas[0] as CommonItem<Any>).name ?: "")
            intent.putExtra("CategoryTitle", (adapter.datas[2] as CommonItem<Any>).content ?: "")
            intent.putExtra("projects", ((adapter.datas[3] as CommonItem<Any>).list
                    ?: mutableListOf<ChooseType>()) as Serializable)
            intent.putExtra("SignUpDeadline", (adapter.datas[5] as CommonItem<Any>).content ?: "")
            intent.putExtra("trainingId", getTrainingId())
            intent.putExtra("contractType", 230)
            startActivityForResult(intent, 99)
        })
        adapter.setOnDetailClick(RecycleItemClickListener {
            val intent = Intent(this@TrainingDetailActivity, StaffInfoActivity::class.java)
            intent.putExtra("title", (adapter.datas[0] as CommonItem<Any>).name)
            intent.putExtra("CategoryTitle", (adapter.datas[2] as CommonItem<Any>).content)
            intent.putExtra("staffInfo", ((adapter.datas[it] as CommonItem<Any>).item) as StaffInfo)
            intent.putExtra("projects", ((adapter.datas[3] as CommonItem<Any>).list
                    ?: mutableListOf<ChooseType>()) as Serializable)
            intent.putExtra("SignUpDeadline", (adapter.datas[5] as CommonItem<Any>).content ?: "")
            startActivityForResult(intent, 99)
        })
        rv_agree.adapter = adapter
    }

    fun setApplyVisible(state: String) {
        if (permits != null && permits.contains("EH") && state == "0") next.visibility = View.VISIBLE
        else next.visibility = View.GONE
    }

    fun refreshData(detail: MutableList<CommonItem<Any>>) {
        adapter.setNewData(detail as List<Any>?)
    }

    override fun onResume() {
        super.onResume()
        presenter.setDefaultValue()
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 99) {
        }
    }
}
