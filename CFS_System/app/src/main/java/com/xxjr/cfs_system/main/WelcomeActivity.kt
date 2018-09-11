package com.xxjr.cfs_system.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.view.View
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger

import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.HttpMethods
import com.xiaoxiao.rxjavaandretrofit.NetService
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener
import com.xxjr.cfs_system.ViewsHolder.MyCountDown
import com.xxjr.cfs_system.tools.BitmapManage
import com.xxjr.cfs_system.tools.Constants
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import kotlinx.android.synthetic.main.activity_welcome.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit

class WelcomeActivity : Activity() {
    //    private lateinit var dialog: Dialog
    private lateinit var advertisingCount: MyCountDown
    private var isShow = false
    private var webUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
//        dialog = CustomDialog.createLoadingDialog(this@WelcomeActivity, "加载中...")
//        dialog.setCancelable(false)
//        dialog.show()
//        dialog.setOnKeyListener { dialogInterface: DialogInterface, keyCode: Int, keyEvent: KeyEvent ->
//            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.repeatCount == 0) return@setOnKeyListener true
//            else false
//        }
        getAdvertising()
        imageView.setOnClickListener {
            if (isShow && webUrl.isNotBlank()) {
                advertisingCount.cancel()
                //代码实现跳转
                val intent = Intent(this@WelcomeActivity, WelWebActivity::class.java)
                intent.putExtra("Type", 0)
                intent.putExtra("AdvertisingUrl", webUrl)
                startActivityForResult(intent, 1)
            }
        }
        tv_count.setOnClickListener {
            advertisingCount.cancel()
            toLogin()
        }
    }

    //初始化计时器
    private fun initCountDown(time: Long) {
        advertisingCount = MyCountDown(time + 100, 1000, tv_count)
        advertisingCount.setTimeFinishListener(RecycleItemClickListener { toLogin() })
    }

    //获取广告路径和图片
    private fun getAdvertising() {
        //手动创建一个OkHttpClient并设置超时时间
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(30, TimeUnit.SECONDS)
        builder.writeTimeout(30, TimeUnit.SECONDS)
        builder.readTimeout(30, TimeUnit.SECONDS)
        val retrofit = Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl("${HttpMethods.AdvertisingUrl}api/Ad/")
                .build().create(NetService::class.java)

        val call = retrofit.getAdvertising("4", Hawk.get("UserID"))
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                initCountDown((application as MyApplication).advertisingTime)
                showCacheIcon("")
            }

            override fun onResponse(call: Call<String>?, response: Response<String>) {
                if (response.code() == 200) {
                    val result = response.body() ?: ""
                    Logger.e("==广告页==>%s", result)
                    if (isJson(result)) {
                        val jsonObject: JSONObject = JSON.parseObject(result)
                        setAdvertisingIcon(jsonObject)
                    } else {
                        initCountDown((application as MyApplication).advertisingTime)
                        showCacheIcon("")
                    }
                } else {
//                    if (dialog.isShowing) dialog.dismiss()
                    toLogin()
                }
            }
        })
    }

    //设置广告图片
    private fun setAdvertisingIcon(jsonObject: JSONObject) {
        isShow = jsonObject.getBooleanValue("isShow")
        (application as MyApplication).showAdvertising = isShow
        webUrl = jsonObject.getString("redirectUrl") ?: ""
        (application as MyApplication).advertisingUrl = webUrl
        val iconUrl = jsonObject.getString("splashImageUrl") ?: ""
        val duration = jsonObject.getIntValue("duration")
        if (duration >= 0) (application as MyApplication).advertisingTime = duration * 1000L
        if (isShow) {
            initCountDown((application as MyApplication).advertisingTime)
            if (Hawk.get<String>("AdvertisingIconUrl") == iconUrl) {
                showCacheIcon(iconUrl)
            } else {
                getServiceIcon(iconUrl)
            }
        } else toLogin()
    }

    //显示缓存图片
    private fun showCacheIcon(iconUrl: String) {
        val file = File(Constants.AdvertisingPath)
        if (file.exists()) {
            Glide.with(this@WelcomeActivity).load(file)
                    .apply(RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.launchlmage).timeout(60 * 1000))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            if (iconUrl.isBlank()) toLogin() else getServiceIcon(iconUrl)
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                                    if (dialog.isShowing) dialog.dismiss()
                            tv_count.visibility = View.VISIBLE
                            advertisingCount.start()
                            return false
                        }
                    })
                    .into(imageView)
        } else {
            if (iconUrl.isBlank()) toLogin()
            else getServiceIcon(iconUrl)
        }
    }

    //获取服务器广告图片
    private fun getServiceIcon(iconUrl: String) {
        Glide.with(this@WelcomeActivity)
                .asBitmap()
                .load(iconUrl)
                .apply(RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.launchlmage).timeout(60 * 1000))
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap?, transition: Transition<in Bitmap>?) {
//                        if (dialog.isShowing) dialog.dismiss()
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap)
                            tv_count.visibility = View.VISIBLE
                            Observable.create(Observable.OnSubscribe<Boolean> {
                                AndPermission.with(this@WelcomeActivity)
                                        .requestCode(Constants.REQUEST_CODE_PERMISSION_SD)
                                        .permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .callback(object : PermissionListener {
                                            override fun onSucceed(requestCode: Int, grantPermissions: MutableList<String>) {
                                                val file = File(Environment.getExternalStorageDirectory(), "/CFS/cache")
                                                if (file.exists() || file.mkdirs()) {
                                                    val locPath = Constants.AdvertisingPath
                                                    BitmapManage.savePhotoToSDCard(bitmap, locPath)
                                                    it.onNext(true)
                                                } else {
                                                    it.onNext(false)
                                                }
                                            }

                                            override fun onFailed(requestCode: Int, deniedPermissions: MutableList<String>) {
                                                it.onNext(false)
                                            }
                                        })
                                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                                        // 这样避免用户勾选不再提示，导致以后无法申请权限。
                                        // 你也可以不设置。
                                        .rationale { requestCode, rationale ->
                                            // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                                            AndPermission.rationaleDialog(this@WelcomeActivity, rationale).show()
                                        }
                                        .start()
                            })
                                    .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                                    .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                                    .subscribe {
                                        if (it) Hawk.put("AdvertisingIconUrl", iconUrl)
                                    }
                            advertisingCount.start()
                        } else toLogin()
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
//                        if (dialog.isShowing) dialog.dismiss()
                        showCacheIcon("")
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        advertisingCount.cancel()
        finish()
    }

    private fun toLogin() {
        startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
        finish()
    }

    fun isJson(content: String): Boolean {
        try {
            JSON.parseObject(content)
            return true
        } catch (e: Exception) {
            return false
        }

    }
}
