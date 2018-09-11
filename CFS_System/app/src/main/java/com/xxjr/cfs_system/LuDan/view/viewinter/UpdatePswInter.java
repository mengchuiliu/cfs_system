package com.xxjr.cfs_system.LuDan.view.viewinter;

import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

/**
 * Created by Administrator on 2017/7/28.
 * 修改密码界面接口
 */

public interface UpdatePswInter extends BaseViewInter {
    String getOldPsw();

    String getNowPsw();

    String getConfirmPsw();

    void complete();
}
