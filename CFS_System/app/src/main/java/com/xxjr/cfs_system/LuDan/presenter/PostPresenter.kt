package com.xxjr.cfs_system.LuDan.presenter

import android.content.Context
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xiaoxiao.rxjavaandretrofit.HttpAction
import com.xiaoxiao.rxjavaandretrofit.HttpMethods
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.rxjavaandretrofit.RxBus
import com.xxjr.cfs_system.LuDan.model.modelimp.PostMImp
import com.xxjr.cfs_system.LuDan.view.activitys.PostActivity.Companion.mDataList
import com.xxjr.cfs_system.LuDan.view.viewinter.PostVInter
import com.xxjr.cfs_system.ViewsHolder.PopChoose
import com.xxjr.cfs_system.main.MyApplication
import com.xxjr.cfs_system.tools.BitmapManage
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.Utils
import entity.ChooseType
import rx.Subscriber
import rx.Subscription

class PostPresenter : BasePresenter<PostVInter, PostMImp>() {
    private var isCancelPost = false
    private var progress = 0
    private var uploadSubscription: UploadSubscriber? = null
    private var popWindow: PopupWindow? = null
    private var subscription: Subscription? = null
    private var uploadNum = 0
    private var fileType = -1
    private var fileTypeName = ""

    override fun getModel(): PostMImp = PostMImp()

    override fun setDefaultValue() {
        view.initRV(model.getRVData(view.getCustomer()))
        if (isViewAttached) {
            subscription = RxBus.getInstance().toObservable(1, ChooseType::class.java).subscribe { s ->
                if (popWindow != null && popWindow?.isShowing!!) {
                    popWindow?.dismiss()
                    fileType = s.id
                    fileTypeName = s.content
                    view.refreshItem(1, s.content)
                }
            }
        }
    }

    override fun onSuccess(resultCode: Int, data: ResponseData?) {
    }

    override fun onFailed(resultCode: Int, msg: String?) = view.showMsg(msg)

    fun showDataType(parent: View) {
        if (isViewAttached) {
            if (popWindow == null)
                popWindow = PopChoose.showChooseType(view as Context, parent, "资料类型",
                        Utils.getTypeDataList("ImageDataType"), 1, false)
            else popWindow?.showAtLocation(parent, Gravity.CENTER, 0, 0)
        }
    }

    override fun permissionSuccess(code: Int) {
        when (code) {
            Constants.REQUEST_CODE_PERMISSION_MORE -> view.showIconPop()
        }
    }

    fun getFileType(): Int = fileType

    fun postData() {
        if (uploadSubscription == null) {
            uploadSubscription = UploadSubscriber()
        }
        val image = mDataList[uploadNum]
        val fileByte = BitmapManage.compressImage(if (image.sourcePath.isNullOrBlank()) image.thumbnailPath else image.sourcePath)
        HttpAction.getInstance().upLoadFile(uploadSubscription, HttpMethods.UPLOAD_URL + "/FileService/Upload",
                getBase64("android"), getBase64(view.getCompanyId()), getBase64(getOperParam(fileByte.size.toString())), fileByte)
    }

    private fun getOperParam(size: String): String {
        val userId = Hawk.get<String>("UserID")
        val list = mutableListOf<Any>()
        list.add(view.getCustomer() + fileTypeName + ".png")
        list.add(userId)
        val map = hashMapOf<Any, Any>()
        map.put("TranName", "FS_UpLoanFile")
        map.put("UserID", userId)
        map.put("Marker", "CFSServer")
        val list1 = mutableListOf<Any>()
        list1.add("")
        list1.add(fileType)
        list1.add("")
        list1.add(view.getCustomer() + fileTypeName + ".png")
        list1.add(size)
        list1.add(userId)
        list1.add(view.getCustomerId())
        list1.add(view.getContractId())
        list1.add("")
        map.put("ParamString", list1)
        list.add(JSON.toJSONString(map))
        Logger.e("==上传文件参数==> %s", JSON.toJSONString(list))
        return JSON.toJSONString(list)
    }

    private fun getBase64(str: String): String = Base64.encodeToString(str.toByteArray(), Base64.NO_WRAP)

    fun rxDeAttach() {
        if (popWindow != null) {
            if (popWindow?.isShowing!!) {
                popWindow?.dismiss()
            }
            popWindow = null
        }
        if (subscription != null && !subscription?.isUnsubscribed!!) {
            subscription?.unsubscribe()
        }
    }

    private inner class UploadSubscriber : Subscriber<ResponseData>() {
        override fun onCompleted() {
        }

        override fun onError(e: Throwable) {
            Logger.e("===文件上传错误===>%s", e.message)
            deUploadSubscription()
            uploadNum++
            uploadOver()
        }

        override fun onNext(data: ResponseData) {
            deUploadSubscription()
            if (data.executeResult!!) {
                mDataList.removeAt(uploadNum)
                view.refreshGvItem()
            } else {
                uploadNum++
            }
            uploadOver()
        }
    }

    private fun uploadOver() {
        progress++
        view.setPostProgress(progress)
        if (mDataList.size > uploadNum) {
            if (!isCancelPost) {
                postData()
            }
        } else {
            view.hideBar()
            if (uploadNum > 0) {
                view.showMsg("${uploadNum}张图片上传失败")
                uploadNum = 0
            } else {
                view.postSucceed()
            }
        }
    }

    private fun deUploadSubscription() {
        if (uploadSubscription != null && !uploadSubscription?.isUnsubscribed!!) {
            uploadSubscription?.unsubscribe()
            uploadSubscription = null
        }
    }

    fun setPost() {
        isCancelPost = false
    }

    fun cancelPost() {
        isCancelPost = true
        deUploadSubscription()
    }
}