package com.xxjr.cfs_system.LuDan.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xxjr.cfs_system.LuDan.model.BaseModelInter;
import com.xxjr.cfs_system.LuDan.model.HttpResult;
import com.xxjr.cfs_system.LuDan.view.BaseViewInter;
import com.xxjr.cfs_system.tools.Constants;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Administrator on 2017/7/25.
 *
 * @author mengchuiliu
 *         执行者基类
 */

public abstract class BasePresenter<V extends BaseViewInter, M extends BaseModelInter> {
    private WeakReference<V> weakReference;
    protected M model;
    //protected String SessionID;
    PermissionRequest permissionRequest;

    public void attach(V v) {
        weakReference = new WeakReference<>(v);
        model = getModel();
//        SessionID = Hawk.get("SessionID");
    }

    public void deAttach() {
        if (weakReference != null) {
            if (permissionRequest != null) {
                permissionRequest = null;
            }
            weakReference.clear();
            weakReference = null;
        }
    }

    public boolean isViewAttached() {
        return weakReference != null && weakReference.get() != null;
    }

    public V getView() {
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    protected abstract M getModel();

    public abstract void setDefaultValue();//设置默认界面显示

    //isShow-->是否显示加载圈
    public void getData(final int resultCode, String param, boolean isShow) {
        model.getData((Context) getView(), Hawk.get("SessionID", ""), resultCode, param, new HttpResult() {
            @Override
            public void reusltSuccess(ResponseData data) {
                onSuccess(resultCode, data);
            }

            @Override
            public void reusltFailed(String msg) {
                onFailed(resultCode, msg);
            }
        }, isShow);
    }

    //碎片获取数据 isShow-->是否显示加载圈
    public void getFragmentData(Context context, final int resultCode, String param, boolean isShow) {
        model.getData(context, Hawk.get("SessionID", ""), resultCode, param, new HttpResult() {
            @Override
            public void reusltSuccess(ResponseData data) {
                onSuccess(resultCode, data);
            }

            @Override
            public void reusltFailed(String msg) {
                onFailed(resultCode, msg);
            }
        }, isShow);
    }

    //请求成功
    protected abstract void onSuccess(int resultCode, ResponseData data);

    //请求失败
    protected abstract void onFailed(int resultCode, String msg);

    //权限申请
    public class PermissionRequest implements PermissionListener {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            permissionSuccess(requestCode);
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            permissionFailed(requestCode);
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission((Activity) getView(), deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog((Activity) getView(), Constants.RESULT_CODE_SETTING).show();

                // 第二种：用自定义的提示语。
//             AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
//                     .setTitle("权限申请失败")
//                     .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
//                     .setPositiveButton("好，去设置")
//                     .show();

//            第三种：自定义dialog样式。
//            SettingService settingService = AndPermission.defineSettingDialog(this, REQUEST_CODE_SETTING);
//            你的dialog点击了确定调用：
//            settingService.execute();
//            你的dialog点击了取消调用：
//            settingService.cancel();
            }
        }
    }

    //权限申请成功
    protected void permissionSuccess(int code) {
    }

    //权限申请失败
    protected void permissionFailed(int code) {
    }

    public PermissionRequest getPermissioner() {
        if (permissionRequest == null) {
            permissionRequest = new PermissionRequest();
        }
        return permissionRequest;
    }
}
