package com.xxjr.cfs_system.ViewsHolder

import android.content.Context
import android.webkit.JavascriptInterface
import com.orhanobut.logger.Logger
import com.xxjr.cfs_system.LuDan.adapters.RecyclerItemCheck
import com.xxjr.cfs_system.LuDan.adapters.TextChangeListener

/**
 * Created by Administrator on 2018/3/13.
 */

class AndroidtoJs(val context: Context, private val onItemCheck: RecyclerItemCheck, val onItemCheck1: RecyclerItemCheck,
                  val onTextChangeListener: TextChangeListener) {
    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    fun resultCallback(isSuccess: Boolean) {
        Logger.e("js调用----$isSuccess")
        onItemCheck1.onItemCheck(true)
    }

    @JavascriptInterface
    fun resultPhoneBack(phone: String) {
        Logger.e("js回调用----$phone")
        onTextChangeListener.setTextChage(0, phone)
    }

    @JavascriptInterface
    fun finalStepCallback() {
        Logger.e("js调用----over")
        onItemCheck.onItemCheck(true)
    }

    @JavascriptInterface
    fun rechargeSuccess(tradeSn: String) {
        Logger.e("js调用----交易流水----$tradeSn ")
        onTextChangeListener.setTextChage(0, tradeSn)
    }
}
