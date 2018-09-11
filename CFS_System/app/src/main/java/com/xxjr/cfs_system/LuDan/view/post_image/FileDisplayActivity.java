package com.xxjr.cfs_system.LuDan.view.post_image;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.xiaoxiao.ludan.R;
import com.xiaoxiao.rxjavaandretrofit.HttpMethods;
import com.xiaoxiao.widgets.CustomDialog;
import com.xxjr.cfs_system.tools.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class FileDisplayActivity extends AppCompatActivity {
    SuperFileView mSuperFileView;
    String filePath;
    String fileType = "";//文件类型
    boolean audit = false;
    Dialog pd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_display);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        init();
    }

    public void init() {
        Intent intent = this.getIntent();
        if (intent.getIntExtra("isRead", 0) == 1) {
            audit = true;
        }
        fileType = intent.getStringExtra("fileType");
        String path = HttpMethods.FileUrl + "id=" + intent.getStringExtra("fileGuid") +
                "&SessionId=" + Hawk.get("SessionID", "") +
                "&UserId=" + Hawk.get("UserID", "") +
                "&ContractId=" + intent.getIntExtra("contractId", 0)
                + "&SaveView=" + intent.getIntExtra("isRead", 0)
                + "&fileID=" + intent.getStringExtra("fileID");

//        String path = "http://192.168.31.221:2569/DuiService/GetFileStream?id=079_d7ae259a-342f-4b57-8ffb-db2ed1012adf&SessionId=95514424-7030-433f-8a2e-2a535e53b5cf";//docx
//        String path = "http://192.168.31.221:2569/DuiService/GetFileStream?id=142_1d2fe5fc-48f4-4e4c-b06b-4e11f0abb41e&SessionId=95514424-7030-433f-8a2e-2a535e53b5cf";//tif
//        String path = "http://www.hrssgz.gov.cn/bgxz/sydwrybgxz/201101/P020110110748901718161.doc";
//        String path = "/storage/emulated/0/test.txt";
//        String path = "/storage/emulated/0/test.xlsx";
//        String path = "/storage/emulated/0/test.pptx";
//        String path = "/storage/emulated/0/test.pdf";
        if (!TextUtils.isEmpty(path)) {
            Logger.e("==文件path==> %s", path);
            setFilePath(path);
        }
        mSuperFileView = findViewById(R.id.mSuperFileView);
        mSuperFileView.setOnGetFilePathListener(new SuperFileView.OnGetFilePathListener() {
            @Override
            public void onGetFilePath(SuperFileView mSuperFileView1) {
                getFilePathAndShowFile(mSuperFileView1);
            }
        });
        mSuperFileView.show();
    }


    private void getFilePathAndShowFile(SuperFileView mSuperFileView) {
        if (getFilePath().startsWith("http")) {//网络地址要先下载
            downLoadFileAndShow(getFilePath(), mSuperFileView);
        } else {
            mSuperFileView.displayFile(new File(getFilePath()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
        if (mSuperFileView != null) {
            mSuperFileView.onStopDisplay();
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("position", getIntent().getIntExtra("position", -1));
        data.putExtra("audit", audit);
        setResult(88, data);
        finish();
        super.onBackPressed();
    }

    public void setFilePath(String fileUrl) {
        this.filePath = fileUrl;
    }

    private String getFilePath() {
        return filePath;
    }

    //下载并显示word文档
    private void downLoadFileAndShow(final String myUrl, final SuperFileView mSuperFileView) {
        pd = CustomDialog.createLoadingDialog(this, "加载中...");
        pd.show();
        Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    URL url = new URL(myUrl);
                    //打开连接
                    URLConnection conn = url.openConnection();
                    //打开输入流
                    is = conn.getInputStream();
                    //获得长度
                    int contentLength = conn.getContentLength();
                    Log.e("my_log", "==contentLength ==>" + contentLength);
                    //创建文件夹 MyDownLoad，在存储卡下
                    String dirName = Constants.DocPath;
                    File file = new File(dirName);
                    //不存在创建
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    //下载后的文件名
                    String fileName = dirName + "资料." + fileType;
                    File file1 = new File(fileName);
                    if (file1.exists()) {
                        file1.delete();
                    }
                    fos = new FileOutputStream(fileName);
                    byte[] buf = new byte[2048];
                    int len;
                    //创建字节流
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.close();
                    is.close();
                    subscriber.onNext(new File(fileName));
                } catch (Exception e) {
                    audit = false;
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        audit = false;
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                        Log.e("my_log", "==查看文件下载失败==>" + e.getMessage());
                    }

                    @Override
                    public void onNext(File file) {
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                        mSuperFileView.displayFile(file);
                    }
                });
    }

}
