package com.xxjr.cfs_system.tools;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Chuiliu Meng on 2016/8/9.
 *
 * @author Chuiliu Meng
 */
public class ToastShow {

    /**
     * 短时间显示Toast
     */
    public static void showShort(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 短时间显示Toast
     */
    public static void showShort(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     */
    public static void showLong(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     */
    public static void showLong(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     */
    public static void showTime(Context context, CharSequence message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     */
    public static void showTime(Context context, int message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

}
