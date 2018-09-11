package com.xxjr.cfs_system.ViewsHolder;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiaoxiao.ludan.R;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xxjr.cfs_system.tools.Utils;

import java.util.List;

import entity.ChooseType;
import refresh_recyclerview.DividerItemDecoration;
import rvadapter.BaseViewHolder;
import rvadapter.CommonAdapter;

/**
 * Created by Administrator on 2017/3/8.
 *
 * @author mengchuiliu
 */

public class PopChoose {
    /**
     * @param context    上下文
     * @param parent     父页面
     * @param typeTitle  主题
     * @param stringList 显示列表
     * @param code       返回标志
     * @param isCancel   是否取消选择
     * @return
     */
    public static PopupWindow showChooseType(Context context, View parent, String typeTitle, List<ChooseType> stringList, final int code, final boolean isCancel) {
        final View view = LayoutInflater.from(context).inflate(R.layout.pop_type, null);
        final PopupWindow popWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCancel) {
                    RxBus.getInstance().post(code, new ChooseType(0, ""));
                }
                popWindow.dismiss();
            }
        });
        TextView title = view.findViewById(R.id.tv_title);
        title.setText(typeTitle);
        CommonAdapter adapter = new CommonAdapter<ChooseType>(context, stringList, R.layout.item_choose_type) {
            @Override
            protected void convert(BaseViewHolder holder, final ChooseType chooseType, final int position) {
                holder.setText(R.id.tv_type, chooseType.getContent());
                holder.setOnClickListener(R.id.tv_type, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RxBus.getInstance().post(code, chooseType);
                    }
                });
            }
        };
        RecyclerView recyclerView = view.findViewById(R.id.recycle_type);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
        popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        return popWindow;
    }

    /**
     * @param context    上下文
     * @param parent     显示在此页面下
     * @param textView   需要设置的内容文本
     * @param code       返回码
     * @param stringList 显示的数据源
     */
    public static void showChooseType(Context context, final View parent, final TextView textView, final int code, final List<ChooseType> stringList) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_store, null);
//        final PopupWindow popWindow = new Popup7(view, Utils.dip2px(context, 180), ViewGroup.LayoutParams.WRAP_CONTENT);
        final PopupWindow popWindow = new Popup7(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        CommonAdapter adapter = new CommonAdapter<ChooseType>(context, stringList, R.layout.item_store_list) {
            @Override
            protected void convert(BaseViewHolder holder, final ChooseType chooseType, final int position) {
                holder.setText(R.id.tv_type, chooseType.getContent());
                holder.setBackgroundRes(R.id.tv_type, R.drawable.clicked_white);
                holder.setOnClickListener(R.id.tv_type, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (popWindow.isShowing()) {
                            popWindow.dismiss();
                        }
                        textView.setText(chooseType.getContent());
                        RxBus.getInstance().post(code, chooseType);
                    }
                });
            }
        };
        RecyclerView recyclerView = view.findViewById(R.id.recycle_store);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.showAsDropDown(parent);
    }
}
