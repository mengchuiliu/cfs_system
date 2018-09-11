package com.xxjr.cfs_system.LuDan.model;

import com.xiaoxiao.rxjavaandretrofit.ResponseData;

/**
 * Created by Administrator on 2017/7/27.
 * 网络请求返回回调接口
 */

public interface HttpResult {
    void reusltSuccess(ResponseData data);

    void reusltFailed(String msg);
}
