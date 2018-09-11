package com.xxjr.cfs_system.LuDan.view.activitys.reimbursement_remind

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.adapters.RemindDetailAdapter
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.presenter.RemindDetailsP
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.ToastShow
import com.yanzhenjie.permission.AndPermission
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_common_list.*

class RemindDetailsActivity : BaseActivity<RemindDetailsP, RemindDetailsActivity>(), BaseViewInter {
    private var phone = ""

    override fun getPresenter(): RemindDetailsP = RemindDetailsP()

    override fun getLayoutId(): Int = R.layout.activity_common_list

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    fun getLendingId(): String = intent.getStringExtra("lendingId")

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "客户贷款信息"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        presenter.setDefaultValue()
    }

    fun initRV(commonItems: MutableList<CommonItem<Any>>) {
        rv_remind.layoutManager = LinearLayoutManager(this@RemindDetailsActivity)
        val adapter = RemindDetailAdapter(this@RemindDetailsActivity, commonItems)
        adapter.setOnItemClick(TextChangeListener { position, text ->
            if (!text.isNullOrBlank()) {
                phone = text
                AndPermission.with(this)
                        .requestCode(Constants.REQUEST_CODE_PERMISSION_Phone)
                        .permission(android.Manifest.permission.CALL_PHONE)
                        .callback(presenter.permissioner)
                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                        // 这样避免用户勾选不再提示，导致以后无法申请权限。
                        // 你也可以不设置。
                        .rationale { requestCode, rationale ->
                            // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                            AndPermission.rationaleDialog(this@RemindDetailsActivity, rationale).show()
                        }
                        .start()
            }
        })
        rv_remind.adapter = adapter
    }

    @SuppressLint("MissingPermission")
    fun callPhone() {
        if (phone.isNotBlank()) {
//            if (phone.length == 11) {
//                phoneStr = phone.substring(0, 3) + "-" + phone.substring(3, 7) + "-" + phone.substring(7, 11);
//            }
            CustomDialog.showTwoButtonDialog(this@RemindDetailsActivity, phone, "呼叫", "取消"
            ) { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
                val intent1 = Intent(Intent.ACTION_CALL)
                intent1.data = Uri.parse("tel:$phone")
                startActivity(intent1)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }
}
