package com.xxjr.cfs_system.LuDan.view.viewinter;

import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

import entity.LoanInfo;

public interface RemarkVInter extends BaseViewInter {
    LoanInfo getLoanInfo();

    int getRemarkType();

    String getRemark();

    int getYellowType();

    void setTextContent(String text);

    void complete();
}
