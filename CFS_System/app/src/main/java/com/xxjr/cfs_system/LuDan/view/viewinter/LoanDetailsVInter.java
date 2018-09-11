package com.xxjr.cfs_system.LuDan.view.viewinter;

import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

import java.util.List;

import entity.CommonItem;
import entity.LoanInfo;
import entity.Schedule;

public interface LoanDetailsVInter extends BaseViewInter {
    LoanInfo getLoanInfo();

    void initRecycler(List<CommonItem> commonItems);

    void refreshData(List<CommonItem> commonItems);

    void refreshTime(int pos, String time);

    void refreshSchedule(int pos, List<Schedule> schedules, boolean ishow);

    boolean getIsNotary();

    boolean getIsRedeem();

    void complete();

    void submitComplete();

    void refreshItem(List<LoanInfo> loanInfos);
}
