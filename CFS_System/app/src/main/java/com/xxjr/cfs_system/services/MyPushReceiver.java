package com.xxjr.cfs_system.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xxjr.cfs_system.LuDan.view.activitys.LoanListActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.reimbursement_remind.RemindListActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.staff_training.StaffTrainingDialog;
import com.xxjr.cfs_system.LuDan.view.activitys.withdraw_wallet.WithdrawListActivity;
import com.xxjr.cfs_system.ViewsHolder.NotificationBroadcastReceiver;
import com.xxjr.cfs_system.ViewsHolder.NotificationUtils;
import com.xxjr.cfs_system.main.LoginActivity;
import com.xxjr.cfs_system.main.MyApplication;
import com.xxjr.cfs_system.tools.Constants;

import cn.jpush.android.api.JPushInterface;
import entity.TrainingList;

/**
 * Created by Administrator on 2017/5/2.
 *
 * @author mengchuiliu
 * 极光推送通知接收
 */

public class MyPushReceiver extends BroadcastReceiver {
    private MyApplication application;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        application = (MyApplication) context.getApplicationContext();
        Logger.d("[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        // 注册ID的广播这个比较重要，因为所有的推送服务都必须，注册才可以额接收消息
        // 注册是在后台自动完成的，如果不能注册成功，那么所有的推送方法都无法正常进行
        // 这个注册的消息，可以发送给自己的业务服务器上。也就是在用户登录的时候，给自己的服务器发送
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Logger.d("[MyReceiver] 接收Registration Id : %s", regId);
            // send the Registration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            // 所有自定义的消息才会进入这个方法里
            Logger.d("[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            // 所有的普通推送，都会进入到这个部分，并且Jpush自己会进行 Notification的显示
            // 我们只要把notificationId 存起来，或者保存到本地，用于列表的排序.
            Logger.d("[MyReceiver] 接收到推送下来的通知");
            if (application.getAppCount() > 1) {
                RxBus.getInstance().post(Constants.POST_REFRESH_MY_TASK, true);
            }
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Logger.d("[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            // notification点击打开，主要针对是普通的推送消息
            Logger.d("[MyReceiver] 用户点击打开了通知");
            // 打开自定义的Activity
            if (application.getAppCount() <= 0 || application.getLoginClick()) {
                Intent i;
                i = new Intent(context, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Logger.d("[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            // 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
            // 打开一个网页等..
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Logger.d("[MyReceiver]" + intent.getAction()
                    + " connected state change to " + connected);
        } else {
            Logger.d("[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey1:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey2:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey3:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    private String getMsgType(Bundle bundle) {
        String msgType = "";
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                String value = bundle.getString(key);
                if (!TextUtils.isEmpty(value)) {
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(value);
                    msgType = jsonObject.getString("FuncCode") != null ? jsonObject.getString("FuncCode") : "";
                    break;
                }
            }
        }
        return msgType;
    }

    private String getMessageContent(Bundle bundle, String contentKey) {
        String content = "";
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                String value = bundle.getString(key);
                if (!TextUtils.isEmpty(value)) {
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(value);
                    content = jsonObject.getString(contentKey) != null ? jsonObject.getString(contentKey) : "";
                    break;
                }
            }
        }
        return content;
    }


    /**
     * 可能经常用到的一点，获取附加的自定义的字段
     *
     * @param context
     * @param bundle
     */
    private void processCustomMessage(Context context, Bundle bundle) {
        // if (MainActivity.isForeground) {//检查当前软件是否在前台
        // 利用JPushInterface.EXTRA_MESSAGE 机械能推送消息的获取
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        // 可能经常用到的一点，获取附加的自定义的字段、
        // 这个字符串就是Json的格式，用于自己的服务器给特定的客户端传递一些特定的属性和配置，
        // 例如显示一些数字、特定的事件，或者是访问特定的网址的时候，使用extras
        // 例如显示订单信息、特定的商品列表，特定的咨询网址

        switch (getMsgType(bundle)) {
            case "J03":
                messageRemind(context, "还款提醒", message, 3);
                break;
            case "J04":
                messageRemind(context, "客户失信黄单提醒", message, 4);
                break;
            case "J05":
                messageRemind(context, "提成提醒", message, 5);
                break;
            case "J06":
                String title = bundle.getString(JPushInterface.EXTRA_TITLE);
                messageDialog(context, title, message, 6, bundle);
                break;
            default:
                commonRemind(context, message);
                break;
        }
    }

    //一般消息
    private void commonRemind(Context context, String message) {
        application.notifyId++;
        Intent intent;
        if (application.getAppCount() <= 0) {
            intent = new Intent(context, LoginActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationUtils notificationUtils = new NotificationUtils(context);
            notificationUtils.sendNotification(application.notifyId, "录单消息", message, pendingIntent);
        } else {
            intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationUtils notificationUtils = new NotificationUtils(context);
            notificationUtils.sendNotification(application.notifyId, "录单消息", message, pendingIntent);
        }
    }

    //消息提醒
    private void messageRemind(Context context, String title, String message, int id) {
        application.notifyId++;
        Intent intent;
        if (application.getAppCount() <= 0 || application.getLoginClick() || application.getSMSClick()) {
            intent = new Intent(context, NotificationBroadcastReceiver.class);
            intent.setAction("notification_clicked");
            intent.putExtra(NotificationBroadcastReceiver.TYPE, id);
            PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationUtils notificationUtils = new NotificationUtils(context);
            notificationUtils.sendNotification(application.notifyId, title, message, pendingIntentClick);
        } else {
            switch (id) {
                case 3:
                    intent = new Intent(context, RemindListActivity.class);
                    break;
                case 4:
                    intent = new Intent(context, LoanListActivity.class);
                    intent.putExtra("Type", 1);
                    intent.putExtra("contractType", 2);
                    break;
                case 5:
                    intent = new Intent(context, WithdrawListActivity.class);
                    break;
                default:
                    intent = new Intent();
                    break;
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationUtils notificationUtils = new NotificationUtils(context);
            notificationUtils.sendNotification(application.notifyId, title, message, pendingIntent);
        }
    }

    private void messageDialog(Context context, String title, String message, int id, Bundle bundle) {
        application.notifyId++;
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction("notification_clicked");
        intent.putExtra(NotificationBroadcastReceiver.TYPE, id);
        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationUtils notificationUtils = new NotificationUtils(context);
        notificationUtils.sendNotification(application.notifyId, title, message, pendingIntentClick);
        if (application.getCurActivity() != null) {
            StaffTrainingDialog staffDialog = new StaffTrainingDialog(application.getCurActivity());
            staffDialog.initView(title, getMessageContent(bundle, "Summary"),
                    getMessageContent(bundle, "CategoryId"), getMessageContent(bundle, "NotificationId"));
            staffDialog.show();
        }
    }
}
