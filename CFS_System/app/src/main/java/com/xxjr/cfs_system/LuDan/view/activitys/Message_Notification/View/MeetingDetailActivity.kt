package com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.View

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.Presenter.MeetingDetailPresenter
import com.xxjr.cfs_system.LuDan.view.activitys.WebActivity
import com.xxjr.cfs_system.LuDan.view.post_image.FileDisplayActivity
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import entity.PactData
import kotlinx.android.synthetic.main.activity_meeting_detail.*
import rvadapter.BaseViewHolder
import rvadapter.CommonAdapter

class MeetingDetailActivity : BaseActivity<MeetingDetailPresenter, MeetingDetailActivity>(), BaseViewInter {
    private lateinit var adapter: CommonAdapter<PactData>

    override fun getPresenter(): MeetingDetailPresenter = MeetingDetailPresenter()

    override fun getLayoutId(): Int = R.layout.activity_meeting_detail

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    fun getNotifyId() = intent.getIntExtra("NotifyId", -1)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = intent.getStringExtra("CategoryTitle") ?: ""
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        initRV()
        val IsReadFb = intent.getBooleanExtra("IsReadFb", false)//是否需要反馈
        val IsReadConfirm = intent.getBooleanExtra("IsReadConfirm", false)//false->未反馈
        tv_confirm.visibility = if (IsReadFb) View.VISIBLE else View.GONE
        tv_read.visibility = if (IsReadFb && !IsReadConfirm) View.VISIBLE else View.GONE
        tv_confirm.isEnabled = !IsReadConfirm
        tv_confirm.setOnClickListener {
            presenter.readFeedMessage()
        }
        presenter.setDefaultValue()
    }

    private fun initRV() {
        recycle_accessory.layoutManager = LinearLayoutManager(this@MeetingDetailActivity)
        adapter = object : CommonAdapter<PactData>(this@MeetingDetailActivity, arrayListOf(), R.layout.item_message_file) {
            override fun convert(holder: BaseViewHolder, item: PactData, position: Int) {
                holder.setText(R.id.tv_file, item.fileName)
                holder.setOnClickListener(R.id.tv_file) {
                    when (getFileType(item.fileName ?: "")) {
                        "jpg", "JPG", "png", "PNG", "GIF", "gif", "JPEG", "jpeg" -> {
                            showFile(getFileType(item.fileName ?: ""), item.fileGuid
                                    ?: "", WebActivity::class.java)
                        }
                        "doc", "excel", "ppt", "txt", "pdf", "pptx", "docx", "xlsx", "xls", "wps" -> {
                            showFile(getFileType(item.fileName ?: ""), item.fileGuid
                                    ?: "", FileDisplayActivity::class.java)
                        }
                        else -> showMsg("手机暂不支持查看此类文件，请在电脑上查看")
                    }
                }
            }
        }
        recycle_accessory.adapter = adapter
    }

    fun setTitle(title: String) {
        tv_title.text = title
    }

    fun setMessageContent(content: String) {
        tv_content.text = content
    }

    fun setConfirmEnable() {
        tv_confirm.isEnabled = false
    }

    fun refreshFiles(files: MutableList<PactData>) {
        if (files.isEmpty()) ll_file.visibility = View.GONE else ll_file.visibility = View.VISIBLE
        adapter.setNewData(files)
    }

    private fun showFile(name: String, fileGuid: String, clazz: Class<*>) {
        val intent2 = Intent(this@MeetingDetailActivity, clazz)
        intent2.putExtra("isRead", 0)
        intent2.putExtra("contractId", "0")
        intent2.putExtra("fileID", "0")
        intent2.putExtra("fileGuid", fileGuid)
        intent2.putExtra("fileType", name)
        startActivity(intent2)
    }

    //文件类型
    private fun getFileType(fileName: String): String {
        var str = ""
        if (TextUtils.isEmpty(fileName)) {
            return str
        }
        val i = fileName.lastIndexOf('.')
        if (i <= -1) {
            return str
        }
        str = fileName.substring(i + 1)
        return str
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }
}
