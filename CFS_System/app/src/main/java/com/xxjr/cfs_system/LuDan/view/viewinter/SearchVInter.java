package com.xxjr.cfs_system.LuDan.view.viewinter;

import android.text.TextWatcher;

import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

import java.util.List;

import entity.ChooseType;

/**
 * Created by Administrator on 2017/8/24.
 * 搜索接口
 */

public interface SearchVInter extends BaseViewInter {

    int getType();//搜索类型

    int getBankID();

    String getAisleType();//代扣平台

    String getCompanyId();

    boolean getSubmit();

    void initRecycler(List<ChooseType> chooseTypes);

    void refreshData(List<ChooseType> listData);

    void setEditChageListener(TextWatcher textWatcher);

    void complete();
}
