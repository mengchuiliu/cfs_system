package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.orhanobut.hawk.Hawk

import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.AddBankAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.RecyclerItemCheck
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.presenter.AddBankPresenter
import com.xxjr.cfs_system.LuDan.view.viewinter.AddBankVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.ToastShow
import com.yanzhenjie.permission.AndPermission
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_add_bank.*

class AddBankActivity : BaseActivity<AddBankPresenter, AddBankVInter>(), AddBankVInter, View.OnClickListener {
    private var adapter: AddBankAdapter? = null
    private var name = Hawk.get("UserRealName") ?: ""
    private var bankCode = ""
    private var idCard = ""
    private var branch = ""
    private var isDefault = false

    override fun getPresenter(): AddBankPresenter = AddBankPresenter()

    override fun getLayoutId(): Int = R.layout.activity_add_bank

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun isShowBacking(): Boolean = true

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "添加银行卡"
        tv_complete.setOnClickListener(this)
        presenter.setDefaultValue()
    }

    override fun initRecycler(mutableList: MutableList<CommonItem<Any>>) {
        rv_add_bank.layoutManager = LinearLayoutManager(this@AddBankActivity)
        adapter = AddBankAdapter(this@AddBankActivity, mutableList)
        adapter?.setTextChangeListener(TextChangeListener { position, text ->
            when (position) {
                0 -> name = text
                1 -> bankCode = text
                2 -> branch = text
                3 -> idCard = text
            }
        })
        adapter?.setOnItemCheck(object : RecyclerItemCheck {
            override fun onItemCheck(isCheck: Boolean) {
                isDefault = isCheck
            }
        })
        adapter?.setOnItemScanClick(RecycleItemClickListener {
            AndPermission.with(this)
                    .requestCode(Constants.REQUEST_CODE_PERMISSION_Camera)
                    .permission(android.Manifest.permission.CAMERA)
                    .callback(presenter.permissioner)
                    // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                    // 这样避免用户勾选不再提示，导致以后无法申请权限。
                    // 你也可以不设置。
                    .rationale { requestCode, rationale ->
                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(this@AddBankActivity, rationale).show()
                    }
                    .start()
        })
        rv_add_bank.adapter = adapter
    }

    override fun onClick(p0: View?) {
        when (p0) {
            tv_complete -> {
                presenter.postBankInfo(name, bankCode, branch, idCard, isDefault)
            }
        }
    }

    override fun addComplete() {
        if (intent.getBooleanExtra("isCheckAdd", false)) {
            startActivity(BindBankActivity::class.java)
        } else {
            setResult(Constants.RESULT_CODE_ADD_BANK)
        }
        this.finish()
    }

    override fun scanCard() {
        val intent = Intent(this@AddBankActivity, ScanCamera::class.java)
        startActivityForResult(intent, 55)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 5555) {
            //添加完成刷新数据
            val resultCard = data?.getStringExtra("resultCard")
            if (resultCard.isNullOrBlank()) {
                //showMsg("扫描失败!")
            } else {
                refreshItem(1, resultCard ?: "")
                bankCode = resultCard ?: ""
            }
        }
    }

    private fun refreshItem(pos: Int, content: String) {
        val item: CommonItem<*> = adapter?.datas?.get(pos) as CommonItem<*>
        item.content = content
        adapter?.notifyItemChanged(pos, item)
    }
}
