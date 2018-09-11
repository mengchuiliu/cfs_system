package com.xxjr.cfs_system.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.xiaoxiao.ludan.R;
import com.xiaoxiao.widgets.CustomDialog;
import com.xiaoxiao.widgets.WaterMarkDrawable;
import com.xxjr.cfs_system.LuDan.adapters.RecycleItemClickListener;
import com.xxjr.cfs_system.LuDan.presenter.BasePresenter;
import com.xxjr.cfs_system.LuDan.view.BaseViewInter;
import com.xxjr.cfs_system.ViewsHolder.MyCountDown;
import com.xxjr.cfs_system.tools.Constants;

import java.io.File;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/7/25.
 *
 * @author mengchuiliu
 */

public abstract class BaseActivity<P extends BasePresenter, V extends BaseViewInter> extends AppCompatActivity {
    protected P presenter;
    private MyApplication application;
    protected Context oContext;
    private TextView title;
    private TextView subTitle;
    private ImageView imageView, back, adImage;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        presenter = getPresenter();
        if (presenter != null)
            presenter.attach((V) this);
        if (application == null) {
            // 得到Application对象
            application = (MyApplication) getApplication();
        }
        oContext = this;// 把当前的上下文对象赋值给BaseActivity
        addActivity();// 调用添加方法
        setToolbarView();
        initView(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected abstract P getPresenter();

    // 添加Activity方法
    public void addActivity() {
        application.addActivity(this);// 调用myApplication的添加Activity方法
    }

    //设置主题布局
    private void setToolbarView() {
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title);
        subTitle = findViewById(R.id.right);
        imageView = findViewById(R.id.iv_right);
        back = findViewById(R.id.iv_back);
        if (toolbar != null) {
            //将Toolbar显示到界面
            setSupportActionBar(toolbar);
        }
        if (title != null) {
            //getTitle()的值是activity的android:lable属性值
            title.setText(getTitle());
            //设置默认的标题不显示
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != getToolbar() && isShowBacking()) {
            showBack();
        }
        initADView();
        if (application.canShowAd() && application.showAdvertising) {
            showADView();
        }
    }

    private MyCountDown advertisingCount;
    private View mAdView;
    private TextView tvCount;//倒计时

    private void initADView() {
        mAdView = View.inflate(this, R.layout.activity_welcome, null);
        adImage = mAdView.findViewById(R.id.imageView);
        tvCount = mAdView.findViewById(R.id.tv_count);
        tvCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (advertisingCount != null) advertisingCount.cancel();
                mAdView.setVisibility(View.GONE);
            }
        });
        adImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(application.advertisingUrl)) {
                    if (advertisingCount != null) advertisingCount.cancel();
                    Intent intent = new Intent(BaseActivity.this, WelWebActivity.class);
                    intent.putExtra("Type", 0);
                    intent.putExtra("AdvertisingUrl", application.advertisingUrl);
                    startActivity(intent);
                    mAdView.setVisibility(View.GONE);
                }
            }
        });
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.x = 0;
        params.y = 0;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindowManager().addView(mAdView, params);
        mAdView.setVisibility(View.GONE);
    }

    //创建广告页
    private void showADView() {
        File file = new File(Constants.AdvertisingPath);
        if (file.exists()) {
            Glide.with(this).load(file)
                    .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.launchlmage).timeout(60 * 1000))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            mAdView.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            mAdView.setVisibility(View.VISIBLE);
                            tvCount.setVisibility(View.VISIBLE);
                            advertisingCount = new MyCountDown(application.advertisingTime + 100, 1000, tvCount);
                            advertisingCount.setTimeFinishListener(new RecycleItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    if (advertisingCount != null) advertisingCount.cancel();
                                    mAdView.setVisibility(View.GONE);
                                }
                            });
                            advertisingCount.start();
                            return false;
                        }
                    })
                    .into(adImage);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!application.canShowAd()) {
            if (advertisingCount != null) advertisingCount.cancel();
            mAdView.setVisibility(View.GONE);
            getWindowManager().removeViewImmediate(mAdView);
        }
    }

    /**
     * 设置水印
     *
     * @param water 水印布局
     */
    protected void setWater(ViewGroup water) {
        String text = Hawk.get("UserRealName", "");
        WaterMarkDrawable drawable = new WaterMarkDrawable(text, getResources().getColor(R.color.font_dd), 45, getResources().getColor(R.color.transparent));
        water.setBackgroundDrawable(drawable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null)
            presenter.deAttach();
        application.removeActivity(this);
    }

    /**
     * 设置layout布局,在子类重写该方法.
     *
     * @return res layout xml id
     */
    protected abstract int getLayoutId();

    /**
     * 加载布局
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 获取头部.
     *
     * @return support.v7.widget.Toolbar.
     */
    public Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * 获取头部标题的TextView
     *
     * @return
     */
    public TextView getToolbarTitle() {
        return title;
    }

    /**
     * 获取头部标题的TextView
     *
     * @return
     */
    public TextView getSubTitle() {
        return subTitle;
    }

    public ImageView getIvRight() {
        return imageView;
    }

    public ImageView getIvBack() {
        return back;
    }

    /**
     * 版本号小于21的后退按钮图片
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void showBack() {
        //setNavigationIcon必须在setSupportActionBar(toolbar);方法后面加入
        getToolbar().setNavigationIcon(R.mipmap.icon_back);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * toolbar是否显示后退按钮,默认显示,可在子类重写该方法.
     *
     * @return
     */
    protected boolean isShowBacking() {
        return false;
    }

    /**
     * 未保存退出弹出框
     *
     * @param save
     * @param activity
     */
    protected void isSave(boolean save, final Activity activity) {
        if (save) {
            activity.finish();
        } else {
            CustomDialog.showTwoButtonDialog(activity, "信息尚未保存，确定退出吗?", "确定", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    activity.finish();
                }
            });
        }
    }

    public String getDefaultInt(String str) {
        if (TextUtils.isEmpty(str)) {
            return "0";
        }
        return str;
    }

    protected void startActivity(Class<?> toClass) {
        Intent intent = new Intent(this, toClass);
        startActivity(intent);
    }

    protected void startActivity(Class<?> toClass, Bundle bundle) {
        Intent intent = new Intent(this, toClass);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void startActivityForResult(Class<?> toClass, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, toClass);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }
}
