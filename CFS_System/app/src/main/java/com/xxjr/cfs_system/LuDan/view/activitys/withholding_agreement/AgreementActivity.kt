package com.xxjr.cfs_system.LuDan.view.activitys.withholding_agreement

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.adapters.AgreementAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.LuDan.presenter.AgreementPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.ScanCamera
import com.xxjr.cfs_system.LuDan.view.activitys.SignActivity
import com.xxjr.cfs_system.LuDan.view.viewinter.AgreementVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.CommonItem
import kotlinx.android.synthetic.main.activity_agreement.*

class AgreementActivity : BaseActivity<AgreementPresenter, AgreementVInter>(), AgreementVInter {
    var adapter: AgreementAdapter? = null

    override fun showMsg(msg: String?) = ToastShow.showShort(application, msg)

    override fun getPresenter(): AgreementPresenter = AgreementPresenter()

    override fun getLayoutId(): Int = R.layout.activity_agreement

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "新增代扣"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        presenter.setDefaultValue()
        next.setOnClickListener {
            presenter.toNext()
        }
    }

    override fun initRV(commonItems: MutableList<CommonItem<Any>>) {
        rv_agree.layoutManager = LinearLayoutManager(this@AgreementActivity)
        adapter = AgreementAdapter(this@AgreementActivity, commonItems)
        adapter?.setOnItemClick(RecycleItemClickListener { position ->
            presenter.clickChoose(position, rv_agree)
        })

        adapter?.setTextChangeListener(TextChangeListener { position, text ->
            presenter.editContent(position, text)
        })
        rv_agree.adapter = adapter
    }

    override fun refreshItem(position: Int, text: String) {
        val item: CommonItem<*> = adapter?.datas?.get(position) as CommonItem<*>
        item.content = text
        adapter?.notifyItemChanged(position, item)
    }

    override fun scanCard() {
        val intent = Intent(this@AgreementActivity, ScanCamera::class.java)
        startActivityForResult(intent, 55)
    }

    /**
     * @param isConfirm 是否需要调用确认接口
     * @param telphone 发送验证码手机号
     * @param signCode 协议编码
     * @param aisleType 平台类型
     * @param isSend 是否已发送验证短信1->已发送，0->需要重新发送
     */
    override fun complete(isConfirm: Boolean, telphone: String, signCode: String, aisleType: String, isSend: String) {
        if (isConfirm) {
            val intent = Intent(this@AgreementActivity, SignActivity::class.java)
            intent.putExtra("telphone", telphone)
            intent.putExtra("signCode", signCode)
            intent.putExtra("aisleType", aisleType)
            intent.putExtra("isSend", isSend)
            startActivityForResult(intent, 66)
        } else {
            showMsg("签约完成")
            setResult(11)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 66) {
            setResult(11)
            finish()
        } else if (requestCode == 55) {
            //添加完成刷新数据
            val resultCard = data?.getStringExtra("resultCard")
            if (resultCard.isNullOrBlank()) {
                //showMsg("扫描失败!")
            } else {
                val imm = this@AgreementActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(rv_agree.windowToken, 0)
                presenter.setAccountNo(resultCard ?: "")
                if (rv_agree.scrollState == RecyclerView.SCROLL_STATE_IDLE || !rv_agree.isComputingLayout) {
                    refreshItem(6, resultCard ?: "")
                }
            }
        }
    }

    override fun onDestroy() {
        presenter.rxDeAttach()
        super.onDestroy()
    }
}
