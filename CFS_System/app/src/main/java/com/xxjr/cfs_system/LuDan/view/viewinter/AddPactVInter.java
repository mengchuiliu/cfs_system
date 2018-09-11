package com.xxjr.cfs_system.LuDan.view.viewinter;

import com.xxjr.cfs_system.LuDan.view.BaseViewInter;

import java.util.List;

import entity.CommonItem;
import entity.Contract;

/**
 * Created by Administrator on 2017/8/9.
 * 新增报单界面接口
 */

public interface AddPactVInter extends BaseViewInter {
    void initReacycle(List<CommonItem> commonItems);

    void refreshData(int position, String content);

    void setCompanyID(String ids);

    Contract getContract();

    void addPactOver();

    void updateOver();

    List getCustomerInfo();
}
