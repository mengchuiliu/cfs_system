package com.xxjr.cfs_system.LuDan.presenter;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.xiaoxiao.rxjavaandretrofit.HttpAction;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xxjr.cfs_system.LuDan.model.HttpResult;
import com.xxjr.cfs_system.LuDan.model.modelimp.LoginMImp;
import com.xxjr.cfs_system.LuDan.view.viewinter.LoginVInter;
import com.xxjr.cfs_system.services.CacheProvide;
import com.xxjr.cfs_system.tools.AppUpdateHelp;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import entity.PersonalInfo;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/7/26.
 * 登录执行者
 */
public class LoginPresenter extends BasePresenter<LoginVInter, LoginMImp> {
    private String returnStrings;
    private String versionName;
    private String versionCode;
    private String permissions;
    private String typeName;
    private String company;
    private String realName;
    private String UserBirthday;
    private AppUpdateHelp appUpdateHelp;

    @Override
    protected LoginMImp getModel() {
        return new LoginMImp();
    }

    //网络请求参数组装
    private List<Object> getListParam() {
        List<Object> list = new ArrayList<>();
        String RegistrationID = JPushInterface.getRegistrationID((Context) getView());
        list.add(getView().getUserName());
        list.add(Utils.getMD5Str(getView().getPassword()));
        list.add("933f63a173540f09b587fb7f95625bbb");
        list.add(versionCode);
        list.add("2");
        list.add(RegistrationID);//极光注册唯一标示码
        return list;
    }

    @Override
    protected void onSuccess(int resultCode, final ResponseData data) {
        if (resultCode == 0) {
            returnStrings = data.getReturnStrings();
            JSONArray jsonArray = JSON.parseArray(data.getData());
            if (jsonArray != null && !jsonArray.isEmpty()) {
                final JSONObject jsonObject = jsonArray.getJSONObject(0);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveData(jsonObject);
                    }
                }).start();
                permissions = jsonObject.getString("PermitValue");
                typeName = jsonObject.getString("UserTypeName");
                company = jsonObject.getString("CompanyName");
                realName = jsonObject.getString("UserRealName");
                UserBirthday = jsonObject.getString("UserBirthday");
                Observable.create(new Observable.OnSubscribe<Object>() {
                    @Override
                    public void call(Subscriber<? super Object> subscriber) {
                        Hawk.put("SessionID", data.getReturnString());
                        Hawk.put("UserID", jsonObject.getString("UserID"));
                        CacheProvide.UserKey = jsonObject.getString("UserID");
                        subscriber.onNext("");
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Object>() {

                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                Logger.e("==登录保存数据错误==> %s", e.getMessage());
                                onFailed(0, e.getMessage());
                            }

                            @Override
                            public void onNext(Object o) {
                                if (getView().getPassword().equals("123456")) {
                                    getView().hideLoading();
                                    getView().showUpdatePswDialog();
                                } else {
//                                getView().showMsg("登录成功!");
                                    Hawk.put("loginTime", System.currentTimeMillis());
                                    HttpAction.getInstance().getData(new CacheSubscriber(), data.getReturnString(), model.getIncreaseCacheParam());
                                }
                            }
                        });
            }
        } else if (resultCode == 1) {
            if (!TextUtils.isEmpty(data.getReturnMsg())) {
                Observable.create(new Observable.OnSubscribe<Object>() {
                    @Override
                    public void call(Subscriber<? super Object> subscriber) {
                        model.saveCacheData(data.getReturnMsg());
                        subscriber.onNext("");
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Object>() {

                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                Logger.e("==缓存处理错误==> %s" + e.getMessage());
                                onFailed(1, e.getMessage());
                            }

                            @Override
                            public void onNext(Object o) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
//                                        getView().showMsg("数据加载完成");
                                        if (isViewAttached()) {
                                            getView().hideLoading();
                                            getView().complete(permissions, typeName, company, realName, UserBirthday, returnStrings);
                                        }
                                    }
                                }, 500);
                            }
                        });
            } else {
                getView().showMsg("加载缓存数据失败,请在侧边栏同步数据后再行操作!");
                getView().hideLoading();
                getView().complete(permissions, typeName, company, realName, UserBirthday, returnStrings);
            }
        }
    }

    @Override
    protected void onFailed(int resultCode, String msg) {
        getView().hideLoading();
        if (resultCode == 1) {
            getView().showMsg("加载缓存数据失败,请在侧边栏同步数据后再行操作!");
            getView().complete(permissions, typeName, company, realName, UserBirthday, returnStrings);
        } else {
            getView().showMsg(msg);
        }
    }

    private class CacheSubscriber extends Subscriber<ResponseData> {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Logger.e("==缓存错误==> %s", e.getMessage());
            onFailed(1, e.getMessage());
        }

        @Override
        public void onNext(ResponseData data) {
            if (data.getExecuteResult()) {
                onSuccess(1, data);
            } else {
                onFailed(1, data.getData());
            }
        }
    }

    @Override
    public void setDefaultValue() {
        if (TextUtils.isEmpty(getView().getUserName())) {
            getView().setUserName(Hawk.get("Account", ""));
        }
        if (TextUtils.isEmpty(getView().getPassword())) {
            getView().setPassword(Hawk.get("Psw", ""));
        }
        getView().setVersionName("V " + versionName);
    }

    public void setVersionName(String versionName, String versionCode) {
        appUpdateHelp = new AppUpdateHelp((Context) getView());
        Hawk.put("versionName", versionCode);
        this.versionName = versionName;
        this.versionCode = versionCode;
    }

    @Override
    protected void permissionSuccess(int code) {
        if (code == Constants.REQUEST_CODE_PERMISSION_SD) {
            checkAppUpdate();
        }
    }

    //检测更新
    private void checkAppUpdate() {
        appUpdateHelp.checkUpdate(true, false);
    }

    //获取显示数据
    public void getData() {
        model.login(model.getParam(getListParam(), "UserLogin"), new HttpResult() {
            @Override
            public void reusltSuccess(ResponseData data) {
                onSuccess(0, data);
            }

            @Override
            public void reusltFailed(String msg) {
                onFailed(0, msg);
            }
        });
    }

    private void saveData(JSONObject jsonObject) {
        if (isViewAttached()) {
            Hawk.put("RegistrationID", JPushInterface.getRegistrationID((Context) getView()));
            Hawk.put("Account", getView().getUserName());
            Hawk.put("Psw", getView().getPassword());
        }
        Hawk.put("CompanyID", jsonObject.getString("CompanyID"));
        Hawk.put("UserZone", jsonObject.getString("UserZone"));
        Hawk.put("CompanyName", jsonObject.getString("CompanyName"));
        Hawk.put("CompanyFullName", jsonObject.getString("CompanyFullName"));
        Hawk.put("UserType", jsonObject.getString("UserType"));
        Hawk.put("PermitValue", jsonObject.getString("PermitValue"));
        Hawk.put("CompanyPowers", jsonObject.getString("CompanyPowers"));
        Hawk.put("ZonePowers", jsonObject.getString("ZonePowers"));
        Hawk.put("UserTypeName", jsonObject.getString("UserTypeName"));
        Hawk.put("UserRealName", jsonObject.getString("UserRealName"));
        setPersonalInfo(jsonObject);
        Utils.setBitmapToCache(Constants.PortraitPath + Hawk.get("UserID") + ".png", null); // 清除头像缓存
    }

    private void setPersonalInfo(JSONObject personalInfo) {
        PersonalInfo info = new PersonalInfo();
        info.setUserSurname(TextUtils.isEmpty(personalInfo.getString("UserSurname")) ? "" : personalInfo.getString("UserSurname"));
        info.setUserName(TextUtils.isEmpty(personalInfo.getString("UserName")) ? "" : personalInfo.getString("UserName"));
        info.setUserSex(personalInfo.getIntValue("UserSex"));
        info.setUserBirthday(TextUtils.isEmpty(personalInfo.getString("UserBirthday")) ? "" : Utils.getTime(personalInfo.getString("UserBirthday")));
        info.setUserCreateTime(TextUtils.isEmpty(personalInfo.getString("UserCreateTime")) ? "" : Utils.getTime(personalInfo.getString("UserCreateTime")));
        info.setSIP(TextUtils.isEmpty(personalInfo.getString("SIP")) ? "" : personalInfo.getString("SIP"));
        info.setSeat(TextUtils.isEmpty(personalInfo.getString("Seat")) ? "" : personalInfo.getString("Seat"));
        info.setPhone(TextUtils.isEmpty(personalInfo.getString("Phone")) ? "" : personalInfo.getString("Phone"));
        info.setPhone1(TextUtils.isEmpty(personalInfo.getString("Phone1")) ? "" : personalInfo.getString("Phone1"));
        Hawk.put("PersonalInfo", info);
    }

}
