package com.xiaoxiao.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Chuiliu Meng on 2016/9/6.
 * 自定义dialog弹出框
 *
 * @author Chuiliu Meng
 */
public class PromptDialog extends Dialog {
    public PromptDialog(Context context) {
        super(context);
    }

    public PromptDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context mCxt;
        private String message = "", positiveButtonText = "", negativeButtonText = "";
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private boolean isShowOneButton;
        private boolean isNotShowClosed;//true-->隐藏弹出框关闭图标

        public Builder(Context context) {
            this.mCxt = context;
            isShowOneButton = false;
        }

        public void setShowOneButton(boolean isShowOneButton) {
            this.isShowOneButton = isShowOneButton;
        }

        public void setNotShowClosed(boolean isNotShowClosed) {
            this.isNotShowClosed = isNotShowClosed;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) mCxt.getText(message);
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = (String) mCxt.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = (String) mCxt.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public PromptDialog create() {
            final PromptDialog dialog = new PromptDialog(mCxt, R.style.loading_dialog);
            dialog.show();
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            DisplayMetrics d = mCxt.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
            lp.width = (int) (d.widthPixels * 0.8);
            window.setAttributes(lp);
            window.setGravity(Gravity.CENTER);
            window.setContentView(R.layout.dialog_prompt);
            Button btn_confirm = (Button) window.findViewById(R.id.positiveButton);
            Button btn_cancel = (Button) window.findViewById(R.id.negativeButton);
            ImageView iv_close = (ImageView) window.findViewById(R.id.iv_close);
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            View view_line = window.findViewById(R.id.view);
            TextView tv_prompt = (TextView) window.findViewById(R.id.tv_prompt);
            tv_prompt.setText(message);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

            iv_close.setVisibility(isNotShowClosed ? View.GONE : View.VISIBLE);
            view_line.setVisibility(isShowOneButton ? View.GONE : View.VISIBLE);
            btn_cancel.setVisibility(isShowOneButton ? View.GONE : View.VISIBLE);

            if (positiveButtonText != null) {
                btn_confirm.setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    btn_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            } else {
                btn_confirm.setVisibility(View.GONE);
            }
            if (negativeButtonText != null) {
                btn_cancel.setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            } else {
                btn_cancel.setVisibility(View.GONE);
            }
            return dialog;
        }
    }
}