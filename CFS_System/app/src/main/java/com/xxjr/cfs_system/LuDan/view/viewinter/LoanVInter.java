package com.xxjr.cfs_system.LuDan.view.viewinter;

import java.util.List;

import entity.LoanInfo;

/**
 * Created by Administrator on 2017/9/6.
 * 贷款列表
 */

public interface LoanVInter extends BaseLsitVInter {
    List<LoanInfo> getLoanInfos();
}
