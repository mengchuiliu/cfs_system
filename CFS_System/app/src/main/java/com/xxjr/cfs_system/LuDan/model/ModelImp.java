package com.xxjr.cfs_system.LuDan.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.xiaoxiao.rxjavaandretrofit.HttpAction;
import com.xiaoxiao.rxjavaandretrofit.HttpMethods;
import com.xiaoxiao.rxjavaandretrofit.ResponseData;
import com.xiaoxiao.rxjavaandretrofit.RxBus;

import java.util.HashMap;
import java.util.List;

import subscribers.ProgressSubscriber;
import subscribers.SubscriberOnNextListener;

/**
 * Created by Administrator on 2017/7/28.
 * 接口处理基类
 */

public class ModelImp implements BaseModelInter {
    @Override
    public void getData(Context context, String SessionID, int resultCode, String ParamString, final HttpResult result, boolean isShow) {
        HttpAction.getInstance().getData(new ProgressSubscriber(new SubscriberOnNextListener<ResponseData>() {
            @Override
            public void onNext(ResponseData data) {
                if (data.getExecuteResult()) {
                    result.reusltSuccess(data);
                } else {
                    if (data.getData().equals("repeated")) {
                        RxBus.getInstance().post(HttpMethods.Login_Repeat_Out, true);//登录互提
                    } else {
                        result.reusltFailed(data.getData());
                    }
                }
            }

            @Override
            public void onError(String msg) {
                result.reusltFailed(msg);
            }
        }, context, isShow), SessionID, ParamString);
    }

    //通用参数组装
    public String getParam(List<Object> list, String tranName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Action", "Default");
        map.put("DBMarker", "DB_CFS_Loan");
        map.put("Marker", "HQServer");
        map.put("IsUseZip", false);
        map.put("Function", "");
        map.put("ParamString", list);
        map.put("TranName", tranName);
        Logger.e("==通用数据参数==> %s", JSON.toJSONString(map));
        return JSON.toJSONString(map);
    }

    //通用参数组装
    public String getMoreParam(List<Object> list, String tranName, String Action) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Action", Action);
        map.put("Function", "");
        map.put("IsUseZip", false);
        map.put("ParamString", list);
        map.put("TranName", tranName);
        Logger.e("==通用数据参数==> %s", JSON.toJSONString(map));
        return JSON.toJSONString(map);
    }
}
