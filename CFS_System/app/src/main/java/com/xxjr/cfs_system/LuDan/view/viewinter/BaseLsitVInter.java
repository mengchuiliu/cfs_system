package com.xxjr.cfs_system.LuDan.view.viewinter;

import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

/**
 * Created by Administrator on 2017/9/6.
 */

public interface BaseLsitVInter extends BaseViewInter {
    int getListType();

    String getSearchContent();

    String getSearchCompany();

    String getChooseTime1();

    String getChooseTime2();

    boolean getPull();

    void refreshChange();

    void completeRefresh();
}
