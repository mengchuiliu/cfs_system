package com.xiaoxiao.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Chuiliu Meng on 2016/8/12.
 *
 * @author Chuiliu Meng
 */
public class CustomDialog {

    /**
     * 加载对话框
     */
    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        //loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.addContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;
    }

    public static void showAlertDialog(Context context, String prompt, String btText) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(prompt);
        builder.setShowOneButton(true);
        builder.setPositiveButton(btText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public static void showAlertDialog(Context context, int prompt, int btText) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(prompt);
        builder.setShowOneButton(true);
        builder.setPositiveButton(btText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public static void showAlertDialog(Context context, final Activity activity, String prompt, String btText) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(prompt);
        builder.setShowOneButton(true);
        builder.setPositiveButton(btText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                activity.finish();
            }
        });
        builder.create().show();
    }

    public static void showOneButtonDialog(Context context, String message, String positiveButtonText) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(message);
        builder.setShowOneButton(true);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public static void showOneButtonDialog(Context context, int messageRes, int positiveButtonTextRes) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(messageRes);
        builder.setShowOneButton(true);
        builder.setPositiveButton(positiveButtonTextRes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public static void showOneButtonDialog(Context context, int messageRes, int positiveButtonTextRes, DialogInterface.OnClickListener listener) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(messageRes);
        builder.setShowOneButton(true);
        builder.setPositiveButton(positiveButtonTextRes, listener);
        builder.create().show();
    }

    public static void showOneButtonDialog(Context context, int messageRes, int positiveButtonTextRes, DialogInterface.OnClickListener listener, boolean isNotShowClosed) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(messageRes);
        builder.setShowOneButton(true);
        builder.setNotShowClosed(isNotShowClosed);
        builder.setPositiveButton(positiveButtonTextRes, listener);
        builder.create().show();
    }

    public static void showOneButtonDialog(Context context, String message, String positiveButtonText, DialogInterface.OnClickListener listener) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(message);
        builder.setShowOneButton(true);
        builder.setPositiveButton(positiveButtonText, listener);
        builder.create().show();
    }

    public static void showTwoButtonDialog(Context context, String message, String positiveButtonText, String negativeButtonText) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public static void showTwoButtonDialog(Context context, int messageRes, int positiveButtonTextRes, int negativeButtonTextRes) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(messageRes);
        builder.setPositiveButton(positiveButtonTextRes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(negativeButtonTextRes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public static void showTwoButtonDialog(Context context, String message, String positiveButtonText, String negativeButtonText, DialogInterface.OnClickListener listener) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, listener);
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public static void showTwoButtonDialog(Context context, String message, String positiveButtonText, String negativeButtonText, DialogInterface.OnClickListener listener, boolean isNotShowClosed) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(message);
        builder.setNotShowClosed(isNotShowClosed);
        builder.setPositiveButton(positiveButtonText, listener);
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public static void showTwoButtonDialog(Context context, String message, String positiveButtonText, String negativeButtonText, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener listener1) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, listener);
        builder.setNegativeButton(negativeButtonText, listener1);
        builder.create().show();
    }

    public static void showTwoButtonDialog(Context context, int messageRes, int positiveButtonTextRes, int negativeButtonTextRes, DialogInterface.OnClickListener listener) {
        PromptDialog.Builder builder = new PromptDialog.Builder(context);
        builder.setMessage(messageRes);
        builder.setPositiveButton(positiveButtonTextRes, listener);
        builder.setNegativeButton(negativeButtonTextRes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
}
