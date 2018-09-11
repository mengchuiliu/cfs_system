package com.xiaoxiao.rxjavaandretrofit;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2017/3/13.
 *
 * @author mengchuiliu
 */

public class HttpAction extends HttpMethods {
    private HttpAction() {
        super();
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpAction ACTION_INSTANCE = new HttpAction();
    }

    //获取单例
    public static HttpAction getInstance() {
        return SingletonHolder.ACTION_INSTANCE;
    }

    //登录接口
    public void login(Subscriber subscriber, final String ParamString, final int count) {
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), ParamString);
                Call<String> responeCall = netService.login("SimulateSync", body);
                useData(responeCall, subscriber, count, "");
            }
        });
        toSubscribe(observable, subscriber);
    }

    //获取数据接口
    public void getData(Subscriber subscriber, final String SessionID, final String ParamString) {
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), ParamString);
                Call<String> responeCall = netService.getData(SessionID, "SimulateSync", body);
                useData(responeCall, subscriber, -1, ParamString);
            }
        });
        toSubscribe(observable, subscriber);
    }

    //上传头像
    public void upLoadPortrait(Subscriber subscriber, final String SessionID, final String OperParam, final String OperName, final String Marker, final byte[] file) {
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/octet-stream"), file);
//                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("image/*"), file);
                Call<String> responeCall = netService.upLoadPortrait(SessionID, OperParam, OperName, Marker, body);
                getFileResult(responeCall, subscriber);
            }
        });
        toSubscribe(observable, subscriber);
    }

    //上传文件
    public void upLoadFile(Subscriber subscriber, final String url, final String ClientType, final String CompanyID, final String OperParam, final byte[] file) {
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/octet-stream"), file);
//                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("image/*"), file);
                Call<String> responeCall = netService.upLoadFile(url, ClientType, CompanyID, OperParam, body);
                getFileResult(responeCall, subscriber);
            }
        });
        toSubscribe(observable, subscriber);
    }

    private void getFileResult(Call<String> responeCall, Subscriber subscriber) {
        try {
            Response<String> response = responeCall.execute();
            ResponseData data = new ResponseData();
            Log.e("my_log", "=== 上传文件 ====>" + response.code());
            if (response.code() == 200) {
                String ExecuteResult = getBase64(response.headers().get("ExecuteResult"));
                String ResultMessage = getBase64(response.headers().get("ResultMessage"));
                Log.e("my_log", "=== 上传文件 ====>" + ExecuteResult + "====" + ResultMessage);
                boolean result = false;
                if (!TextUtils.isEmpty(ExecuteResult)) {
                    result = Boolean.valueOf(ExecuteResult);
                }
                if (!result) {
                    data.setData(ResultMessage);
                }
                data.setExecuteResult(result);
                subscriber.onNext(data);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new ApiException("资料上传失败，请稍后重试!"));
            }
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    private String getBase64(String str) {
        if (TextUtils.isEmpty(str)) return "";
        else return new String(Base64.decode(str, Base64.DEFAULT));
    }
}
