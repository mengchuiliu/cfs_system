package com.xxjr.cfs_system.LuDan.view.viewinter;

import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

/**
 * Created by Administrator on 2017/7/25.
 * 登录界面接口
 */

public interface LoginVInter extends BaseViewInter {
    void showLoading();

    void hideLoading();

    void showUpdatePswDialog();

    void setUserName(String userName);

    void setPassword(String password);

    void setVersionName(String name);

    void complete(String permissions, String typeName, String company, String realName, String UserBirthday, String returnStrings);

    String getUserName();

    String getPassword();
}
