package com.xxjr.cfs_system.clipheadphoto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.ludan.R;
import com.xiaoxiao.rxjavaandretrofit.HttpAction;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xiaoxiao.widgets.CustomDialog;
import com.xxjr.cfs_system.tools.BitmapManage;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.ToastShow;
import com.xxjr.cfs_system.tools.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class ClipActivity extends AppCompatActivity {
    private Dialog loadingDialog;
    private String path;//图片路径
    private ClipImageLayout mClipImageLayout;
    Bitmap mybitmap;
    private boolean isPost = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);
        ButterKnife.bind(this);
        path = getIntent().getStringExtra("path");
        initView();
    }

    private void initView() {
        mClipImageLayout = (ClipImageLayout) findViewById(R.id.clipImageLayout);
        if (TextUtils.isEmpty(path) || !(new File(path).exists())) {
            ToastShow.showShort(this, "图片加载失败!path");
            return;
        }
        Bitmap bitmap = BitmapManage.convertToBitmap(path, 600, 600);
        if (bitmap == null) {
            ToastShow.showShort(this, "图片加载失败!");
            return;
        }
        mClipImageLayout.setBitmap(rotateBitmapByDegree(bitmap, getBitmapDegree(path)));
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degree;
    }


    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError ignored) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        return returnBm;
    }

    @OnClick({R.id.clip_back, R.id.action_clip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clip_back:
                finish();
                break;
            case R.id.action_clip:
                loadingDialog = CustomDialog.createLoadingDialog(ClipActivity.this, "请稍后...");
                loadingDialog.show();
                if (!isPost) {
                    return;
                }
                isPost = false;
                mybitmap = mClipImageLayout.clip();
                File file = new File(Environment.getExternalStorageDirectory(), "/CFS/cache");
                if (file.exists() || file.mkdirs()) {
                    final String locpath = Constants.SerPortraitPath + Hawk.get("UserID") + ".png";
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            BitmapManage.savePhotoToSDCard(mybitmap, locpath, handler);
                        }
                    }).start();
                }
                break;
        }
    }

    //上传用户图像
    private void postUserPortrait(Bitmap bitmap) {
        String userId = Hawk.get("UserID");
        String SessionID = Hawk.get("SessionID");
        List<Object> list = new ArrayList<>();
        list.add("HeadPic");
        list.add("UPD");
        list.add(userId);
        String str = JSON.toJSONString(list);
        HttpAction.getInstance().upLoadPortrait(
                new Subscriber<ResponseData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        handler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onNext(ResponseData data) {
                        if (data.getExecuteResult()) {
                            handler.sendEmptyMessage(3);
                        } else {
                            handler.sendEmptyMessage(2);
                        }
                    }
                }, SessionID, getBase64(str), getBase64("UserInfoManage"), getBase64(""), BitmapManage.compressImageByte(bitmap));
    }

    private String getBase64(String str) {
        return Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    File file = new File(Constants.SerPortraitPath + Hawk.get("UserID") + ".png");
                    if (file.exists()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (mybitmap != null)
                                    postUserPortrait(mybitmap);
                            }
                        }).start();
                    }
                    isPost = false;
                    break;
                case 2:
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    isPost = true;
                    ToastShow.showShort(getApplicationContext(), "图像上传失败,请稍后再试!");
                    break;
                case 3:
                    Utils.setBitmapToCache(Constants.PortraitPath + Hawk.get("UserID") + ".png", null); // 清除头像缓存
                    if (mybitmap != null) {
                        Utils.setPortraitBitmap(mybitmap);
                    }
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    isPost = true;
                    ToastShow.showShort(getApplicationContext(), "图像上传成功!");
                    Intent intent = new Intent();
                    intent.putExtra("path", Constants.PortraitPath + Hawk.get("UserID") + ".png");
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mybitmap = null;
        handler.removeCallbacksAndMessages(null);
    }
}
