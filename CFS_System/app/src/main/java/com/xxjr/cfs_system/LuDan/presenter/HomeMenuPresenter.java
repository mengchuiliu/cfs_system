package com.xxjr.cfs_system.LuDan.presenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.xiaoxiao.rxjavaandretrofit.HttpAction;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xiaoxiao.widgets.CustomDialog;
import com.xxjr.cfs_system.LuDan.model.modelimp.PageMenuMImp;
import com.xxjr.cfs_system.LuDan.view.activitys.HomeMenuActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.GoldAccountActivity;
import com.xxjr.cfs_system.LuDan.view.activitys.gold_account.GoldRegistActivity;
import com.xxjr.cfs_system.main.MyApplication;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entity.GoldRegisteredInfo;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;


/**
 * Created by Administrator on 2017/7/31.
 * 首页执行类
 */

public class HomeMenuPresenter extends BasePresenter<HomeMenuActivity, PageMenuMImp> {
    private Subscription subscription, birthSubscription;
    public boolean cardShow = true;//显示银行卡提示

    @Override
    protected PageMenuMImp getModel() {
        return new PageMenuMImp();
    }

    @Override
    public void setDefaultValue() {
        getView().setUserName(TextUtils.isEmpty(getView().getRealName()) ? Hawk.get("UserRealName", "") : getView().getRealName());
        getData(4, model.getMoreParam(getMessageParam(), "GetNotificationInfoList", "App"), false);
    }

    public void initMenu() {
        subscription = RxBus.getInstance().toObservable(Constants.POST_REFRESH_MY_TASK, Boolean.class).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isRefresh) {
                if (isRefresh) {
                    refreshMyTask(true);
                }
            }
        });

        birthSubscription = RxBus.getInstance().toObservable(Constants.SHOW_BIRTH, Integer.class).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer isShow) {
                if (isShow == 1) {
                    showCardDialog();
                } else {
                    if (cardShow) showCardDialog();
                }
            }
        });
    }

    private void showCardDialog() {
        CustomDialog.showTwoButtonDialog(getView(), "您还没有绑定银行卡,是否绑定银行卡!", "确定", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                getView().addBank();
            }
        });
    }

    //待办事项列表
    public void refreshMyTask(boolean show) {
        getData(2, model.getParam(getTaskParam(), "ToDoWorkInfo"), show);
    }

    //检查银行卡
    public void checkHasBank() {
        getData(1, model.getParam(getListParam(), "CheckBankCardBinded"), false);
    }

    //头像和银行卡参数
    private List<Object> getListParam() {
        List<Object> list = new ArrayList<>();
        list.add(Hawk.get("UserID"));
        return list;
    }

    //待办事项参数
    private List<Object> getTaskParam() {
        List<Object> list = new ArrayList<>();
        list.add(1);
        return list;
    }

    //金账户
    public void hasGoldAccount() {
        List<Object> list = new ArrayList<>();
        list.add(Hawk.get("UserID", ""));
        list.add(Hawk.get("CompanyID", ""));
        getData(3, model.getAccountParam(list), true);
    }

    //获取消息列表
    private List<Object> getMessageParam() {
        List<Object> list = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("Receiver", Hawk.get("UserID"));
        list.add(JSON.toJSONString(map));
        return list;
    }

    @Override
    protected void onSuccess(int resultCode, ResponseData data) {
        if (isViewAttached()) {
            switch (resultCode) {
                case 0:
                    if (!TextUtils.isEmpty(data.getData())) {
                        JSONObject object = JSON.parseArray(data.getData()).getJSONObject(0);
                        if (object.get("UserHeadPic") != null) {
                            byte[] temp = object.getBytes("UserHeadPic");
                            if (temp.length != 0) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                                if (bitmap != null) {
                                    getView().setPortrait(bitmap);
                                    Utils.setPortraitBitmap(bitmap);
                                    Utils.setBitmapToCache(Constants.PortraitPath + Hawk.get("UserID") + ".png", bitmap);
                                }
                            }
                        }
                    } else {
                        Log.e("my_log", "----pagePresenter111---->" + "头像加载失败！");
                    }
                    break;
                case 1://银行卡
                    if (!TextUtils.isEmpty(data.getReturnString())) {
                        if (data.getReturnString().equals("False")) {
                            RxBus.getInstance().post(Constants.SHOW_BIRTH, 2);
                        }
                    }
                    break;
                case 2:
                    //待办事项
                    if (!TextUtils.isEmpty(data.getReturnString())) {
                        JSONObject object = JSON.parseObject(data.getReturnString());
                        if (!object.isEmpty()) {
                            getView().initRecycle(model.getMyData(getView().getUserPermission()), model.getOtherData(getView().getUserPermission()), model.getTaskData(object));
                        } else {
                            getView().initRecycle(model.getMyData(getView().getUserPermission()), model.getOtherData(getView().getUserPermission()), null);
                        }
                    }
                    break;
                case 3://金账户
                    if (!TextUtils.isEmpty(data.getData())) {
                        MyApplication application = (MyApplication) getView().getApplication();
                        JSONArray jsonArray = JSON.parseArray(data.getData());
                        if (jsonArray != null && jsonArray.size() != 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String userNo = jsonObject.getString("UserNo");
                            if (!TextUtils.isEmpty(userNo)) {
                                if (application.goldRegister == null) {
                                    application.goldRegister = new GoldRegisteredInfo();
                                }
                                application.goldRegister.setUserNo(userNo);
                                application.goldRegister.setCustomerName(jsonObject.getString("CstmNm"));
                                application.goldRegister.setBankName(jsonObject.getString("BankName"));
                                application.goldRegister.setEmail(jsonObject.getString("Email"));
                                application.goldRegister.setTelPhone(jsonObject.getString("PhoneNo"));
                                application.goldRegister.setIDCardNo(jsonObject.getString("IDCardNo"));
                                application.goldRegister.setBankNo(jsonObject.getString("BankCardNo"));
                                Intent intent = new Intent(getView(), GoldAccountActivity.class);
                                getView().startActivity(intent);
                            } else {
                                getView().startActivity(new Intent(getView(), GoldRegistActivity.class));
                            }
                        } else {
                            getView().startActivity(new Intent(getView(), GoldRegistActivity.class));
                        }
                    } else {
                        getView().showMsg("空数据");
                    }
                    break;
                case 4://消息
                    if (!TextUtils.isEmpty(data.getData())) {
                        int messageCount = 0;
                        JSONArray jsonArray = JSON.parseArray(data.getData());
                        if (jsonArray != null && jsonArray.size() != 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                messageCount += jsonObject.getIntValue("NotReadCount");
                            }
                        }
                        getView().setBadgeNumber(messageCount);
                    } else {
                        getView().setBadgeNumber(0);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onFailed(int resultCode, String msg) {
        if (resultCode == 0) {
            Log.e("mcl", "----pagePresenter---->" + "头像加载失败！");
        } else if (resultCode == 2) {
            getView().showMsg("我的任务加载失败");
            Logger.e("==我的任务==> %s", msg);
            getView().initRecycle(model.getMyData(getView().getUserPermission()), model.getOtherData(getView().getUserPermission()), null);
        }
    }

    @Override
    protected void permissionSuccess(int code) {
        if (code == Constants.REQUEST_CODE_PERMISSION_SD) {
            Bitmap bitmap = Utils.getPortraitBitmap();
            if (bitmap != null) {
                getView().setPortrait(bitmap);
            } else {
                getData(0, model.getPortraitParam(getListParam(), "UserInfoManage"), false);
            }
        }
    }

    //上传log文件
    public void upLoadLogFile() {
        final String logName = Hawk.get("LogFile");
        if (!TextUtils.isEmpty(logName)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(logName);
                    if (file.exists()) {
                        try {
                            FileInputStream fis = new FileInputStream(file);
                            postLog(logName, Utils.readStream(fis));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

    }

    private void postLog(final String logName, byte[] bytes) {
        List<Object> list = new ArrayList<>();
        list.add("2");
        String str = JSON.toJSONString(list);
        HttpAction.getInstance().upLoadPortrait(
                new Subscriber<ResponseData>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ResponseData data) {
                        if (data.getExecuteResult()) {
                            if (deleteFile(logName)) {
                                Hawk.remove("LogFile");
                            }
                        }
                    }
                }, Hawk.get("SessionID", ""), getBase64(str), getBase64("UpMobileLog"), getBase64(""), bytes);
    }

    private String getBase64(String str) {
        return Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
    }

    /**
     * 文件删除
     *
     * @param filePath 文件路径
     */
    private boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public void rxDeAttach() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        if (birthSubscription != null && !birthSubscription.isUnsubscribed()) {
            birthSubscription.unsubscribe();
        }
        deAttach();
    }
}
