package com.xxjr.cfs_system.LuDan.view.activitys.gesture_lock

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.orhanobut.hawk.Hawk
import com.xiaoxiao.ludan.R
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter
import com.xxjr.cfs_system.LuDan.view.BaseViewInter
import com.xxjr.cfs_system.main.BaseActivity
import com.xxjr.cfs_system.tools.ToastShow
import com.zyyoona7.lock.GestureLockLayout
import com.zyyoona7.lock.QQLockView
import kotlinx.android.synthetic.main.activity_lock_set.*

class LockSetActivity : BaseActivity<BasePresenter<*, *>, BaseViewInter>() {
    private val mHandler = Handler()

    override fun getPresenter(): BasePresenter<*, *>? = null

    override fun getLayoutId(): Int = R.layout.activity_lock_set

    override fun initView(savedInstanceState: Bundle?) {
        toolbarTitle.text = "手势密码"
        ivBack.visibility = View.VISIBLE
        ivBack.setOnClickListener { finish() }
        initViews()
        initEvents()
    }

    override fun onStart() {
        super.onStart()
        setWater(water)
    }

    private fun initViews() {
        //设置提示view 每行每列点的个数
        display_view.setDotCount(3)
        //设置提示view 选中状态的颜色
        display_view.setDotSelectedColor(Color.parseColor("#01A0E5"))
        //设置提示view 非选中状态的颜色
        display_view.setDotUnSelectedColor(resources.getColor(R.color.font_cc))
        //设置手势解锁view 每行每列点的个数
        gesture_view.setDotCount(3)
        //设置手势解锁view 最少连接数
        gesture_view.setMinCount(3)
        //默认解锁样式为手Q手势解锁样式
        gesture_view.setLockView(QQLockView(this))
        //设置手势解锁view 模式为重置密码模式
        gesture_view.setMode(GestureLockLayout.RESET_MODE)
    }

    private fun initEvents() {
        gesture_view.setOnLockResetListener(object : GestureLockLayout.OnLockResetListener {
            override fun onConnectCountUnmatched(connectCount: Int, minCount: Int) {
                //连接数小于最小连接数时调用
                tv_setting_hint.text = "最少连接${minCount}个点"
                resetGesture()
            }

            override fun onFirstPasswordFinished(answerList: List<Int>) {
                //第一次绘制手势成功时调用
                tv_setting_hint.text = "确认解锁图案"
                //将答案设置给提示view
                display_view.setAnswer(answerList)
                //重置
                resetGesture()
            }

            override fun onSetPasswordFinished(isMatched: Boolean, answerList: List<Int>) {
                //第二次密码绘制成功时调用
                if (isMatched) {
                    //两次答案一致，保存
                    Hawk.put(Hawk.get<String>("Account", "") + "_answer", answerList.toString())
                    Hawk.put(Hawk.get<String>("Account", "") + "_isUnlock", true)
                    ToastShow.showShort(this@LockSetActivity, "手势密码设置成功")
                    finish()
                } else {
                    ToastShow.showShort(this@LockSetActivity, "两次绘制的团不一致")
                    resetGesture()
                }
            }
        })
    }

    /**
     * 重置
     */
    private fun resetGesture() {
        mHandler.postDelayed({ gesture_view.resetGesture() }, 200)
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }
}
