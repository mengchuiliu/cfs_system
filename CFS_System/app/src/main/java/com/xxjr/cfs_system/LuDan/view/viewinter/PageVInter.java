package com.xxjr.cfs_system.LuDan.view.viewinter;

import android.graphics.Bitmap;

import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

import java.util.List;

import entity.CommonItem;
import entity.PageModel;

/**
 * Created by Administrator on 2017/7/31.
 * 主页界面接口
 */

public interface PageVInter extends BaseViewInter {
    void setPortrait(Bitmap bitmap);

    void setUserName(String userName);

    void refreshData(List<CommonItem> commonItems);

    List<CommonItem> getData();

    String getUserPermission();

    void addBank();

    String getRealName();

    String getCompany();

    String getTypeName();

    void setBadgeNumber(int number);
}
