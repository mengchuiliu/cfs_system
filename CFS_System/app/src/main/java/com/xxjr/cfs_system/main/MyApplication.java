package com.xxjr.cfs_system.main;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.smtt.sdk.QbSdk;
import com.xiaoxiao.ludan.R;
import com.xiaoxiao.rxjavaandretrofit.HttpMethods;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xiaoxiao.widgets.CustomDialog;
import com.xxjr.cfs_system.LuDan.view.activitys.SignActivity;
import com.xxjr.cfs_system.services.CrashHandler;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.Utils;

import java.util.LinkedList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import entity.GoldRegisteredInfo;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/7/25.
 *
 * @author meng
 */

public class MyApplication extends MultiDexApplication {
    private static int appState = Constants.APP_STATE_NORMAL;//app状态 前台 后台
    // 标记程序是否已进入后台(依据onStop回调)
    private boolean flag;
    // 标记程序是否已进入后台(依据onTrimMemory回调)
    private boolean background;
    // 从前台进入后台的时间
    private long frontToBackTime;
    // 从后台返回前台的时间
    private long backToFrontTime;
    //是否广告
    public boolean showAdvertising = false;
    //广告显示时间
    public long advertisingTime = 10 * 1000;
    //广告链接
    public String advertisingUrl = "";
    private List<Activity> list = new LinkedList<>();
    private int appCount = 0;//记录是否前台
    private boolean isLoginClick = false;//是否登录点击消息
    private boolean isSMSClick = false;//是否短信验证界面
    public boolean isCheckUpgradeApp = true;//检测更新app
    public int MsgType = -1;//消息标识 3->还款提醒,4->客户失信,5->提成钱包
    public int notifyId = 0;//消息id
    public GoldRegisteredInfo goldRegister = null;//金账户信息对象

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM)//设置加密方法，中等强度加密
//                .setStorage(HawkBuilder.newSqliteStorage(this))//设置存储方式，SqliteStorage或者SharedPrefStorage
                .setStorage(HawkBuilder.newSharedPrefStorage(this))//设置存储方式，SqliteStorage或者SharedPrefStorage
                .setLogLevel(LogLevel.FULL)//日志输出级别，FULL或者NONE
                .build();

        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了x5，有可能特殊情况下x5内核加载失败，切换到系统内核。
            }

            @Override
            public void onViewInitFinished(boolean b) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.e("my_log", "加载内核是否成功:" + b);
            }
        });//TBS查看word

        registForeground();

        //错误日志
//        CrashHandler.getInstance().init(this);

        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        JPushInterface.stopCrashHandler(this);

        //初始化logger打印
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // 是否显示线程信息，默认为ture
                .methodCount(1) // 显示的方法行数，默认为2
//                .methodOffset(0) // 隐藏内部方法调用到偏移量，默认为0
                .tag("mcl")   // 每个日志的全局标记。默认PRETTY_LOGGER
                .build();
        //正式库不打印log
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return getApplicationInfo() != null &&
                        (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            }
        });

        setLoginRepeat();
    }

    //设置重复登录退出
    private void setLoginRepeat() {
        Subscription subscription = RxBus.getInstance().toObservable(HttpMethods.Login_Repeat_Out, Boolean.class).subscribe(
                new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            if (list.size() > 0) {
                                if (list.get(list.size() - 1) != null) {
                                    showLoginOut();
                                }
                            } else {
                                repeatOut();
                            }
                        }
                    }
                }
        );
    }

    private void showLoginOut() {
        CustomDialog.showOneButtonDialog(list.get(list.size() - 1), R.string.login_repeat, R.string.exit_login,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        repeatOut();
                    }
                }, true);
    }

    //退出登录
    private void repeatOut() {
        Hawk.put("Psw", "");
        exit();
        Intent intent1 = new Intent(MyApplication.this, LoginActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);
    }

    //注册监听应用是否在前台运行
    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void registForeground() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                appCount++;
                isLoginClick = activity instanceof LoginActivity;
                isSMSClick = activity instanceof SignActivity;
                if (background || flag) {
                    background = false;
                    flag = false;
                    appState = Constants.APP_STATE_BACK_TO_FRONT;
                    backToFrontTime = System.currentTimeMillis();
                } else {
                    appState = Constants.APP_STATE_NORMAL;
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                //判断当前activity是否处于前台
                if (!Utils.isCurAppTop(activity)) {
                    // 从前台进入后台
                    appState = Constants.APP_STATE_FRONT_TO_BACK;
                    frontToBackTime = System.currentTimeMillis();
                    flag = true;
                } else {
                    // 否则是正常状态
                    appState = Constants.APP_STATE_NORMAL;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // TRIM_MEMORY_UI_HIDDEN是UI不可见的回调, 通常程序进入后台后都会触发此回调, 大部分手机多是回调这个参数
        // TRIM_MEMORY_BACKGROUND也是程序进入后台的回调, 不同厂商不太一样, 魅族手机就是回调这个参数
        if (level == Application.TRIM_MEMORY_UI_HIDDEN || level == TRIM_MEMORY_BACKGROUND) {
            background = true;
        } else if (level == Application.TRIM_MEMORY_COMPLETE) {
            background = !Utils.isCurAppTop(this);
        }
        if (background) {
            frontToBackTime = System.currentTimeMillis();
            appState = Constants.APP_STATE_FRONT_TO_BACK;
        } else {
            appState = Constants.APP_STATE_NORMAL;
        }
    }

    //把当前Activity添加到集合中
    public void addActivity(Activity activity) {
        list.add(activity);
    }

    public void removeActivity(Activity activity) {
        list.remove(activity);
    }

    //获取当前activity
    public Activity getCurActivity() {
        if (list.size() > 0 && list.get(list.size() - 1) != null) {
            return list.get(list.size() - 1);
        }
        return null;
    }

    //退出
    public void exit() {
        try {
            for (Activity activity : list) {
                if (activity != null) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            list.clear();
            //System.exit(0);
        }
    }

    public int getAppCount() {
        return appCount;
    }

    public boolean getLoginClick() {
        return isLoginClick;
    }

    public boolean getSMSClick() {
        return isSMSClick;
    }

    /**
     * 进入后台间隔多长时间再次显示广告
     *
     * @return 是否能显示广告
     */
    public boolean canShowAd() {
        return appState == Constants.APP_STATE_BACK_TO_FRONT &&
                (backToFrontTime - frontToBackTime) > Constants.IntervalTime;
    }
}
