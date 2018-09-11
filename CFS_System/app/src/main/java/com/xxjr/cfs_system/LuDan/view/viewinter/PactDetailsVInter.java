package com.xxjr.cfs_system.LuDan.view.viewinter;

import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

import java.util.List;

import entity.CommonItem;
import entity.Contract;
import entity.LoanInfo;

/**
 * Created by Administrator on 2017/8/25.
 * 合同详情接口类
 */

public interface PactDetailsVInter extends BaseViewInter {
    Contract getContract();

    void initRecycler(List<CommonItem> commonItems);

    void refreshData(int pos, List<LoanInfo> loanInfos);

    void refreshReadData(int pos, String num);

    void setIDs(String ids);

    void setLoanDescription(String loanDescription);
}
