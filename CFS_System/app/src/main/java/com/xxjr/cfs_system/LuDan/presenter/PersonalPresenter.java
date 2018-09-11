package com.xxjr.cfs_system.LuDan.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xiaoxiao.rxjavaandretrofit.RxBus;
import com.xxjr.cfs_system.LuDan.model.modelimp.PersonalMImp;
import com.xxjr.cfs_system.LuDan.view.activitys.PersonalInfoActivity;
import com.xxjr.cfs_system.LuDan.view.viewinter.PersonalVInter;
import com.xxjr.cfs_system.tools.BitmapManage;
import com.xxjr.cfs_system.tools.Constants;
import com.xxjr.cfs_system.tools.Utils;

import java.util.ArrayList;
import java.util.List;

import entity.ChooseType;
import entity.PersonalInfo;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/8/1.
 * 执行类
 */

public class PersonalPresenter extends BasePresenter<PersonalVInter, PersonalMImp> {
    private PersonalInfo info;
    private Subscription subscription, sexSubscription;
    private int sexs = 1;

    @Override
    protected PersonalMImp getModel() {
        return new PersonalMImp();
    }

    @Override
    public void setDefaultValue() {
        info = Hawk.get("PersonalInfo");
        if (info != null) {
            getView().initReacycle(model.getItemList(info));
        } else {
            info = new PersonalInfo();
        }
    }

    public void getChooseData() {
        subscription = RxBus.getInstance().toObservable(Integer.class).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer type) {
                if (type == 2) {
                    getView().showSexPop();
                } else if (type == 3) {
                    getView().showBirthDate();
                }
            }
        });
        sexSubscription = RxBus.getInstance().toObservable(1, ChooseType.class).subscribe(new Action1<ChooseType>() {
            @Override
            public void call(ChooseType s) {
                getView().hidePop();
                ((PersonalInfoActivity) getView()).setText.setText(s.getContent());
                sexs = s.getId();
            }
        });
    }

    public String getSaveParam() {
        return model.getSaveParam(getSaveListParam(), "UserInfoManage");
    }

    public String getPortraitParam() {
        return model.getPortraitParam(getportraitListParam(), "UserInfoManage");
    }

    private List<Object> getSaveListParam() {
        SparseArray<String> sparseArray = getView().getSparseData();
        List<Object> list = new ArrayList<>();
        list.add(Hawk.get("UserID"));
        list.add(sparseArray.get(0));
        list.add(sparseArray.get(1));
        list.add(sparseArray.get(0) + sparseArray.get(1));
        list.add(sexs);
        list.add(sparseArray.get(3));
        list.add(sparseArray.get(7));
        list.add(sparseArray.get(8));
        return list;
    }

    @Override
    protected void onSuccess(int resultCode, ResponseData data) {
        switch (resultCode) {
            case 0:
                saveAndOver();
                break;
            case 1:
                setUserPortrait(data);
                break;
        }

    }

    //保存成功并退出
    private void saveAndOver() {
        SparseArray<String> sparseArray = getView().getSparseData();
        info.setUserSurname(sparseArray.get(0));
        info.setUserName(sparseArray.get(1));
        info.setUserSex(sexs);
        info.setUserBirthday(sparseArray.get(3));
        info.setPhone(sparseArray.get(7));
        info.setPhone1(sparseArray.get(8));
        Hawk.put("PersonalInfo", info);
        Hawk.put("UserRealName", sparseArray.get(0) + sparseArray.get(1));
        getView().showMsg("信息保存成功!");
        getView().over();
    }

    //设置网络头像
    private void setUserPortrait(ResponseData data) {
        if (!TextUtils.isEmpty(data.getData())) {
            JSONObject object = JSON.parseArray(data.getData()).getJSONObject(0);
            if (object.get("UserHeadPic") != null) {
                byte[] temp = object.getBytes("UserHeadPic");
                if (temp.length != 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                    if (bitmap != null) {
                        getView().setPortrait(BitmapManage.toRoundBitmap(bitmap));
                        Utils.setPortraitBitmap(bitmap);
                        Utils.setBitmapToCache(Constants.PortraitPath + Hawk.get("UserID") + ".png", bitmap);
                    }
                }
            }
        } else {
            Log.e("my_log", "----pagePresenter---->" + "头像加载失败！");
        }
    }

    @Override
    protected void onFailed(int resultCode, String msg) {
        getView().showMsg(msg);
    }

    @Override
    protected void permissionSuccess(int code) {
        switch (code) {
            case Constants.REQUEST_CODE_PERMISSION_SD:
                Bitmap bitmap = null;
                if (Utils.getPortraitBitmap() != null) {
                    bitmap = BitmapManage.toRoundBitmap(Utils.getPortraitBitmap());
                }
                if (bitmap != null) {
                    getView().setPortrait(bitmap);
                } else {
                    getData(1, getPortraitParam(), true);
                }
                break;
            case Constants.REQUEST_CODE_PERMISSION_MORE:
                getView().showPortraitPop();
                break;
        }
    }

    private List<Object> getportraitListParam() {
        List<Object> list = new ArrayList<>();
        list.add(Hawk.get("UserID"));
        return list;
    }

    public void rxDeAttach() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        if (sexSubscription != null && !sexSubscription.isUnsubscribed()) {
            sexSubscription.unsubscribe();
        }
    }
}
