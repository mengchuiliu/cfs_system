package com.xxjr.cfs_system.LuDan.view.activitys.gold_account

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.KeyEvent
import com.xiaoxiao.ludan.R
import android.view.ViewGroup
import android.webkit.*
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.rxjavaandretrofit.HttpMethods
import com.xxjr.cfs_system.LuDan.adapters.RecyclerItemCheck
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener
import com.xxjr.cfs_system.ViewsHolder.AndroidtoJs
import kotlinx.android.synthetic.main.activity_gold_web.*

@RequiresApi(Build.VERSION_CODES.KITKAT)
@SuppressLint("SetJavaScriptEnabled,AddJavascriptInterface")
class GoldWebActivity : AppCompatActivity() {
    private var mWebView: WebView? = null
    private lateinit var onItemCheck: RecyclerItemCheck
    private lateinit var onItemCheck1: RecyclerItemCheck
    private lateinit var onTextChangeListener: TextChangeListener
    private var isOver = false
    private var isFirst = true
    private var tradeSn = ""
    private var phone = ""

    fun getType(): Int = intent.getIntExtra("Type", 0) //0->提现  1->充值  2->修改密码 3->修改手机号码

    fun getUserNo(): String = intent.getStringExtra("UserNo")

    fun getAmount(): Double = intent.getDoubleExtra("Amount", 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold_web)
        initView()
    }

    private fun initView() {
        onTextChangeListener = TextChangeListener { position, text ->
            when (getType()) {
                2 -> tradeSn = text
                3 -> phone = text
            }
        }
        onItemCheck = object : RecyclerItemCheck {
            override fun onItemCheck(isCheck: Boolean) {
                isOver = isCheck
            }
        }
        onItemCheck1 = object : RecyclerItemCheck {
            override fun onItemCheck(isCheck: Boolean) {
                val intent2 = Intent()
                when (getType()) {
                    2 -> intent2.putExtra("TradeSn", tradeSn)
                    3 -> intent2.putExtra("Phone", phone)
                }
                setResult(22, intent2)
                finish()
            }
        }
        mWebView = WebView(applicationContext)
        mWebView?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        activity_web.addView(mWebView)

        val webSettings = mWebView?.settings
        webSettings?.useWideViewPort = true//设置此属性，可任意比例缩放
        webSettings?.loadWithOverviewMode = true
        webSettings?.builtInZoomControls = true
        webSettings?.setSupportZoom(true)
        webSettings?.javaScriptEnabled = true
        webSettings?.displayZoomControls = false
        webSettings?.mediaPlaybackRequiresUserGesture = false
        webSettings?.javaScriptCanOpenWindowsAutomatically = true// 设置允许JS弹窗
        webSettings?.allowContentAccess = true
        webSettings?.setAppCacheEnabled(false)
        webSettings?.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webSettings?.cacheMode = WebSettings.LOAD_NO_CACHE

        // 通过addJavascriptInterface()将Java对象映射到JS对象
        mWebView?.addJavascriptInterface(AndroidtoJs(this@GoldWebActivity, onItemCheck, onItemCheck1, onTextChangeListener), "payment")//AndroidtoJS类对象映射到js的test对象
        mWebView?.loadUrl(getUrl())

        mWebView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                //因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
                super.onPageFinished(view, url)
                if (isFirst) {
                    if (Build.VERSION.SDK_INT < 18) {
                        mWebView?.loadUrl(getJSMethod())
                    } else {
                        mWebView?.evaluateJavascript(getJSMethod(), {})
                    }
                    isFirst = false
                }
            }
        }
        mWebView?.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                val b = AlertDialog.Builder(this@GoldWebActivity)
                b.setTitle("提示")
                b.setMessage(message)
                b.setPositiveButton(android.R.string.ok) { dialog, which -> result?.confirm() }
                b.setCancelable(false)
                b.create().show()
                return true
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                val b = AlertDialog.Builder(this@GoldWebActivity)
                b.setTitle("提示")
                b.setMessage(message)
                b.setPositiveButton(android.R.string.ok) { dialog, which -> result?.confirm() }
                b.setNegativeButton(android.R.string.cancel) { dialog, which -> result?.cancel() }
                b.create().show()
                return true
            }
        }
    }

    private fun getUrl(): String {
        return when (getType()) {
            0 -> "${HttpMethods.GoldUrl}PersonalWithdraw"//提现
            1 -> "${HttpMethods.GoldUrl}PersonalEasyRecharge" //充值
            2 -> "${HttpMethods.GoldUrl}Account/ChangePassword?type=3"//重置支付密码
            3 -> "${HttpMethods.GoldUrl}AccountChangePhone/ChangePhone"//修改手机号码
            else -> ""
        }
    }

    private fun getJSMethod(): String {
        return when (getType()) {
            0, 1 -> "javascript:startAction('${getUserNo()}','${getAmount()}','${Hawk.get<String>("CompanyID")}','${Hawk.get<String>("UserID")}','4')"
            2 -> "javascript:startAction('${getUserNo()}','${Hawk.get<String>("CompanyID")}','${Hawk.get<String>("UserID")}','4')"
            3 -> "javascript:startAction('${getUserNo()}')"
            else -> ""
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (isOver) {
            val intent2 = Intent()
            when (getType()) {
                2 -> intent2.putExtra("TradeSn", tradeSn)
                3 -> intent2.putExtra("Phone", phone)
            }
            setResult(22, intent2)
            this.finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun clearWebViewResource() {
        if (mWebView != null) {
            mWebView?.removeAllViews()
            (mWebView?.parent as ViewGroup).removeView(mWebView)
            mWebView?.clearHistory()
            mWebView?.destroy()
            mWebView = null
        }
    }

    override fun onDestroy() {
        clearWebViewResource()
        super.onDestroy()
    }
}
