package com.xxjr.cfs_system.LuDan.view.activitys.staff_training

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnClick
import com.alibaba.fastjson.JSON
import com.orhanobut.hawk.Hawk
import com.orhanobut.logger.Logger
import com.xiaoxiao.ludan.R
import com.xiaoxiao.rxjavaandretrofit.HttpAction
import com.xiaoxiao.rxjavaandretrofit.ResponseData
import com.xiaoxiao.widgets.dialog.base.BaseDialog
import com.xiaoxiao.widgets.dialog.base.IBaseDialog
import com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.View.TrainingNotifyActivity
import subscribers.ProgressSubscriber
import subscribers.SubscriberOnNextListener
import java.util.HashMap

/**
 * 员工消息弹框
 */
class StaffTrainingDialog(private val mContext: Context) : IBaseDialog {
    private var mBaseDialog: BaseDialog? = null
    private var categoryId = -1
    private var notificationId = ""

    @JvmField
    @Bind(R.id.tv_training_title)
    var trainingTitle: TextView? = null

    @JvmField
    @Bind(R.id.tv_training_content)
    var trainingContent: TextView? = null


    init {
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_message_staff_training, null, false)
        ButterKnife.bind(this, view)
        val builder = BaseDialog.Builder(mContext, view)
        builder.setWidthScale(0.75f)
        builder.setCancelable(true)
        builder.setCanceledOnTouchOutside(true)
        builder.setGravity(Gravity.CENTER)
        mBaseDialog = builder.create()
    }

    fun initView(title: String, content: String, categoryId: String, notificationId: String) {
        this.categoryId = if (categoryId.isNotBlank()) categoryId.toInt() else -1
        this.notificationId = notificationId
        trainingTitle?.text = title
        trainingContent?.text = content
    }

    @OnClick(R.id.tv_more, R.id.tv_confirm, R.id.tv_cancel)
    fun onViewClicked(view: View) {
        dismiss()
        when (view.id) {
            R.id.tv_more -> {
                val intent1 = Intent(mContext, TrainingNotifyActivity::class.java)
                intent1.putExtra("CategoryId", categoryId)
                mContext.startActivity(intent1)
            }
            R.id.tv_confirm -> {
                HttpAction.getInstance().getData(ProgressSubscriber<ResponseData>(object : SubscriberOnNextListener<ResponseData> {
                    override fun onNext(t: ResponseData?) {
                    }

                    override fun onError(msg: String?) {
                    }
                }, mContext, false), Hawk.get<String>("SessionID"), getParam(mutableListOf<Any>().apply {
                    add(notificationId)
                    add(Hawk.get<String>("UserID"))
                }, "ReadNotification"))
            }
            R.id.tv_cancel -> {
            }
        }
    }

    fun getParam(list: List<Any>, tranName: String): String {
        val map = HashMap<String, Any>()
        map["Function"] = ""
        map["ParamString"] = list
        map["TranName"] = tranName
        Logger.e("==消息弹框确认==> %s", JSON.toJSONString(map))
        return JSON.toJSONString(map)
    }

    override fun show() {
        if (mBaseDialog != null) mBaseDialog?.show()
    }

    override fun dismiss() {
        if (mBaseDialog != null) mBaseDialog?.dismiss()
    }

    override fun onDestroy() {
        if (mBaseDialog != null) {
            mBaseDialog?.onDestroy()
            mBaseDialog = null
        }
    }
}