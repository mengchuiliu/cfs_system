package com.xxjr.cfs_system.ViewsHolder

import android.os.CountDownTimer
import android.widget.TextView
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener

class MyCountDown(millisInFuture: Long, countDownInterval: Long, val textView: TextView) : CountDownTimer(millisInFuture, countDownInterval) {
    private var recycleItemClickListener: RecycleItemClickListener? = null

    override fun onFinish() {
        if (recycleItemClickListener != null)
            recycleItemClickListener?.onItemClick(-1)
    }

    override fun onTick(millisUntilFinished: Long) {
        if (millisUntilFinished / 1000 > 0)
            textView.text = "${millisUntilFinished / 1000}s 跳过"
    }

    fun setTimeFinishListener(recycleItemClickListener: RecycleItemClickListener) {
        this.recycleItemClickListener = recycleItemClickListener
    }
}