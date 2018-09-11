package com.xxjr.cfs_system.LuDan.view.viewinter;

import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

import java.util.List;

import entity.CommonItem;
import entity.LoanInfo;

public interface TaskDetailsVInter extends BaseViewInter {
    LoanInfo getLoanInfo();

    void initRecycler(List<CommonItem> commonItems);

    void refreshData(List<CommonItem> commonItems);

    void setIvShow(List<CommonItem> commonItems);

    boolean getisUpdateLoan();

    void showPop(View parent, boolean yellow);

    void hidePop();
}
