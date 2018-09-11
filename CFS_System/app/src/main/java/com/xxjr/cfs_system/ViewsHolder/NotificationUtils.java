package com.xxjr.cfs_system.ViewsHolder;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.xiaoxiao.ludan.R;

/**
 * Created by Administrator on 2018/4/11.
 */
@TargetApi(Build.VERSION_CODES.O)
@SuppressLint("WrongConstant")
public class NotificationUtils extends ContextWrapper {

    private NotificationManager manager;
    public static final String id = "channel_1";
    public static final String name = "channel_name_1";

    public NotificationUtils(Context context) {
        super(context);
    }

    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public Notification.Builder getChannelNotification(String title, String content) {
        return new Notification.Builder(getApplicationContext(), id)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo))
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND);
    }

    public NotificationCompat.Builder getNotification(String title, String content) {
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo))
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND);
    }

    public void sendNotification(int id, String title, String content, PendingIntent pendingIntent) {
        Notification notification;
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            Notification.Builder builder = getChannelNotification(title, content);
            if (pendingIntent != null) {
                builder.setContentIntent(pendingIntent);
            }
            notification = builder.build();
        } else {
            NotificationCompat.Builder builder = getNotification(title, content);
            if (pendingIntent != null) {
                builder.setContentIntent(pendingIntent);
            }
            notification = builder.build();
        }
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        getManager().notify(id, notification);
    }
}
