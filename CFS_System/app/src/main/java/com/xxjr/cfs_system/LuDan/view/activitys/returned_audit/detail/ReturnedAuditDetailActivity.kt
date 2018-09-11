package com.xxjr.cfs_system.LuDan.view.activitys.returned_audit.detail

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import com.alibaba.fastjson.JSONArray
import com.bigkoo.pickerview.TimePickerView
import com.bigkoo.pickerview.listener.CustomListener
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.PopWindow
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.adapters.RemindDetailAdapter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.returned_audit.attachment.AttachmentAdapter
import com.xxjr.cfs_system.LuDan.view.activitys.returned_audit.dialog.BaseNormalDialog
import com.xxjr.cfs_system.LuDan.view.post_image.AlbumChooseActivity
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.OpenFileUtils
import com.xxjr.cfs_system.tools.ToastShow
import com.yanzhenjie.permission.AndPermission
import entity.CommonItem
import entity.ImageInfo
import kotlinx.android.synthetic.main.activity_retured_audit_detial.*
import java.io.File
import java.util.*


/**
 * 回款审核详情   界面
 * @author huangdongqiang
 * @date 27/06/2018
 */
class ReturnedAuditDetailActivity : BaseActivity<ReturnedAuditDetailLP, ReturnedAuditDetailActivity>(), BaseViewInter, View.OnClickListener {


    private lateinit var adapter: RemindDetailAdapter
    private lateinit var adapter1: RemindDetailAdapter
    private lateinit var adapter2: RemindDetailAdapter
    private lateinit var attachmentAdapter: AttachmentAdapter
    private var mPvTime: TimePickerView? = null
    private var iconPopupWindow: PopupWindow? = null
    private var photoSaveName = ""//照相路径
    private val TAKE_PICTURE = 0x000000//照相
    //var uploadFileList: MutableList<ImageInfo> = ArrayList()
    var progressDialog: ProgressDialog? = null

    override fun getPresenter(): ReturnedAuditDetailLP {
        return ReturnedAuditDetailLP()
    }

    override fun isShowBacking(): Boolean = true

    override fun getLayoutId(): Int {
        return R.layout.activity_retured_audit_detial
    }

    override fun showMsg(msg: String?) {
        ToastShow.showShort(this, msg)
    }

    fun showShort(resId: Int) {
        ToastShow.showShort(this, resId)
    }

    override fun initView(savedInstanceState: Bundle?) {
        presenter.getDefaultIntent(intent)
        presenter.setDefaultValue()
        toolbarTitle.text = getString(R.string.returned_audit_detail_title)

        initWaitForCheckLayout()
    }

    private fun initWaitForCheckLayout() {
        tv_ok.text = getText(R.string.base_pass)
        tv_not.text = getText(R.string.base_reject)
        tv_ok.setOnClickListener(this)
        tv_not.setOnClickListener(this)
        tv_withhold.setOnClickListener(this)
    }

    fun initRecyclerView() {
        //回款信息
        recycler_view.layoutManager = object : LinearLayoutManager(this@ReturnedAuditDetailActivity) {
            override fun canScrollVertically(): Boolean = false
        }
        adapter = RemindDetailAdapter(this@ReturnedAuditDetailActivity, presenter.getItemData(false))
        adapter.setOnItemShrink(RecycleItemClickListener { _ ->
            val isShrink = (adapter.datas[0] as CommonItem<*>).isClick
            adapter.setNewData(presenter.getItemData(!isShrink) as List<*>)
        })
        recycler_view.adapter = adapter

        //贷款信息
        recycler_view_1.layoutManager = object : LinearLayoutManager(this@ReturnedAuditDetailActivity) {
            override fun canScrollVertically(): Boolean = false
        }
        adapter1 = RemindDetailAdapter(this@ReturnedAuditDetailActivity, presenter.getItemData1(true, presenter.jsonArray))
        adapter1.setOnItemShrink(RecycleItemClickListener { _ ->
            val isShrink = (adapter1.datas[0] as CommonItem<*>).isClick
            adapter1.setNewData(presenter.getItemData1(!isShrink, presenter.jsonArray) as List<*>)
        })
        recycler_view_1.adapter = adapter1

        //审核信心和代扣金账户信息
        recycler_view_2.layoutManager = object : LinearLayoutManager(this@ReturnedAuditDetailActivity) {
            override fun canScrollVertically(): Boolean = false
        }
        adapter2 = RemindDetailAdapter(this@ReturnedAuditDetailActivity, presenter.getItemData2(true, true))
        adapter2.setOnItemShrink(RecycleItemClickListener { position ->
            val isShrink = (adapter2.datas[0] as CommonItem<*>).isClick
            val isShrinkSecond = if (adapter2.datas.size > 10) (adapter2.datas[10] as CommonItem<*>).isClick else false
            when (position) {
                0 -> adapter2.setNewData(presenter.getItemData2(!isShrink, isShrinkSecond) as List<*>)
                10 -> adapter2.setNewData(presenter.getItemData2(isShrink, !isShrinkSecond) as List<*>)
            }
        })
        recycler_view_2.adapter = adapter2
    }

    /**
     * 附件布局
     */
    fun initAttachmentView() {
        rv_attachment.layoutManager = object : LinearLayoutManager(this@ReturnedAuditDetailActivity) {
            override fun canScrollVertically(): Boolean = false
        }
        attachmentAdapter = AttachmentAdapter(this@ReturnedAuditDetailActivity, presenter.getAttachmentItemData(true))
        attachmentAdapter.setOnItemShrink(RecycleItemClickListener { position ->
            val isShrink = (attachmentAdapter.datas[0] as CommonItem<*>).isClick
            val uploadBtnPosition = if (attachmentAdapter.datas.size > 0) attachmentAdapter.datas.size - 1 else 0
            when (position) {
                0 -> attachmentAdapter.setNewData(presenter.getAttachmentItemData(!isShrink) as List<*>)
                uploadBtnPosition -> {
                    presenter.clickUploadFile()
                }
            }
        })
        attachmentAdapter.setOnItemDelete(object : AttachmentAdapter.OnItemDeleteListener {
            override fun onItemDelete(position: Int) {
                val deleteDialog = BaseNormalDialog(this@ReturnedAuditDetailActivity)
                deleteDialog.setContentText(R.string.returned_audit_delete_dialog_content)
                deleteDialog.setOnConfirmClickListener {
                    //position -  1 ,减去标题
                    presenter.clickDeleteFile(position - 1)
                }
                deleteDialog.show()
            }
        })
        rv_attachment.adapter = attachmentAdapter
    }

    fun removeAttachmentData(position: Int) {
        attachmentAdapter.remove(position)
        attachmentAdapter.notifyDataSetChanged()
    }

    /**
     * 设置新数据
     */
    fun setLoanNewData(data: List<CommonItem<Any>>) {
        adapter1.setNewData(data)
    }

    /**
     * 设置新数据
     */
    fun setNewData(data: List<CommonItem<Any>>) {
        adapter2.setNewData(data)
    }

    /**
     * 设置附件新数据
     */
    fun setNewAttachmentData(data: List<CommonItem<Any>>) {
        attachmentAdapter.setNewData(data)
    }

    /**
     * 设置审核通过布局显示与否
     */
    fun setWaitForCheckLayoutVisibility(visibility: Int) {
        ll_wait_for_check?.visibility = visibility
    }

    /**
     * 代扣按钮是否显示
     */
    fun setWithholdLayoutVisibility(visibility: Int) {
        tv_withhold?.visibility = visibility
    }

    override fun onClick(p0: View?) {
        when (p0) {
            tv_not -> {
                presenter.clickPassOrRejectBtn(false)
            }
            tv_ok -> {
                presenter.clickPassOrRejectBtn(true)
            }
            tv_withhold -> {
                presenter.getWithholdAction()
            }
        }
    }

    /**
     * 显示时间选择对话框
     */
    fun showCalendarDialog(selectedData: Calendar) {
        if (null == mPvTime) {
            val startDate = Calendar.getInstance()
            val endDate = Calendar.getInstance()
            startDate.set(1900, 1, 1)
            mPvTime = TimePickerView.Builder(this, TimePickerView.OnTimeSelectListener { date, v ->
                presenter.getCheckAction(true, date)
                //mPresenter.onTimeSelect(date)
            })
                    .setLayoutRes(R.layout.dialog_returned_audit_picker_time, CustomListener { v ->
                        val tvSubmit = v.findViewById(R.id.tv_finish) as TextView
                        val ivCancel = v.findViewById(R.id.tv_cancel) as TextView
                        tvSubmit.setOnClickListener(View.OnClickListener {
                            mPvTime?.dismiss()
                            mPvTime?.returnData()
                        })
                        ivCancel.setOnClickListener(View.OnClickListener {
                            //pvCustomTime.dismiss()
                            mPvTime?.dismiss()
                        })
                    })
                    .setRangDate(startDate, endDate)
                    .setDate(selectedData)
                    .setLineSpacingMultiplier(2F)
                    .setSubmitColor(ContextCompat.getColor(this, R.color.colorAccent))
                    .setCancelColor(ContextCompat.getColor(this, R.color.colorAccent))
                    .setType(booleanArrayOf(true, true, true, false, false, false))
                    .build()
        }
        mPvTime?.show()
    }

    //更换头像需要的权限申请
    fun getPermissions() {
        AndPermission.with(this)
                .requestCode(Constants.REQUEST_CODE_PERMISSION_MORE)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .callback(presenter.permissioner)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale { requestCode, rationale ->
                    // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                    AndPermission.rationaleDialog(this@ReturnedAuditDetailActivity, rationale).show()
                }
                .start()
    }

    fun showIconPop() {
        if (iconPopupWindow == null) {
            iconPopupWindow = PopWindow.choosePortrait(this@ReturnedAuditDetailActivity, onClickListener, rv_attachment)
        } else {
            iconPopupWindow!!.showAtLocation(rv_attachment, Gravity.CENTER, 0, 0)
        }
    }

    private var onClickListener: View.OnClickListener = View.OnClickListener { view ->
        if (iconPopupWindow != null && iconPopupWindow?.isShowing!!) {
            iconPopupWindow?.dismiss()
        }
        when (view.id) {
            R.id.photograph -> {
                photoSaveName = System.currentTimeMillis().toString() + ".jpg"
                val openCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0)
                val photoSavePath = getPhotoSavePath()
                // 下面这句指定调用相机拍照后的照片存储的路径
                val imageUri = OpenFileUtils.getUri(this@ReturnedAuditDetailActivity, File(photoSavePath, photoSaveName))
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(openCameraIntent, TAKE_PICTURE)
            }
            R.id.albums -> {
                val intent = Intent(this@ReturnedAuditDetailActivity, AlbumChooseActivity::class.java)
                intent.putExtra("AvailableSize", presenter.getAvailableSize())
                startActivityForResult(intent, 88)
            }
            R.id.cancel -> iconPopupWindow?.dismiss()
        }
    }

    private fun getPhotoSavePath(): String? {
        val file = File(Environment.getExternalStorageDirectory(), "/DCIM/Camera")
        return if (file.exists() || file.mkdir()) {
            file.path + File.separator
        } else null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == TAKE_PICTURE) {
            val path = getPhotoSavePath()!! + photoSaveName
            if (presenter.getAttachmentDataList().size < Constants.MAX_IMAGE_SIZE && resultCode == -1 && !TextUtils.isEmpty(path)) {
                val item = ImageInfo()
                item.sourcePath = path
                presenter.uploadFile(mutableListOf<ImageInfo>().apply {
                    add(item)
                })
            }

        } else if (resultCode == 6) {
            if (data != null) {
                val incomingDataList = data.getSerializableExtra("image_list") as? List<ImageInfo>
                if (incomingDataList != null && incomingDataList.isNotEmpty()) {
                    presenter.uploadFile(incomingDataList as MutableList<ImageInfo>)
                }

            }
        }
    }

    fun setPostProgress(progress: Int) {
        progressDialog?.progress = progress
    }

    fun showProgressDialog(max: Int) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this@ReturnedAuditDetailActivity)
            progressDialog?.progress = 0
            progressDialog?.setTitle("文件上传中...")
            progressDialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progressDialog?.max = max
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.setOnCancelListener {
                progressDialog?.dismiss()
                presenter.cancelPost()
            }
        }
        progressDialog?.show()
    }

    fun hideBar() {
        if (progressDialog != null && progressDialog?.isShowing!!) {
            progressDialog?.dismiss()
            progressDialog?.cancel()
        }
    }
}