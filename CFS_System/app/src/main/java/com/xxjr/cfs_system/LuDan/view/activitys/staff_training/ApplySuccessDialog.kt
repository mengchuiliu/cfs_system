package com.xxjr.cfs_system.LuDan.view.activitys.staff_training

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import com.xiaoxiao.ludan.R
import com.xiaoxiao.widgets.dialog.base.BaseDialog
import com.xiaoxiao.widgets.dialog.base.BaseDialogInterface
import com.xiaoxiao.widgets.dialog.base.IBaseDialog

/**
 * 报名成功弹框
 */
class ApplySuccessDialog(private val mContext: Context) : IBaseDialog {
    private var mBaseDialog: BaseDialog? = null
    private var mOnConfirmClickListener: BaseDialogInterface.OnConfirmClickListener? = null
    private var mOnCancelClickListener: BaseDialogInterface.OnCancelClickListener? = null

    init {
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_apply_success, null, false)
        ButterKnife.bind(this, view)
        val builder = BaseDialog.Builder(mContext, view)
        builder.setWidthScale(0.75f)
        builder.setCancelable(false)
        builder.setCanceledOnTouchOutside(false)
        builder.setGravity(Gravity.CENTER)
        mBaseDialog = builder.create()
    }

    @OnClick(R.id.tv_continue, R.id.tv_complete)
    fun onViewClicked(view: View) {
        dismiss()
        when (view.id) {
            R.id.tv_continue -> {
                if (mOnConfirmClickListener != null) mOnConfirmClickListener?.onConfirm(mBaseDialog)
            }
            R.id.tv_complete -> {
                if (mOnConfirmClickListener != null) mOnCancelClickListener?.onCancel(mBaseDialog)
            }
        }
    }

    fun setOnConfirmClickListener(onConfirmClickListener: BaseDialogInterface.OnConfirmClickListener) {
        mOnConfirmClickListener = onConfirmClickListener
    }

    fun setOnCancelClickListener(onCancelClickListener: BaseDialogInterface.OnCancelClickListener) {
        mOnCancelClickListener = onCancelClickListener
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