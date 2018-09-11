package com.xxjr.cfs_system.LuDan.view.activitys

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.HttpMethods
import com.xiaoxiao.widgets.CustomDialog
import com.xxjr.cfs_system.LuDan.presenter.WebPresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.LuDan.view.viewinter.WebVInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.Constants
import com.xxjr.cfs_system.tools.OpenFileUtils
import com.xxjr.cfs_system.tools.ToastShow
import kotlinx.android.synthetic.main.activity_web.*
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class WebActivity : BaseActivity<WebPresenter, WebVInter>(), BaseViewInter {
    var audit = false

    override fun showMsg(msg: String?) = ToastShow.showShort(application, msg)

    override fun getPresenter(): WebPresenter = WebPresenter()

    override fun getLayoutId(): Int = R.layout.activity_web

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "图片查看"

        //供外面自己拼接文件路径，如果外面传该字段，则加载该路径；否则用原本拼接逻辑
        val filePath = intent?.getStringExtra("filePath")
        val imaUrl =
                if (filePath.isNullOrBlank())
                "${HttpMethods.FileUrl}id=${intent.getStringExtra("fileGuid")}&SessionId=${Hawk.get<String>("SessionID")}" +
                "&UserId=${Hawk.get<String>("UserID")}&ContractId=${intent.getIntExtra("contractId", 0)}" +
                "&SaveView=${intent.getIntExtra("isRead", 0)}" +
                "&fileID=${intent.getStringExtra("fileID") ?: ""}"
                else filePath
        Logger.e("==图片地址==> %s", imaUrl)
        val pd = CustomDialog.createLoadingDialog(this@WebActivity, "加载中...")
        pd.show()

//        val simpleTarget = object : SimpleTarget<Drawable>() {
//            override fun onResourceReady(drawable: Drawable?, transition: Transition<in Drawable>?) {
//                if (pd != null && pd.isShowing) {
//                    pd.dismiss()
//                }
//                if (intent.getIntExtra("isRead", 0) == 1) {
//                    audit = true
//                }
//                zoomImage.setImageDrawable(drawable
//                        ?: resources.getDrawable(R.mipmap.imageloadingfailed))
//            }
//
//            override fun onLoadFailed(errorDrawable: Drawable?) {
//                if (pd != null && pd.isShowing) {
//                    pd.dismiss()
//                }
//                zoomImage.setImageDrawable(resources.getDrawable(R.mipmap.imageloadingfailed))
//            }
//        }
//        Glide.with(this).load(imaUrl)
//                .apply(RequestOptions().error(R.mipmap.imageloadingfailed))
//                .into(simpleTarget)


        Glide.with(this).load(imaUrl).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                if (pd != null && pd.isShowing) {
                    pd.dismiss()
                }
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                if (pd != null && pd.isShowing) {
                    pd.dismiss()
                }
                if (intent.getIntExtra("isRead", 0) == 1) {
                    audit = true
                }
                return false
            }

        })
                .apply(RequestOptions().error(R.mipmap.imageloadingfailed).timeout(60 * 1000))
                .into(zoomImage)
    }

    override fun isShowBacking(): Boolean = true

    override fun onBackPressed() {
        val data = Intent()
        data.putExtra("position", intent.getIntExtra("position", -1))
        data.putExtra("audit", audit)
        setResult(88, data)
        super.onBackPressed()
    }

    private fun getData() {
        Observable.create(Observable.OnSubscribe<File> {
            try {
                val url = URL(HttpMethods.FileUrl)
                //打开连接
                val conn = url.openConnection()
                //打开输入流
                val `is` = conn.getInputStream()
                //获得长度
                val contentLength = conn.contentLength
                Log.e("my_log", "contentLength = " + contentLength)
                //创建文件夹 MyDownLoad，在存储卡下
                val dirName = Constants.DocPath
                val file = File(dirName)
                //不存在创建
                if (!file.exists()) {
                    file.mkdir()
                }
                //下载后的文件名
                val fileName = dirName + "测试文档.tif"
                val file1 = File(fileName)
                if (file1.exists()) {
                    file1.delete()
                }
                //创建字节流
                val bs = ByteArray(1024)
                val os = FileOutputStream(fileName)
                var len: Int = `is`.read(bs)
                //写数据
                while (len != -1) {
                    os.write(bs, 0, len)
                    len = `is`.read(bs)
                }
                os.close()
                `is`.close()
                it.onNext(File(fileName))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(object : Subscriber<File>() {
                    override fun onCompleted() {

                    }

                    override fun onNext(file: File?) {
                        OpenFileUtils.getInstance().openFile(this@WebActivity, file?.path)
                    }

                    override fun onError(e: Throwable?) {
                    }
                })
    }
}
