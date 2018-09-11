package com.xxjr.cfs_system.ViewsHolder;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;
import com.xxjr.cfs_system.LuDan.view.activitys.LoanListActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.Message_Notification.View.MessageActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.reimbursement_remind.RemindListActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.staff_training.StaffTrainingDialog;
import com.xxjr.cfs_system.LuDan.view.activitys.withdraw_wallet.WithdrawListActivity;
import com.xxjr.cfs_system.main.LoginActivity;
import com.xxjr.cfs_system.main.MyApplication;

import entity.TrainingList;

/**
 * Created by Administrator on 2018/4/11.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static final String TYPE = "type"; //这个type是为了Notification更新信息的，这个不明白的朋友可以去搜搜，很多

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int type = intent.getIntExtra(TYPE, -1);
        if (type != -1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(type);
        }

        assert action != null;
        if (action.equals("notification_clicked")) {
            MyApplication application = (MyApplication) context.getApplicationContext();
            Logger.d("---notification_clicked---");

            if (application.getAppCount() <= 0) {
                application.MsgType = type;
                Intent intent1 = new Intent(context, LoginActivity.class);
                context.startActivity(intent1);
            } else if (application.getLoginClick() || application.getSMSClick()) {
                application.MsgType = type;
            } else {
                Intent intent1;
                switch (type) {
                    case 3://还款提醒消息点击
                        intent1 = new Intent(context, RemindListActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                        break;
                    case 4://客户失信消息点击
                        intent1 = new Intent(context, LoanListActivity.class);
                        intent1.putExtra("Type", 1);
                        intent1.putExtra("contractType", 2);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                        break;
                    case 5://提成提醒
                        intent1 = new Intent(context, WithdrawListActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                        break;
                    case 6:
                        intent1 = new Intent(context, MessageActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                        break;
                }
            }
        }
        if (action.equals("notification_cancelled")) {
            //处理滑动清除和点击删除事件
        }
    }
}
