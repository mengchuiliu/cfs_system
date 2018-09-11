package com.xiaoxiao.widgets;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by Chuiliu Meng on 2016/9/8.
 * 头像popupwindow
 *
 * @author Chuiliu Meng
 */
public class PopWindow {

    public static PopupWindow choosePortrait(Context context, View.OnClickListener onClickListener, View parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.pop_select_photo, null);
        PopupWindow popWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        view.findViewById(R.id.photograph).setOnClickListener(onClickListener);// 拍照
        view.findViewById(R.id.albums).setOnClickListener(onClickListener);// 相册
        view.findViewById(R.id.cancel).setOnClickListener(onClickListener);// 取消
        popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        return popWindow;
    }

    public static PopupWindow showBankPop(Context context, View.OnClickListener onClickListener, View parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.pop_select_photo, null);
        PopupWindow popWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        TextView photograph = (TextView) view.findViewById(R.id.photograph);
        photograph.setText("设为默认卡");
        photograph.setOnClickListener(onClickListener);
        TextView albums = (TextView) view.findViewById(R.id.albums);
        albums.setText("解除绑定");
        albums.setOnClickListener(onClickListener);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(onClickListener);
        popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        return popWindow;
    }
}
