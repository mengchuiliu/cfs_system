package com.xxjr.cfs_system.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import com.xiaoxiao.ludan.R
import com.just.agentweb.DefaultWebClient
import kotlinx.android.synthetic.main.activity_gold_web.*

class WelWebActivity : AppCompatActivity() {
    private lateinit var mAgentWeb: AgentWeb

    fun getType(): Int = intent.getIntExtra("Type", 0) //0->广告页面

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold_web)
        initView()
    }

    private fun initView() {
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(activity_web, LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebChromeClient(object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                        //do you work
                        //            Log.i("Info","onProgress:"+newProgress);
                    }

                    override fun onReceivedTitle(view: WebView, title: String) {
                        super.onReceivedTitle(view, title)
//                        if (mTitleTextView != null) {
//                            mTitleTextView.setText(title)
//                        }
                    }
                })
                .setWebViewClient(object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        view.loadUrl(url)
                        return true
                    }
                })
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
                .interceptUnkownUrl() //拦截找不到相关页面的Scheme
                .createAgentWeb()
                .ready()
                .go(getUrl())
    }

    private fun getUrl(): String = when (getType()) {
        0 -> intent.getStringExtra("AdvertisingUrl")
        1 -> "https://www.nfex.com/mobileRegister"
        else -> ""
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onPause() {
        mAgentWeb.webLifeCycle.onPause()
        super.onPause()

    }

    override fun onResume() {
        mAgentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAgentWeb.webLifeCycle.onDestroy()
    }
}
