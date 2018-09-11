package com.xxjr.cfs_system.LuDan.model;

import android.content.Context;

/**
 * Created by Administrator on 2017/7/25.
 * 数据处理基类
 */

public interface BaseModelInter {
    void getData(Context context, String SessionID, int resultCode, String ParamString, HttpResult result, boolean isShow);
}
