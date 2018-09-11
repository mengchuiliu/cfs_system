package com.xiaoxiao.rxjavaandretrofit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Chuiliu Meng on 2016/8/9.
 *
 * @author Chuiliu Meng
 */
public class HttpMethods {
    //    public static final String BASE_URL = "https://www.xxxxjs.cn:2568/DuiService/";//正式库
    public static final String BASE_URL = "http://192.168.31.154:2569/DuiService/";//开发测试
//    public static final String BASE_URL = "https://192.168.31.155:2568/DuiService/";//测试专用

    //    public static final String BASE_URL = "https://192.168.31.24:2568/DuiService/"; //谢
//    public static final String BASE_URL = "http://192.168.31.78:2569/DuiService/";//肖
//    public static final String BASE_URL = "http://192.168.31.52:2569/DuiService/";//谭
//    public static final String BASE_URL = "http://192.168.31.96:2569/DuiService/";//张

    private static final int DEFAULT_TIMEOUT = 30;
    NetService netService;

    //上传地址
//    public static final String UPLOAD_URL = "https://www.xxxxjs.cn:8734";//正式库
    public static final String UPLOAD_URL = "http://192.168.31.154:8733";//开发环境
//    public static final String UPLOAD_URL = "https://192.168.31.155:8734";//测试环境

    //查看图片和文件地址
//    public static final String FileUrl = "http://www.xxxxjs.cn:2569/DuiService/GetFileStream?";//正式库
    public static final String FileUrl = "http://192.168.31.154:2569/DuiService/GetFileStream?";//开发环境
//    public static final String FileUrl = "http://192.168.31.155:2569/DuiService/GetFileStream?";//测试环境

    //金账户地址
//    public static final String GoldUrl = "http://www.xxxxjs.cn:5010/Jzh/";//正式库
    public static final String GoldUrl = "http://192.168.31.165:5010/Jzh/";//测试库

    //广告页地址
//    public static final String AdvertisingUrl = "http://www.xxxxjs.cn:5010/";//正式库
    public static final String AdvertisingUrl = "http://192.168.31.165:5010/";//测试库

    public static final int Login_Repeat_Out = 10000;//重复登录退出

    public HttpMethods() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.protocols(Collections.singletonList(Protocol.HTTP_1_1));

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
//                    return null;
                }
            }}, new SecureRandom());
            builder.sslSocketFactory(sc.getSocketFactory());
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(new NullOnEmptyConverterFactory())//返回string
//                .addConverterFactory(GsonConverterFactory.create())//返回格式化gson
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        netService = retrofit.create(NetService.class);
    }

    <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    //数据处理方法
    void useData(Call<String> responeCall, Subscriber subscriber, int count, String ParamString) {
        try {
            Response<String> response = responeCall.execute();
            if (response.code() == 200) {
                String result = response.body();
                ResponseData responseData = getData(result, count);
                if (responseData != null) {
                    subscriber.onNext(responseData);
                    subscriber.onCompleted();
                } else {
                    login(subscriber, ParamString);
                }
            } else {
                String string = response.errorBody().string();
                subscriber.onError(new ApiException(string));
            }
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    //返回所需要的数据集合
    private ResponseData getData(String result, int count) {
        Logger.e("==http数据返回==> %s", result);
//     {"ExecuteResult":false,"ReturnMsg":"notlogin","ReturnString":null,"ReturnStrings":null,
//     "ReturnBytes":null,"ReturnDataTable":null,"ReturnDataSet":null,"Marker":null,"IsZip":false,"ZipType":null,"UserID":null}
        ResponseData responseData = new ResponseData();
        JSONObject jsonObject = JSONObject.parseObject(result);
        Boolean ExecuteResult;
        ExecuteResult = jsonObject.get("ExecuteResult") != null && jsonObject.getBooleanValue("ExecuteResult");
        String ReturnMsg = jsonObject.getString("ReturnMsg");
        String ReturnString = jsonObject.getString("ReturnString");
        String ReturnStrings = jsonObject.getString("ReturnStrings");
        String Marker = jsonObject.getString("Marker");
        String data = jsonObject.getString("ReturnDataTable");
        JSONObject ReturnDataSet = null;
        if (jsonObject.get("ReturnDataSet") != null) {
            ReturnDataSet = jsonObject.getJSONObject("ReturnDataSet");
        }
        if (ExecuteResult) {
            responseData.setData(data);
            responseData.setReturnString(ReturnString);
            responseData.setReturnStrings(ReturnStrings);
            responseData.setReturnDataSet(ReturnDataSet);
            responseData.setMarker(Marker);
            responseData.setReturnMsg(ReturnMsg);
        } else {
            if (count == 0) {
                responseData.setData(ReturnMsg);
            } else if (count == -1) {
                if (ReturnMsg != null && ReturnMsg.contains("notlogin")) {
                    return null;
                } else if (ReturnMsg != null && ReturnMsg.contains("repeated")) {
                    responseData.setData("repeated");
                } else {
                    responseData.setData(ReturnMsg == null ? "null数据" : ReturnMsg);
                }
            } else if (count == 1) {
                responseData.setData("登录过期，请退出重新登陆后使用!");
            }
        }
        responseData.setExecuteResult(ExecuteResult);
        return responseData;
    }

    private boolean isLogin = false;

    private void login(final Subscriber subscriber, final String ParamString) {
        Logger.e("==二次登录==> %s", ParamString);
        if (!isLogin) {
            isLogin = true;
        } else {
            ResponseData responseData = new ResponseData();
            responseData.setExecuteResult(false);
            responseData.setData("数据加载失败");
            subscriber.onNext(responseData);
            subscriber.onCompleted();
            return;
        }
        String name = Hawk.get("Account");
        String psw = Hawk.get("Psw");
        String RegistrationID = Hawk.get("RegistrationID");
        Map<String, Object> map = new HashMap<>();
        map.put("IsUseZip", false);
        List<Object> list = new ArrayList<>();
        list.add(name);
        list.add(getMD5Str(psw));
        list.add("933f63a173540f09b587fb7f95625bbb");
        list.add(Hawk.get("versionName", "0"));
        list.add("2");
        list.add(RegistrationID);//极光注册唯一标示码
        map.put("ParamString", list);
        map.put("TranName", "UserLogin");
        String str = JSON.toJSONString(map);
        HttpAction.getInstance().login(new Subscriber<ResponseData>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                isLogin = false;
                subscriber.onNext(new ResponseData());
                subscriber.onCompleted();
            }

            @Override
            public void onNext(ResponseData data) {
                if (data.getExecuteResult()) {
                    Hawk.put("SessionID", data.getReturnString());
                    HttpAction.getInstance().getData(new Subscriber<ResponseData>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            isLogin = false;
                            ResponseData responseData = new ResponseData();
                            responseData.setExecuteResult(false);
                            responseData.setData(e.getMessage());
                            subscriber.onNext(responseData);
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onNext(ResponseData responseData) {
                            isLogin = false;
                            subscriber.onNext(responseData);
                            subscriber.onCompleted();
                        }
                    }, data.getReturnString(), ParamString);
                } else {
                    isLogin = false;
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                }
            }
        }, str, 1);
    }

    public class NullOnEmptyConverterFactory extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return new Converter<ResponseBody, Object>() {
                @Override
                public Object convert(ResponseBody body) throws IOException {
                    if (body == null || body.contentLength() == 0) {
                        return null;
                    }
                    return delegate.convert(body);
                }
            };
        }
    }

    public String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (Exception e) {
            System.out.println("MD5 加密异常");
        }
        assert messageDigest != null;
        byte[] byteArray = messageDigest.digest();
        StringBuilder md5StrBuff = new StringBuilder();
        for (byte aByteArray : byteArray) {
            if (Integer.toHexString(0xFF & aByteArray).length() == 1) {
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & aByteArray));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & aByteArray));
            }
        }
        // 16位加密，从第9位到25位
        // return md5StrBuff.substring(8, 24).toString().toUpperCase();
        // 32位大写MD5加密
        return md5StrBuff.toString();
    }
}
