package com.xxjr.cfs_system.LuDan.view.activitys

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.PopWindow
import com.xxjr.cfs_system.LuDan.adapters.PostAdapter
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.LuDan.presenter.PostPresenter
import com.xxjr.cfs_system.LuDan.view.post_image.AlbumChooseActivity
import com.xxjr.cfs_system.LuDan.view.post_image.ImageZoomActivity
import com.xxjr.cfs_system.LuDan.view.viewinter.PostVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.OpenFileUtils
import com.xxjr.cfs_system.tools.ToastShow
import com.yanzhenjie.permission.AndPermission
import entity.ChooseType
import entity.ClientInfo
import entity.CommonItem
import entity.ImageInfo
import kotlinx.android.synthetic.main.activity_add_pact.*
import java.io.File
import java.io.Serializable
import java.util.ArrayList

class PostActivity : BaseActivity<PostPresenter, PostVInter>(), PostVInter {
    companion object {
        var mDataList: MutableList<ImageInfo> = ArrayList()
    }

    var progressDialog: ProgressDialog? = null

    private val TAKE_PICTURE = 0x000000//照相
    private var photoSaveName = ""//照相路径

    private var iconPopupWindow: PopupWindow? = null
    private var adapter: PostAdapter? = null
    private var customer = ""
    private var customerId = ""
    private var infos: List<ClientInfo>? = null

    override fun getPresenter(): PostPresenter = PostPresenter()

    override fun getCustomer(): String = customer

    override fun getCustomerId(): String = customerId

    override fun getContractId(): Int = intent.getIntExtra("contractId", 0)

    override fun getCompanyId(): String = intent.getStringExtra("CompanyID")

    override fun getLayoutId(): Int = R.layout.activity_add_pact

    override fun initView(savedInstanceState: Bundle?) {
        mDataList.clear()
        toolbarTitle.text = "上传资料"
        subTitle.text = " 上传"
        subTitle.setOnClickListener {
            when {
                getCustomerId().isBlank() -> {
                    showMsg("请选择客户!")
                    return@setOnClickListener
                }
                presenter.getFileType() == -1 -> {
                    showMsg("请选择文件类型!")
                    return@setOnClickListener
                }
                mDataList.size == 0 -> {
                    showMsg("请选择上传的文件!")
                    return@setOnClickListener
                }
            }
            showProgressDialog(mDataList.size)
            presenter.setPost()
            presenter.postData()
        }
        infos = intent.getSerializableExtra("CustomerInfo") as? List<ClientInfo>
        if (infos != null && infos?.size == 1) {
            customer = infos!![0].name
            customerId = infos!![0].userID.toString()
        }
        presenter.setDefaultValue()
    }

    fun showProgressDialog(max: Int) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this@PostActivity)
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

    override fun setPostProgress(progress: Int) {
        progressDialog?.progress = progress
    }

    override fun hideBar() {
        if (progressDialog != null && progressDialog?.isShowing!!) {
            progressDialog?.dismiss()
            progressDialog?.cancel()
        }
    }

    override fun postSucceed() {
        showMsg("上传成功")
        setResult(99)
        this@PostActivity.finish()
    }

    override fun initRV(commonItems: MutableList<CommonItem<Any>>) {
        recycle_add_pact.layoutManager = object : LinearLayoutManager(this@PostActivity) {
            override fun canScrollVertically(): Boolean = false
        }
        adapter = PostAdapter(this@PostActivity, commonItems)
        adapter?.setOnItemClick(RecycleItemClickListener { position ->
            when (position) {
                0 -> {
                    val intent = Intent(this@PostActivity, CustomerActivity::class.java)
                    intent.putExtra("CustomerInfo", infos as Serializable)
                    intent.putExtra("isOne", true)
                    startActivityForResult(intent, 1)
                }
                1 -> presenter.showDataType(recycle_add_pact)
            }
        })

        adapter?.setOnGvItemClick(RecycleItemClickListener { position ->
            if (position == mDataList.size) {
                getPermissions()
            } else {
                val intent = Intent(this@PostActivity, ImageZoomActivity::class.java)
                intent.putExtra("position", position)
                startActivityForResult(intent, 2)
            }
        })

        adapter?.setOnDelItemClick(RecycleItemClickListener { position ->
            mDataList.removeAt(position)
            refreshGvItem()
        })
        recycle_add_pact.adapter = adapter
    }

    override fun refreshItem(position: Int, text: String) {
        val item: CommonItem<*> = adapter?.datas?.get(position) as CommonItem<*>
        item.content = text
        adapter?.notifyItemChanged(position, item)
    }

    override fun refreshGvItem() {
        val item: CommonItem<*> = adapter?.datas?.get(2) as CommonItem<*>
        item.list = mDataList
        adapter?.notifyItemChanged(2, item)
    }

    override fun showIconPop() {
        if (iconPopupWindow == null) {
            iconPopupWindow = PopWindow.choosePortrait(this@PostActivity, onClickListener, recycle_add_pact)
        } else {
            iconPopupWindow!!.showAtLocation(recycle_add_pact, Gravity.CENTER, 0, 0)
        }
    }

    override fun isShowBacking(): Boolean = true

    override fun showMsg(msg: String?) = ToastShow.showShort(application, msg)

    //更换头像需要的权限申请
    private fun getPermissions() {
        AndPermission.with(this)
                .requestCode(Constants.REQUEST_CODE_PERMISSION_MORE)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .callback(presenter.permissioner)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale { requestCode, rationale ->
                    // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                    AndPermission.rationaleDialog(this@PostActivity, rationale).show()
                }
                .start()
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
                val imageUri = OpenFileUtils.getUri(this@PostActivity, File(photoSavePath, photoSaveName))
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(openCameraIntent, TAKE_PICTURE)
            }
            R.id.albums -> {
                val intent = Intent(this@PostActivity, AlbumChooseActivity::class.java)
                intent.putExtra("AvailableSize", getAvailableSize())
                startActivityForResult(intent, 88)
            }
            R.id.cancel -> iconPopupWindow?.dismiss()
        }
    }

    //获取可上传图片张数
    private fun getAvailableSize(): Int {
        val availSize = Constants.MAX_IMAGE_SIZE - mDataList.size
        return if (availSize >= 0) {
            availSize
        } else 0
    }

    private fun getPhotoSavePath(): String? {
        val file = File(Environment.getExternalStorageDirectory(), "/DCIM/Camera")
        return if (file.exists() || file.mkdir()) {
            file.path + File.separator
        } else null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == 11) {
            val chooseType = data?.getSerializableExtra("chooseType") as? ChooseType
            if (chooseType != null) {
                customerId = chooseType.ids
                customer = chooseType.content
                refreshItem(0, chooseType.content)
            }
        }
        if (requestCode == TAKE_PICTURE) {
            val path = getPhotoSavePath()!! + photoSaveName
            if (mDataList.size < Constants.MAX_IMAGE_SIZE && resultCode == -1 && !TextUtils.isEmpty(path)) {
                val item = ImageInfo()
                item.sourcePath = path
                mDataList.add(item)
            }
            refreshGvItem()
        }
        if (resultCode == 6) {
            if (data != null) {
                val incomingDataList = data.getSerializableExtra("image_list") as? List<ImageInfo>
                if (incomingDataList != null) {
                    mDataList.addAll(incomingDataList)
                }
                refreshGvItem()
            }
        }
        if (requestCode == 2 && resultCode == 22) {
            refreshGvItem()
        }
    }

    override fun onDestroy() {
        if (iconPopupWindow != null && iconPopupWindow?.isShowing!!) {
            iconPopupWindow?.dismiss()
            iconPopupWindow = null
        }
        presenter.rxDeAttach()
        super.onDestroy()
    }
}
