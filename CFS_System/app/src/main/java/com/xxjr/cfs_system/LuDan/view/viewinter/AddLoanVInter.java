package com.xxjr.cfs_system.LuDan.view.viewinter;

import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

import java.util.List;

import entity.CommonItem;
import entity.LoanInfo;

/**
 * Created by Administrator on 2017/9/1.
 * 添加贷款接口
 */

public interface AddLoanVInter extends BaseViewInter {

    LoanInfo getLoanInfo();

    boolean getScheduleUp();

    void initRecycler(List<CommonItem> commonItems);

    void refreshData(int pos, String text);

    void hidePop();

    boolean getIsUpdate();

    int getSubmit();

    void completeOver(String loanId);
}
