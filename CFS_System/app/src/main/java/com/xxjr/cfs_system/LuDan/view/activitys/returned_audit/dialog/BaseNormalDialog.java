package com.xxjr.cfs_system.LuDan.view.activitys.returned_audit.dialog;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xiaoxiao.ludan.R;
import com.xiaoxiao.widgets.dialog.base.BaseDialog;
import com.xiaoxiao.widgets.dialog.base.BaseDialogInterface;
import com.xiaoxiao.widgets.dialog.base.IBaseDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 通用样式，文本 + 取消／确定按钮
 *
 * @author huangdongqiang
 * @date 21/05/2018
 */
public class BaseNormalDialog implements IBaseDialog {
    @Bind(R.id.tv_content)
    TextView mTvContent;
    private BaseDialog mBaseDialog;
    private Context mContext;
    private BaseDialogInterface.OnConfirmClickListener mOnConfirmClickListener;
    private BaseDialogInterface.OnCancelClickListener mOnCancelClickListener;


    public BaseNormalDialog(Context context) {
        mContext = context;
        if (null == mContext) {
            return;
        }
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.base_normal_dialog, null, false);
        BaseDialog.Builder builder = new BaseDialog.Builder(mContext, view);
        ButterKnife.bind(this, view);
        if (null != builder) {
            builder.setWidthScale(0.8f);
            //builder.setHeightScale(0.2f);
            builder.setCancelable(true);
            builder.setCanceledOnTouchOutside(true);
            builder.setGravity(Gravity.CENTER);
        }
        mBaseDialog = builder.create();
    }

    /**
     * 设置文本
     *
     * @param res
     */
    public void setContentText(@StringRes int res) {
        if (null != mTvContent) {
            mTvContent.setText(res);
        }
    }

    @Override
    public void show() {
        if (null != mBaseDialog) {
            mBaseDialog.show();
        }
    }

    @Override
    public void dismiss() {
        if (null != mBaseDialog) {
            mBaseDialog.dismiss();
        }
    }


    @Override
    public void onDestroy() {
        if (null != mBaseDialog) {
            mBaseDialog.onDestroy();
            mBaseDialog = null;
        }
    }

    public void setOnConfirmClickListener(BaseDialogInterface.OnConfirmClickListener onConfirmClickListener) {
        mOnConfirmClickListener = onConfirmClickListener;
    }

    public void setOnCancelClickListener(BaseDialogInterface.OnCancelClickListener onCancelClickListener) {
        mOnCancelClickListener = onCancelClickListener;
    }

    @OnClick({R.id.tv_no, R.id.tv_yes})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_no:
                dismiss();
                if (null != mOnCancelClickListener) {
                    mOnCancelClickListener.onCancel(mBaseDialog);
                }
                break;
            case R.id.tv_yes:
                dismiss();
                if (null != mOnConfirmClickListener) {
                    mOnConfirmClickListener.onConfirm(mBaseDialog);
                }
                break;
            default:
                break;
        }
    }
}
