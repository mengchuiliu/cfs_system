package com.xiaoxiao.rxjavaandretrofit;

import java.io.FileInputStream;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/3/13.
 *
 * @author mengchuiliu
 */

public interface NetService {

    @PUT("Tran")
    Call<String> login(@Header("Support") String Support, @Body RequestBody param);//登录接口

    @PUT("Tran")
    Call<String> getData(@Header("SessionID") String SessionID,
                         @Header("Support") String Support,
                         @Body RequestBody param);//数据接口

    //    @Multipart
//    @PUT("StreamTran")
//    Call<String> upLoadFile(@Header("OperParam") String OperParam,
//                            @Header("OperName") String OperName,
//                            @Header("Marker") String Marker,
//                            @Part("file\"; filename=\"image.png") RequestBody image);
    @POST("StreamTran")
    Call<String> upLoadPortrait(@Header("SessionID") String SessionID,
                                @Header("OperParam") String OperParam,
                                @Header("OperName") String OperName,
                                @Header("Marker") String Marker,
                                @Body RequestBody image);

    @POST("")
    Call<String> upLoadFile(@Url String url,
                            @Header("ClientType") String ClientType,
                            @Header("CompanyID") String CompanyID,
                            @Header("OperParam") String OperParam,
                            @Body RequestBody image);

    @GET
    Call<ResponseBody> retrofitDownload(@Url String url);//更新

    @GET("GetSplash")
    Call<String> getAdvertising(@Query("originCode") String originCode, @Query("operateId") String operateId);

//    @GET("GetFileStream")
//    Call<byte[]> getReadFile(@Query("id") String id, @Query("SessionId") String SessionId);
}
