package com.xxjr.cfs_system.tools;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ljd.retrofit.progress.DownloadProgressHandler;
import com.ljd.retrofit.progress.ProgressHelper;
import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.rxjavaandretrofit.HttpAction;
import com.xiaoxiao.rxjavaandretrofit.HttpMethods;
import com.xiaoxiao.rxjavaandretrofit.NetService;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xiaoxiao.widgets.CustomDialog;
import com.xiaoxiao.ludan.R;
import com.xxjr.cfs_system.main.LoginActivity;
import com.xxjr.cfs_system.main.MyApplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import subscribers.ProgressSubscriber;
import subscribers.SubscriberOnNextListener;

/**
 * Created by Administrator on 2017/5/18.
 * app更新协助类
 */

public class AppUpdateHelp {
    public final static String NAME_OF_APP = "CFS.apk";
    private Context context;
    private AlertDialog updateDialog;
    private ProgressBar mProgressBar;
    private TextView mTvProgress;
    private Dialog promptDialog;//服务运维提示加载框

    public AppUpdateHelp(Context context) {
        this.context = context;
    }

    private void login() {
        if (!TextUtils.isEmpty(((LoginActivity) context).getPassword())) {
            long loginTime = Hawk.get("loginTime", 0L);
            if (System.currentTimeMillis() - loginTime >= 7 * 24 * 60 * 60 * 1000) {
                Hawk.put("Psw", "");
                ((LoginActivity) context).setPassword("");
            } else {
                ((LoginActivity) context).login();
            }
        }
    }

    /**
     * @param isShow      显示加载动效
     * @param isShowCheck 是否显示检测结果提示
     */
    public void checkUpdate(final boolean isShow, final boolean isShowCheck) {
        Map<String, Object> map = new HashMap<>();
        map.put("Marker", " HQServer");
        map.put("IsUseZip", false);
        map.put("TranName", "CheckApkVersion");
        String str = JSON.toJSONString(map);
        HttpAction.getInstance().getData(new ProgressSubscriber(new SubscriberOnNextListener<ResponseData>() {
            @Override
            public void onNext(ResponseData data) {
                if (data.getExecuteResult()) {
                    List<String> list = JSON.parseArray(data.getReturnStrings(), String.class);
                    try {
                        int currentCode = Integer.valueOf(list.get(0));
                        String upContent = list.size() > 1 ? list.get(2) : "";
                        PackageManager packageManager = context.getPackageManager();
                        PackageInfo packageInfo;
                        packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                        if (currentCode > packageInfo.versionCode) {
                            hasUpdate(list.get(1));
                            Hawk.put("upgradeShow", true);
                        } else {
                            ((MyApplication) context.getApplicationContext()).isCheckUpgradeApp = false;
                            if (isShowCheck) {
                                ToastShow.showShort(context, "当前版本已是最新版本!");
                            } else {
                                if (!TextUtils.isEmpty(upContent)) {
                                    boolean upgradeShow = Hawk.get("upgradeShow", true);
                                    if (upgradeShow) {
                                        showPromptDialog(upContent);
                                    } else {
                                        login();
                                    }
                                } else {
                                    login();
                                }
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastShow.showShort(context, data.getData());
                }
            }

            @Override
            public void onError(String msg) {
                ToastShow.showShort(context, msg);
            }
        }, context, isShow), "", str);
    }

    //运维升级提示框
    private void showPromptDialog(String content) {
        if (promptDialog == null) {
            promptDialog = new Dialog(context, R.style.loading_dialog);
        }
        promptDialog.show();
        Hawk.put("upgradeShow", false);
        Window window = promptDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.75);
        lp.gravity = Gravity.CENTER;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
        window.setContentView(R.layout.prompt);
        TextView textView = window.findViewById(R.id.tv_upgrade_content);
        textView.setText(content);
        window.findViewById(R.id.tv_know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptDialog.dismiss();
                login();
            }
        });
        CheckBox noPrompt = window.findViewById(R.id.tv_no_prompt);
        noPrompt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Hawk.put("upgradeShow", false);
                } else {
                    Hawk.put("upgradeShow", true);
                }
            }
        });
        promptDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return i == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0;
            }
        });
        promptDialog.setCanceledOnTouchOutside(false);
    }

    //更新
    private void hasUpdate(String url) {
        File file = new File(Environment.getExternalStorageDirectory(), NAME_OF_APP);
        if (file.exists()) {
            file.delete();
        }
        showRemindDialog(url);
    }

    private void showRemindDialog(final String url) {
        CustomDialog.showOneButtonDialog(context, "发现最新版本,是否立即更新!", "更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                showUpdateDialog(url);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showUpdateDialog(String url) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_new_version, null);
        mProgressBar = (ProgressBar) dialogView.findViewById(R.id.pb_download);
        mTvProgress = (TextView) dialogView.findViewById(R.id.tv_version);
        updateDialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT).create();
        updateDialog.setCancelable(false);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setView(dialogView);
        Window window = updateDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.y = -150;
        window.setAttributes(lp);
        updateDialog.show();
        downloadApk(url);
    }

    private void downloadApk(final String url) {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(HttpMethods.BASE_URL); //没有实际作用，因为DownloadApi使用了@url注解，但是不能为空
        OkHttpClient.Builder builder = ProgressHelper.addProgress(null);
        NetService retrofit = retrofitBuilder
                .client(builder.build())
                .build().create(NetService.class);

        ProgressHelper.setProgressHandler(new DownloadProgressHandler() {
            @Override
            protected void onProgress(long bytesRead, long contentLength, boolean done) {
                mProgressBar.setProgress((int) ((100 * bytesRead) / contentLength));
                mTvProgress.setText((int) ((100 * bytesRead) / contentLength) + "%");
                if (done) {
                    updateDialog.dismiss();
                }
            }
        });

        Call<ResponseBody> call = retrofit.retrofitDownload(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        InputStream is = response.body().byteStream();
                        File file = new File(Environment.getExternalStorageDirectory(), NAME_OF_APP);
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        byte[] buffer = new byte[1024 * 4];
                        int len;
                        while ((len = bis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            fos.flush();
                        }
                        fos.close();
                        bis.close();
                        is.close();
                        installApk(file.getAbsolutePath(), file);
                    } else {
                        updateDialog.dismiss();
                        Toast.makeText(context, "更新异常", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    updateDialog.dismiss();
                    Toast.makeText(context, "更新异常!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                updateDialog.dismiss();
                Toast.makeText(context, "更新包下载失败，请检查网络是否正常", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void installApk(String path, File file) {
        if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
            Uri apkUri = FileProvider.getUriForFile(context, "com.xiaoxiao.ludan.fileprovider", file);//在AndroidManifest中的android:authorities值
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            context.startActivity(install);
        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
        }
    }
}
