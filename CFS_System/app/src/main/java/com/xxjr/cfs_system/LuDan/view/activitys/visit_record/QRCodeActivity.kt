package com.xxjr.cfs_system.LuDan.view.activitys.visit_record

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.text.Html
import android.view.View
import com.orhanobut.hawk.Hawk
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.activitys.visit_record.presenter.QRCodePresenter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.BitmapManage
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.ToastShow
import com.xxjr.cfs_system.tools.Utils
import kotlinx.android.synthetic.main.activity_qrcode.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class QRCodeActivity : BaseActivity<QRCodePresenter, QRCodeActivity>(), BaseViewInter {
    var codeWith = 0
    var myUrl = ""
    private var mBitmap: Bitmap? = null

    override fun getPresenter(): QRCodePresenter = QRCodePresenter()

    override fun getLayoutId(): Int = R.layout.activity_qrcode

    override fun showMsg(msg: String?) = ToastShow.showShort(applicationContext, msg)

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "我的二维码"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        codeWith = Utils.getScreenWidth(this@QRCodeActivity) * 7 / 10
        presenter.setDefaultValue()
        ivRight.setImageResource(R.mipmap.share)
        shareShow()
    }

    fun createQRCode(myUrl: String) {
        this.myUrl = myUrl
        if (myUrl.isNotBlank()) {
            mBitmap = CodeUtils.createImage(myUrl, codeWith, codeWith, BitmapFactory.decodeResource(resources, R.mipmap.logo))
            if (mBitmap != null) {
                iv_qr.setImageBitmap(mBitmap)
                tv_share.visibility = View.VISIBLE
            }
        }
    }

    private fun shareShow() {
        ivRight.setOnClickListener {
            if (myUrl.isNotBlank() && mBitmap != null) {
                shareText()
            }
        }
        tv_share.setOnClickListener {
            if (myUrl.isNotBlank() && mBitmap != null) {
                shareText()
            }
        }
    }

    private fun shareText() {
        Observable.create(Observable.OnSubscribe<String> {
            val bitmap = captureScreen()
            val file = File(Environment.getExternalStorageDirectory(), "/CFS/cache")
            var locPath = ""
            if (file.exists() || file.mkdirs()) {
                if (bitmap != null) {
                    locPath = Constants.QRPath
                    BitmapManage.savePhotoToSDCard(bitmap, locPath)
                }
            }
            it.onNext(locPath)
        })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe {
                    if (it.isNotBlank()) {
                        val share_intent = Intent()
                        share_intent.action = Intent.ACTION_SEND//设置分享行为
                        share_intent.type = "image/*"  //设置分享内容的类型
                        if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
                            share_intent.putExtra(Intent.EXTRA_STREAM,
                                    FileProvider.getUriForFile(this@QRCodeActivity, "com.xiaoxiao.ludan.fileprovider", File(it)))
                        } else {
                            share_intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(it)))
                        }
                        share_intent.putExtra(Intent.EXTRA_SUBJECT, "来访登记")
                        share_intent.putExtra(Intent.EXTRA_TEXT, "来访登记")
                        share_intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(share_intent)
                    } else {
                        showMsg("分享失败!")
                    }
                }
//        //文本分享
//        if (content == null || "" == content) {
//            return
//        }
//        val intent = Intent(Intent.ACTION_SEND)
//        intent.type = "text/plain"
//        if (subject != null && "" != subject) {
//            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
//        }
//        intent.putExtra(Intent.EXTRA_TEXT, content)
//        startActivity(intent)
    }

    //截屏
    private fun captureScreen(): Bitmap? {
        val cv = this@QRCodeActivity.window.decorView
        cv.isDrawingCacheEnabled = true
        cv.buildDrawingCache()
        val bmp = cv.drawingCache ?: return null
        bmp.setHasAlpha(false)
        bmp.prepareToDraw()
        return bmp
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    override fun onDestroy() {
        mBitmap = null
        super.onDestroy()
    }
}
